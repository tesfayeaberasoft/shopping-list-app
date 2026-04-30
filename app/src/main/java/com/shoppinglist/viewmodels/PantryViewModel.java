package com.shoppinglist.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.database.entities.PantryItemEntity;
import com.shoppinglist.repository.PantryRepository;
import java.util.List;
import java.util.UUID;

public class PantryViewModel extends AndroidViewModel {
    private final PantryRepository repository;
    private final String userId;

    public PantryViewModel(@NonNull Application application) {
        super(application);
        repository = new PantryRepository(application);
        SessionManager sessionManager = new SessionManager(application);
        if (sessionManager.getUser() != null) {
            userId = sessionManager.getUser().getId();
        } else {
            userId = "default_user";
        }
    }

    public LiveData<List<PantryItemEntity>> getAllPantryItems() {
        return repository.getAllPantryItems(userId);
    }

    public LiveData<List<PantryItemEntity>> getLowStockItems() {
        return repository.getLowStockItems(userId);
    }

    public LiveData<List<PantryItemEntity>> getExpiringSoonItems() {
        return repository.getExpiringSoonItems(userId);
    }

    public LiveData<Integer> getLowStockCount() {
        return repository.getLowStockCount(userId);
    }

    public LiveData<List<PantryItemEntity>> searchPantryItems(String query) {
        return repository.searchPantryItems(userId, query);
    }

    public void createPantryItem(String name, double currentQty, String unit, String category, 
                                double minQty, String location, long expiryDate, String notes) {
        PantryItemEntity item = new PantryItemEntity();
        item.setId(UUID.randomUUID().toString());
        item.setUserId(userId);
        item.setName(name);
        item.setCurrentQuantity(currentQty);
        item.setUnit(unit);
        item.setCategory(category);
        item.setMinimumQuantity(minQty);
        item.setLocation(location);
        item.setExpiryDate(expiryDate);
        item.setNotes(notes);
        long now = System.currentTimeMillis();
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        repository.insert(item);
    }

    public void updatePantryItem(PantryItemEntity item) {
        repository.update(item);
    }

    public void deletePantryItem(PantryItemEntity item) {
        repository.delete(item);
    }

    public void adjustQuantity(PantryItemEntity item, double amount) {
        double newQty = item.getCurrentQuantity() + amount;
        if (newQty < 0) newQty = 0;
        item.setCurrentQuantity(newQty);
        repository.update(item);
    }
}
