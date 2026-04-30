package com.shoppinglist.fragments;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.shoppinglist.utils.VoiceInputHelper;

public class VoiceInputFragment extends DialogFragment {
    private VoiceInputHelper helper;
    private OnVoiceResult listener;

    public interface OnVoiceResult {
        void onVoiceResult(String text);
    }

    public void setListener(OnVoiceResult listener) { this.listener = listener; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new VoiceInputHelper();
        
        // Initialize voice helper with callback
        helper.initialize(this, result -> {
            if (listener != null) {
                listener.onVoiceResult(result);
            }
            dismiss();
        });
        
        // Start voice input immediately
        if (getActivity() != null) {
            helper.startVoiceInput(getActivity());
        }
    }
}