package com.shoppinglist.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.shoppinglist.R;

public class ShareListFragment extends DialogFragment {
    private String shareCode;

    public void setShareCode(String shareCode) { this.shareCode = shareCode; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_share_list, container, false);
        TextView tvCode = v.findViewById(R.id.tv_share_code);
        Button btnCopy = v.findViewById(R.id.btn_copy_code);
        tvCode.setText(getString(R.string.share_code, shareCode));
        btnCopy.setOnClickListener(v1 -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("share code", shareCode);
            clipboard.setPrimaryClip(clip);
            dismiss();
        });
        return v;
    }
}