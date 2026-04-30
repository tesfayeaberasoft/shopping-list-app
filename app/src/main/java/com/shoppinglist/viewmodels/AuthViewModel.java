package com.shoppinglist.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseUser;
import com.shoppinglist.auth.FirebaseAuthManager;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.database.entities.UserEntity;
import com.shoppinglist.repository.LocalRepository;
import com.shoppinglist.models.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {
    private FirebaseAuthManager authManager;
    private SessionManager sessionManager;
    private LocalRepository localRepository;
    private MutableLiveData<Boolean> authSuccess = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authManager = null; // initialized via setActivity() before use
        sessionManager = new SessionManager(application);
        localRepository = new LocalRepository(application);
    }

    public void setActivity(android.app.Activity activity) {
        authManager = new FirebaseAuthManager(activity);
    }

    public LiveData<Boolean> getAuthSuccess() { return authSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void register(String email, String password, String displayName) {
        authManager.registerWithEmail(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser fUser = authManager.getCurrentUser();
                if (fUser != null) {
                    User user = new User(fUser.getUid(), email, displayName, null);
                    sessionManager.saveUser(user);
                    executor.execute(() -> localRepository.insertUser(
                            new UserEntity(user.getId(), user.getEmail(), user.getDisplayName(), null)));
                    authSuccess.setValue(true);
                }
            } else {
                errorMessage.setValue(
                        task.getException() != null ? task.getException().getMessage() : "Registration failed");
            }
        });
    }

    public void login(String email, String password) {
        authManager.loginWithEmail(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser fUser = authManager.getCurrentUser();
                if (fUser != null) {
                    User user = new User(fUser.getUid(), email, fUser.getDisplayName(), null);
                    sessionManager.saveUser(user);
                    authSuccess.setValue(true);
                }
            } else {
                errorMessage.setValue(
                        task.getException() != null ? task.getException().getMessage() : "Login failed");
            }
        });
    }

    public void handleGoogleSignInResult() {
        // Google sign-in result is handled directly in AuthActivity via onActivityResult.
    }

    public void signOut() {
        if (authManager != null) authManager.signOut();
        sessionManager.clearSession();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
