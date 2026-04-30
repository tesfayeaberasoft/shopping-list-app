package com.shoppinglist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.shoppinglist.R;
import com.shoppinglist.utils.BarcodeScannerHelper;

public class BarcodeScannerFragment extends BottomSheetDialogFragment {
    private PreviewView previewView;
    private BarcodeScannerHelper scannerHelper;
    private OnBarcodeScannedListener listener;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    public interface OnBarcodeScannedListener {
        void onBarcodeScanned(String barcode, String format);
    }

    public void setListener(OnBarcodeScannedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Theme_ShoppingListApp);
        
        // Register camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startScanning();
                    } else {
                        Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barcode_scanner, container, false);
        
        previewView = view.findViewById(R.id.preview_view);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        
        scannerHelper = new BarcodeScannerHelper(this, previewView);
        scannerHelper.setListener(new BarcodeScannerHelper.OnBarcodeScannedListener() {
            @Override
            public void onBarcodeScanned(String barcode, String format) {
                if (listener != null) {
                    listener.onBarcodeScanned(barcode, format);
                }
                dismiss();
            }

            @Override
            public void onScanError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCancel.setOnClickListener(v -> dismiss());
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Check permission and start scanning
        if (scannerHelper.checkCameraPermission()) {
            startScanning();
        } else {
            scannerHelper.requestCameraPermission(cameraPermissionLauncher);
        }
    }

    private void startScanning() {
        scannerHelper.startScanning(getViewLifecycleOwner());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scannerHelper != null) {
            scannerHelper.shutdown();
        }
    }
}
