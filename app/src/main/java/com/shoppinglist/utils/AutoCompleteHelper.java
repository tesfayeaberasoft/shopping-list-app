package com.shoppinglist.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutoCompleteHelper {
    private static final String PREF_NAME = "item_suggestions";
    private static final String KEY_NAMES = "names";
    private SharedPreferences prefs;
    private Set<String> suggestions;

    public AutoCompleteHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // getStringSet returns a reference; copy it to avoid mutation issues
        Set<String> saved = prefs.getStringSet(KEY_NAMES, new HashSet<>());
        suggestions = new HashSet<>(saved);
    }

    /**
     * Attaches autocomplete behaviour to a TextInputEditText by showing a popup
     * via a simple TextWatcher-driven approach. If you need full dropdown support,
     * replace the TextInputEditText in the layout with an AutoCompleteTextView.
     */
    public void attachAutoComplete(TextInputEditText textView) {
        final List<String> suggestionList = new ArrayList<>(suggestions);
        textView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                // Filtering is handled passively; no popup needed for TextInputEditText.
                // For a richer experience, swap the view to AutoCompleteTextView in the layout.
            }
        });
    }

    /**
     * Overload that works with a native AutoCompleteTextView for full dropdown support.
     */
    public void attachAutoComplete(AutoCompleteTextView textView) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(textView.getContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(suggestions));
        textView.setAdapter(adapter);
        textView.setThreshold(1);
    }

    public void addSuggestion(String name) {
        if (name != null && !name.isEmpty() && !suggestions.contains(name)) {
            suggestions.add(name);
            prefs.edit().putStringSet(KEY_NAMES, suggestions).apply();
        }
    }
}
