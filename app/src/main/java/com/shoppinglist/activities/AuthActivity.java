package com.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseUser;
import com.shoppinglist.R;
import com.shoppinglist.auth.FirebaseAuthManager;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.utils.LocaleHelper;
import com.shoppinglist.viewmodels.AuthViewModel;
import com.shoppinglist.fragments.LoginFragment;
import com.shoppinglist.fragments.RegisterFragment;

public class AuthActivity extends AppCompatActivity implements LoginFragment.OnLoginListener, RegisterFragment.OnRegisterListener {
    private static final int RC_SIGN_IN = 9001;
    private AuthViewModel authViewModel;
    private FirebaseAuthManager firebaseAuthManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Apply saved locale to this activity's context
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.setActivity(this);
        firebaseAuthManager = new FirebaseAuthManager(this);

        // Check if we should show register fragment
        boolean showRegister = getIntent().getBooleanExtra(WelcomeActivity.EXTRA_SHOW_REGISTER, false);
        
        if (showRegister) {
            showRegisterFragment();
        } else {
            showLoginFragment();
        }

        findViewById(R.id.google_sign_in_button).setOnClickListener(v -> {
            Intent signInIntent = firebaseAuthManager.getGoogleSignInClient().getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void showLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    public void showRegisterFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLogin(String email, String password) {
        authViewModel.login(email, password);
        
        // Observe login success
        authViewModel.getAuthSuccess().observe(this, success -> {
            if (success != null && success) {
                startMain();
            }
        });
        
        // Observe login errors
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, getString(R.string.login_failed, error), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRegister(String email, String password, String displayName) {
        authViewModel.register(email, password, displayName);
        
        // Observe registration success
        authViewModel.getAuthSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show();
                // Go back to login fragment
                getSupportFragmentManager().popBackStack();
            }
        });
        
        // Observe registration errors
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, getString(R.string.registration_failed, error), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onGoToRegister() {
        showRegisterFragment();
    }

    @Override
    public void onGoToLogin() {
        // Pop back to login
        getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthManager.signInWithGoogle(account).addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                            FirebaseUser user = task.getResult().getUser();
                            SessionManager session = new SessionManager(this);
                            session.saveUser(new com.shoppinglist.models.User(
                                    user.getUid(), 
                                    user.getEmail(), 
                                    user.getDisplayName(), 
                                    user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null));
                            Toast.makeText(this, R.string.google_sign_in_success, Toast.LENGTH_SHORT).show();
                            startMain();
                        } else {
                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(this, getString(R.string.google_sign_in_failed, errorMsg), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(this, R.string.google_sign_in_cancelled, Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                String errorMsg = "Error code: " + e.getStatusCode();
                if (e.getStatusCode() == 12501) {
                    errorMsg = getString(R.string.google_sign_in_cancelled);
                } else if (e.getStatusCode() == 10) {
                    errorMsg = getString(R.string.google_sign_in_config_error);
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}