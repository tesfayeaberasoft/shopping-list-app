package com.shoppinglist.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ShoppingItemsViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String listId;

    public ShoppingItemsViewModelFactory(Application application, String listId) {
        this.application = application;
        this.listId = listId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShoppingItemsViewModel.class)) {
            return (T) new ShoppingItemsViewModel(application, listId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
