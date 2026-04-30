package com.shoppinglist.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BarcodeScannerHelper {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    
    private Fragment fragment;
    private PreviewView previewView;
    private ProcessCameraProvider cameraProvider;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private OnBarcodeScannedListener listener;
    private boolean isScanning = true;

    public interface OnBarcodeScannedListener {
        void onBarcodeScanned(String barcode, String format);
        void onScanError(String error);
    }

    public BarcodeScannerHelper(Fragment fragment, PreviewView previewView) {
        this.fragment = fragment;
        this.previewView = previewView;
        this.cameraExecutor = Executors.newSingleThreadExecutor();
        
        // Configure barcode scanner for common product barcodes
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E,
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_CODE_39,
                        Barcode.FORMAT_QR_CODE
                )
                .build();
        
        this.scanner = BarcodeScanning.getClient(options);
    }

    public void setListener(OnBarcodeScannedListener listener) {
        this.listener = listener;
    }

    public boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCameraPermission(ActivityResultLauncher<String> permissionLauncher) {
        permissionLauncher.launch(Manifest.permission.CAMERA);
    }

    public void startScanning(LifecycleOwner lifecycleOwner) {
        if (!checkCameraPermission()) {
            if (listener != null) {
                listener.onScanError("Camera permission not granted");
            }
            return;
        }

        isScanning = true;
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
                ProcessCameraProvider.getInstance(fragment.requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(lifecycleOwner);
            } catch (ExecutionException | InterruptedException e) {
                if (listener != null) {
                    listener.onScanError("Camera initialization failed: " + e.getMessage());
                }
            }
        }, ContextCompat.getMainExecutor(fragment.requireContext()));
    }

    private void bindCameraUseCases(LifecycleOwner lifecycleOwner) {
        // Preview
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image analysis for barcode scanning
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            if (isScanning) {
                processImageProxy(imageProxy);
            } else {
                imageProxy.close();
            }
        });

        // Camera selector (back camera)
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
            // Unbind all use cases before rebinding
            cameraProvider.unbindAll();

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
            );
        } catch (Exception e) {
            if (listener != null) {
                listener.onScanError("Camera binding failed: " + e.getMessage());
            }
        }
    }

    @androidx.camera.core.ExperimentalGetImage
    private void processImageProxy(ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null && !rawValue.isEmpty() && isScanning) {
                            isScanning = false; // Stop scanning after first successful scan
                            String format = getBarcodeFormatName(barcode.getFormat());
                            if (listener != null) {
                                listener.onBarcodeScanned(rawValue, format);
                            }
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onScanError("Scan failed: " + e.getMessage());
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private String getBarcodeFormatName(int format) {
        switch (format) {
            case Barcode.FORMAT_EAN_13: return "EAN-13";
            case Barcode.FORMAT_EAN_8: return "EAN-8";
            case Barcode.FORMAT_UPC_A: return "UPC-A";
            case Barcode.FORMAT_UPC_E: return "UPC-E";
            case Barcode.FORMAT_CODE_128: return "CODE-128";
            case Barcode.FORMAT_CODE_39: return "CODE-39";
            case Barcode.FORMAT_QR_CODE: return "QR Code";
            default: return "Unknown";
        }
    }

    public void stopScanning() {
        isScanning = false;
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }

    public void shutdown() {
        stopScanning();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (scanner != null) {
            scanner.close();
        }
    }

    public void resumeScanning() {
        isScanning = true;
    }
}
