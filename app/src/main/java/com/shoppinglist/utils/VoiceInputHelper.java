package com.shoppinglist.utils;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Locale;

public class VoiceInputHelper {
    private VoiceResultCallback callback;
    private ActivityResultLauncher<Intent> voiceLauncher;

    public interface VoiceResultCallback {
        void onResult(String text);
    }

    /**
     * Initialize voice input for a Fragment
     * Call this in onCreate or onCreateView
     */
    public void initialize(Fragment fragment, VoiceResultCallback callback) {
        this.callback = callback;
        this.voiceLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ArrayList<String> matches = result.getData()
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (matches != null && !matches.isEmpty()) {
                            String spokenText = matches.get(0);
                            if (callback != null) {
                                callback.onResult(spokenText);
                            }
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // User cancelled voice input
                        if (fragment.getContext() != null) {
                            Toast.makeText(fragment.getContext(), "Voice input cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    /**
     * Start voice recognition
     */
    public void startVoiceInput(Activity activity) {
        if (voiceLauncher == null) {
            Toast.makeText(activity, "Voice input not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the item name...");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        
        try {
            voiceLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "Voice input not available on this device", Toast.LENGTH_SHORT).show();
        }
    }
}