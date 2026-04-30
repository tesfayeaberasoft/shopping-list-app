package com.shoppinglist.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccessibilityHelper {
    public static void setMinTouchSize(View view, int minDp) {
        // Not necessary if using dimens; just ensure in XML.
    }

    public static void setContentDescription(View view, String description) {
        view.setContentDescription(description);
    }
}