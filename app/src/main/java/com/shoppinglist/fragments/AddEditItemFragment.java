/**
 * AddEditItemFragment - Shopping Item Management
 * Handles adding and editing shopping items with categories, quantities, and priorities
 */
package com.shoppinglist.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.shoppinglist.R;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import com.shoppinglist.utils.AutoCompleteHelper;
import com.shoppinglist.utils.VoiceInputHelper;

public class AddEditItemFragment extends DialogFragment {
    private TextInputEditText etName, etQuantity, etUnit, etNote, etPrice;
    private Spinner spinnerCategory, spinnerPriority;
    private MaterialButton btnSave, btnVoice, btnScanBarcode;
    private AutoCompleteHelper autoCompleteHelper;
    private VoiceInputHelper voiceInputHelper;
    private OnItemSavedListener listener;
    private ShoppingItemEntity existingItem; // for editing
    private String scannedBarcode = null;

    public interface OnItemSavedListener {
        void onItemSaved(String name, int qty, String unit, String category, int priority, String note, double price, ShoppingItemEntity existingItem);
    }

    public void setListener(OnItemSavedListener listener) { this.listener = listener; }
    public void setExistingItem(ShoppingItemEntity item) { this.existingItem = item; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Theme_ShoppingListApp);
        autoCompleteHelper = new AutoCompleteHelper(getContext());
        voiceInputHelper = new VoiceInputHelper();
        
        // Initialize voice input with callback
        voiceInputHelper.initialize(this, result -> {
            if (result != null && !result.isEmpty()) {
                // Set the spoken text to the item name field
                if (etName != null) {
                    etName.setText(result);
                    etName.setSelection(result.length()); // Move cursor to end
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_edit_item, container, false);
        etName = v.findViewById(R.id.et_name);
        etQuantity = v.findViewById(R.id.et_quantity);
        etUnit = v.findViewById(R.id.et_unit);
        etNote = v.findViewById(R.id.et_note);
        etPrice = v.findViewById(R.id.et_price);
        spinnerCategory = v.findViewById(R.id.spinner_category);
        spinnerPriority = v.findViewById(R.id.spinner_priority);
        btnSave = v.findViewById(R.id.btn_save);
        btnVoice = v.findViewById(R.id.btn_voice);
        btnScanBarcode = v.findViewById(R.id.btn_scan_barcode);

        // Setup category spinner
        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categories, android.R.layout.simple_spinner_item);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        // Priority spinner
        ArrayAdapter<CharSequence> prioAdapter = ArrayAdapter.createFromResource(getContext(), R.array.priorities, android.R.layout.simple_spinner_item);
        prioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(prioAdapter);

        // Auto-complete
        autoCompleteHelper.attachAutoComplete(etName);

        if (existingItem != null) {
            etName.setText(existingItem.getName());
            etQuantity.setText(String.valueOf(existingItem.getQuantity()));
            etUnit.setText(existingItem.getUnit());
            etNote.setText(existingItem.getNote());
            if (existingItem.getPrice() > 0) {
                etPrice.setText(String.valueOf(existingItem.getPrice()));
            }
            // set spinner selections
            setSpinnerSelection(spinnerCategory, existingItem.getCategory());
            setSpinnerSelection(spinnerPriority, existingItem.getPriority() == 1 ? getString(R.string.high) : existingItem.getPriority() == 2 ? getString(R.string.medium) : getString(R.string.low));
        }

        btnSave.setOnClickListener(view -> saveItem());
        btnVoice.setOnClickListener(view -> voiceInputHelper.startVoiceInput(getActivity()));
        btnScanBarcode.setOnClickListener(view -> showBarcodeScanner());

        return v;
    }

    private void showBarcodeScanner() {
        BarcodeScannerFragment scannerFragment = new BarcodeScannerFragment();
        scannerFragment.setListener((barcode, format) -> {
            scannedBarcode = barcode;
            // Auto-fill item name with barcode
            etName.setText("Product " + barcode);
            etNote.setText("Barcode: " + barcode + " (" + format + ")");
            android.widget.Toast.makeText(getContext(), 
                "Scanned: " + barcode + " (" + format + ")", 
                android.widget.Toast.LENGTH_SHORT).show();
        });
        scannerFragment.show(getParentFragmentManager(), "barcode_scanner");
    }

    // Remove onActivityResult - no longer needed with new API

    private void saveItem() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) { etName.setError("Required"); return; }
        int qty = 1;
        try { qty = Integer.parseInt(etQuantity.getText().toString().trim()); } catch (NumberFormatException e) {}
        String unit = etUnit.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String prioText = spinnerPriority.getSelectedItem().toString();
        int priority = prioText.equals(getString(R.string.high)) ? 1 : prioText.equals(getString(R.string.medium)) ? 2 : 3;
        String note = etNote.getText().toString().trim();
        double price = 0.0;
        try { 
            String priceText = etPrice.getText().toString().trim();
            if (!priceText.isEmpty()) {
                price = Double.parseDouble(priceText);
            }
        } catch (NumberFormatException e) {}
        if (listener != null) {
            listener.onItemSaved(name, qty, unit, category, priority, note, price, existingItem);
        }
        autoCompleteHelper.addSuggestion(name);
        dismiss();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}