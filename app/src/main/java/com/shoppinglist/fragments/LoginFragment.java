package com.shoppinglist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.shoppinglist.R;

public class LoginFragment extends Fragment {
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private OnLoginListener listener;

    public interface OnLoginListener {
        void onLogin(String email, String password);
        void onGoToRegister();
    }

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginListener) listener = (OnLoginListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        etEmail = v.findViewById(R.id.et_email);
        etPassword = v.findViewById(R.id.et_password);
        btnLogin = v.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v1 -> {
            if (listener != null) listener.onLogin(etEmail.getText().toString(), etPassword.getText().toString());
        });
        v.findViewById(R.id.tv_goto_register).setOnClickListener(v2 -> {
            if (listener != null) listener.onGoToRegister();
        });
        return v;
    }
}