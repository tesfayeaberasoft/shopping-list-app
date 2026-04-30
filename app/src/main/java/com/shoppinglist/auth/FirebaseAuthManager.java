package com.shoppinglist.auth;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import com.shoppinglist.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthManager {
    private static final String TAG = "FirebaseAuthManager";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    public FirebaseAuthManager(Activity activity) {
        mAuth = FirebaseAuth.getInstance();
        
        try {
            // Get the web client ID from resources
            String webClientId = activity.getString(R.string.default_web_client_id);
            Log.d(TAG, "Web Client ID loaded: " + (webClientId != null && !webClientId.isEmpty()));
            
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .requestProfile()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Google Sign-In: " + e.getMessage(), e);
            // Create a basic client without ID token (will fail at sign-in but won't crash)
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        }
    }

    public Task<AuthResult> registerWithEmail(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> loginWithEmail(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signInWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        return mAuth.signInWithCredential(credential);
    }

    public Task<Void> sendPasswordResetEmail(String email) {
        return mAuth.sendPasswordResetEmail(email);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void signOut() {
        mAuth.signOut();
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut();
        }
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }
}
