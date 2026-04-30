package com.shoppinglist.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shoppinglist.R;

public class RegisterFragment extends Fragment {
    private TextInputEditText etDisplayName, etEmail, etPassword;
    private TextInputLayout tilDisplayName, tilEmail, tilPassword;
    private MaterialButton btnRegister;
    private OnRegisterListener listener;

    public interface OnRegisterListener {
        void onRegister(String email, String password, String displayName);
        void onGoToLogin();
    }

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterListener) listener = (OnRegisterListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        
        // Initialize views
        etDisplayName = v.findViewById(R.id.et_display_name);
        etEmail = v.findViewById(R.id.et_email);
        etPassword = v.findViewById(R.id.et_password);
        tilDisplayName = v.findViewById(R.id.til_display_name);
        tilEmail = v.findViewById(R.id.til_email);
        tilPassword = v.findViewById(R.id.til_password);
        btnRegister = v.findViewById(R.id.btn_register);
        
        btnRegister.setOnClickListener(v1 -> validateAndRegister());
        
        v.findViewById(R.id.tv_goto_login).setOnClickListener(v2 -> {
            if (listener != null) listener.onGoToLogin();
        });
        
        return v;
    }

    private void validateAndRegister() {
        // Clear previous errors
        tilDisplayName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        String displayName = etDisplayName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        boolean isValid = true;

        // Validate display name
        if (TextUtils.isEmpty(displayName)) {
            tilDisplayName.setError(getString(R.string.error_name_required));
            isValid = false;
        } else if (displayName.length() < 2) {
            tilDisplayName.setError(getString(R.string.error_name_too_short));
            isValid = false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_email_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        // Validate password strength
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_too_short));
            isValid = false;
        } else if (!isPasswordStrong(password)) {
            tilPassword.setError(getString(R.string.error_password_weak));
            isValid = false;
        }

        // If all validations pass, proceed with registration
        if (isValid && listener != null) {
            listener.onRegister(email, password, displayName);
        }
    }

    /**
     * Check if password is strong enough
     * Requirements: At least 6 characters, contains at least one letter and one number
     */
    private boolean isPasswordStrong(String password) {
        if (password.length() < 6) {
            return false;
        }
        
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (hasLetter && hasDigit) {
                return true;
            }
        }
        
        return hasLetter && hasDigit;
    }
}
