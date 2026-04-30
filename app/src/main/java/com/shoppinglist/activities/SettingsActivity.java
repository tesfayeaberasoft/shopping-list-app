package com.shoppinglist.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shoppinglist.R;
import com.shoppinglist.auth.FirebaseAuthManager;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.models.User;
import com.shoppinglist.repository.CloudRepository;
import com.shoppinglist.utils.ExportImportHelper;
import com.shoppinglist.utils.LocaleHelper;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int IMPORT_DATA = 3;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private CircleImageView ivProfile;
    private TextInputEditText etDisplayName, etEmail, etPassword;
    private TextInputLayout tilDisplayName, tilEmail, tilPassword;
    private MaterialButton btnChangePhoto, btnSave, btnExport, btnImport, btnLogout;
    private RadioGroup rgLanguage, rgTheme;
    private SessionManager sessionManager;
    private CloudRepository cloudRepository;
    private FirebaseAuthManager authManager;
    private Uri selectedImageUri;
    private Uri photoUri;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Apply saved locale to this activity's context
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sessionManager = new SessionManager(this);
        cloudRepository = new CloudRepository();
        authManager = new FirebaseAuthManager(this);

        ivProfile = findViewById(R.id.iv_profile_photo);
        etDisplayName = findViewById(R.id.et_display_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tilDisplayName = findViewById(R.id.til_display_name);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        btnChangePhoto = findViewById(R.id.btn_change_photo);
        btnSave = findViewById(R.id.btn_save_profile);
        btnExport = findViewById(R.id.btn_export);
        btnImport = findViewById(R.id.btn_import);
        btnLogout = findViewById(R.id.btn_logout);
        rgLanguage = findViewById(R.id.rg_language);

        User user = sessionManager.getUser();
        if (user != null) {
            etDisplayName.setText(user.getDisplayName());
            etEmail.setText(user.getEmail());
            if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                loadProfilePhoto(user.getPhotoUrl());
            }
        }

        btnChangePhoto.setOnClickListener(v -> showPhotoOptions());

        btnSave.setOnClickListener(v -> saveProfile());

        btnExport.setOnClickListener(v -> new ExportImportHelper(this).exportData());
        btnImport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            startActivityForResult(intent, IMPORT_DATA);
        });

        btnLogout.setOnClickListener(v -> {
            authManager.signOut();
            sessionManager.clearSession();
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Load saved language preference FIRST (before setting listener)
        String savedLanguage = LocaleHelper.getSavedLanguage(this);
        if (savedLanguage.equals("am")) {
            rgLanguage.check(R.id.rb_amharic);
        } else {
            rgLanguage.check(R.id.rb_english);
        }
        
        // Language preference - Set listener AFTER loading saved state
        rgLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            String languageCode;
            if (checkedId == R.id.rb_english) {
                languageCode = "en";
            } else if (checkedId == R.id.rb_amharic) {
                languageCode = "am";
            } else {
                languageCode = "en"; // Default to English
            }
            
            // Only restart if language actually changed
            String currentLanguage = LocaleHelper.getSavedLanguage(this);
            if (!languageCode.equals(currentLanguage)) {
                // Save language preference using LocaleHelper
                LocaleHelper.setLocale(this, languageCode);
                
                // Restart app from MainActivity to apply language everywhere
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        
        // Theme preference
        rgTheme = findViewById(R.id.rg_theme);
        
        // Load saved theme preference FIRST (before setting listener)
        android.content.SharedPreferences prefsTheme = getSharedPreferences("app_preferences", MODE_PRIVATE);
        int savedTheme = prefsTheme.getInt("theme_mode", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        
        // Set the correct radio button based on saved theme
        if (savedTheme == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO) {
            rgTheme.check(R.id.rb_light);
        } else if (savedTheme == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES) {
            rgTheme.check(R.id.rb_dark);
        } else {
            rgTheme.check(R.id.rb_system);
        }
        
        // Set listener AFTER loading saved state
        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int themeMode;
            if (checkedId == R.id.rb_light) {
                themeMode = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.rb_dark) {
                themeMode = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                themeMode = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }
            
            // Only restart if theme actually changed
            int currentTheme = prefsTheme.getInt("theme_mode", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            if (themeMode != currentTheme) {
                // Save preference
                prefsTheme.edit().putInt("theme_mode", themeMode).apply();
                
                // Apply theme immediately
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(themeMode);
                
                // Recreate all activities by restarting the app from MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    
    private void showPhotoOptions() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_photo_source);
        String[] options = {getString(R.string.take_photo), getString(R.string.choose_from_gallery)};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Take photo - check camera permission first
                if (checkCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            } else {
                // Choose from gallery - check storage permission first
                if (checkStoragePermission()) {
                    openGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });
        builder.show();
    }
    
    private boolean checkCameraPermission() {
        return androidx.core.content.ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestCameraPermission() {
        androidx.core.app.ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE);
    }
    
    private boolean checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        } else {
            return androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        }
    }
    
    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                    STORAGE_PERMISSION_CODE);
        } else {
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }
    
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                java.io.File photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = androidx.core.content.FileProvider.getUriForFile(this,
                            getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, TAKE_PHOTO);
                }
            } catch (Exception e) {
                android.util.Log.e("Camera", "Error opening camera: " + e.getMessage(), e);
                Toast.makeText(this, R.string.camera_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions,
                                          @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private java.io.File createImageFile() throws java.io.IOException {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        java.io.File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return java.io.File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void saveProfile() {
        String name = etDisplayName.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();
        
        // Validate name
        if (TextUtils.isEmpty(name)) {
            tilDisplayName.setError(getString(R.string.error_name_required));
            return;
        }
        tilDisplayName.setError(null);
        
        User currentUser = sessionManager.getUser();
        if (currentUser == null) return;
        
        // Update display name
        FirebaseUser firebaseUser = authManager.getCurrentUser();
        if (firebaseUser != null) {
            com.google.firebase.auth.UserProfileChangeRequest profileUpdates = 
                new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            
            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sessionManager.updateProfile(name, currentUser.getPhotoUrl());
                    cloudRepository.updateUserProfile(new com.shoppinglist.database.entities.UserEntity(
                            currentUser.getId(), currentUser.getEmail(), name, currentUser.getPhotoUrl()));
                    Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        // Update password if provided
        if (!TextUtils.isEmpty(newPassword)) {
            if (newPassword.length() < 6) {
                tilPassword.setError(getString(R.string.error_password_too_short));
                return;
            }
            tilPassword.setError(null);
            
            if (firebaseUser != null) {
                firebaseUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, R.string.password_updated, Toast.LENGTH_SHORT).show();
                        etPassword.setText("");
                    } else {
                        Toast.makeText(this, getString(R.string.password_update_failed, 
                            task.getException() != null ? task.getException().getMessage() : "Unknown error"), 
                            Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                selectedImageUri = data.getData();
                uploadPhoto(selectedImageUri);
            } else if (requestCode == TAKE_PHOTO) {
                selectedImageUri = photoUri;
                uploadPhoto(selectedImageUri);
            } else if (requestCode == IMPORT_DATA && data != null) {
                Uri uri = data.getData();
                new ExportImportHelper(this).importData(uri);
            }
        }
    }
    
    private void uploadPhoto(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, R.string.photo_upload_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        
        User currentUser = sessionManager.getUser();
        if (currentUser == null) {
            Toast.makeText(this, R.string.photo_upload_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.uploading_photo));
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Display image immediately
        ivProfile.setImageURI(uri);
        
        // Try Firebase Storage first, fallback to local storage
        try {
            cloudRepository.uploadProfilePhoto(currentUser.getId(), uri)
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage(getString(R.string.uploading_photo) + " " + (int) progress + "%");
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get download URL after successful upload
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(downloadUri -> {
                                    String photoUrl = downloadUri.toString();
                                    sessionManager.updateProfile(currentUser.getDisplayName(), photoUrl);
                                    cloudRepository.updateUserProfile(new com.shoppinglist.database.entities.UserEntity(
                                            currentUser.getId(), currentUser.getEmail(),
                                            currentUser.getDisplayName(), photoUrl));
                                    progressDialog.dismiss();
                                    Toast.makeText(this, R.string.photo_uploaded, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    // Fallback to local storage
                                    savePhotoLocally(uri, currentUser);
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        android.util.Log.e("PhotoUpload", "Firebase upload failed: " + e.getMessage(), e);
                        // Fallback to local storage
                        savePhotoLocally(uri, currentUser);
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            android.util.Log.e("PhotoUpload", "Upload exception: " + e.getMessage(), e);
            // Fallback to local storage
            savePhotoLocally(uri, currentUser);
        }
    }
    
    private void savePhotoLocally(Uri uri, User currentUser) {
        // Save photo to local storage as fallback
        String localPath = com.shoppinglist.utils.LocalPhotoManager.savePhotoLocally(this, currentUser.getId(), uri);
        if (localPath != null) {
            sessionManager.updateProfile(currentUser.getDisplayName(), localPath);
            cloudRepository.updateUserProfile(new com.shoppinglist.database.entities.UserEntity(
                    currentUser.getId(), currentUser.getEmail(),
                    currentUser.getDisplayName(), localPath));
            Toast.makeText(this, R.string.photo_saved_locally, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.photo_upload_failed, Toast.LENGTH_LONG).show();
            // Reload original photo if save fails
            if (currentUser.getPhotoUrl() != null && !currentUser.getPhotoUrl().isEmpty()) {
                loadProfilePhoto(currentUser.getPhotoUrl());
            } else {
                ivProfile.setImageResource(R.drawable.ic_user_placeholder);
            }
        }
    }
    
    private void loadProfilePhoto(String photoUrl) {
        if (photoUrl == null || photoUrl.isEmpty()) {
            ivProfile.setImageResource(R.drawable.ic_user_placeholder);
            return;
        }
        
        // Check if it's a local file path or URL
        if (photoUrl.startsWith("http://") || photoUrl.startsWith("https://")) {
            // Load from URL (Firebase Storage)
            Glide.with(this).load(photoUrl).placeholder(R.drawable.ic_user_placeholder).into(ivProfile);
        } else {
            // Load from local file
            java.io.File photoFile = new java.io.File(photoUrl);
            if (photoFile.exists()) {
                Glide.with(this).load(photoFile).placeholder(R.drawable.ic_user_placeholder).into(ivProfile);
            } else {
                ivProfile.setImageResource(R.drawable.ic_user_placeholder);
            }
        }
    }
}
