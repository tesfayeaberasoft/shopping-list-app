package com.shoppinglist.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.shoppinglist.database.AppDatabase;
import com.shoppinglist.database.dao.PantryItemDao;
import com.shoppinglist.database.entities.PantryItemEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PantryRepository {
    private PantryItemDao pantryItemDao;
    private ExecutorService executorService;

    public PantryRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        pantryItemDao = database.pantryItemDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert pantry item
    public void insert(PantryItemEntity item) {
        executorService.execute(() -> pantryItemDao.insert(item));
    }

    // Update pantry item
    public void update(PantryItemEntity item) {
        executorService.execute(() -> {
            item.setUpdatedAt(System.currentTimeMillis());
            pantryItemDao.update(item);
        });
    }

    // Delete pantry item
    public void delete(PantryItemEntity item) {
        executorService.execute(() -> pantryItemDao.delete(item));
    }

    // Get all pantry items for user
    public LiveData<List<PantryItemEntity>> getAllPantryItems(String userId) {
        return pantryItemDao.getAllPantryItems(userId);
    }

    // Get pantry item by ID
    public LiveData<PantryItemEntity> getPantryItemById(String itemId) {
        return pantryItemDao.getPantryItemById(itemId);
    }

    // Get pantry items by category
    public LiveData<List<PantryItemEntity>> getPantryItemsByCategory(String userId, String category) {
        return pantryItemDao.getPantryItemsByCategory(userId, category);
    }

    // Get low stock items
    public LiveData<List<PantryItemEntity>> getLowStockItems(String userId) {
        return pantryItemDao.getLowStockItems(userId);
    }

    // Get low stock items synchronously
    public List<PantryItemEntity> getLowStockItemsSync(String userId) {
        return pantryItemDao.getLowStockItemsSync(userId);
    }

    // Get items expiring soon
    public LiveData<List<PantryItemEntity>> getExpiringSoonItems(String userId) {
        long threeDaysFromNow = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L);
        return pantryItemDao.getExpiringSoonItems(userId, threeDaysFromNow);
    }

    // Get expired items
    public LiveData<List<PantryItemEntity>> getExpiredItems(String userId) {
        return pantryItemDao.getExpiredItems(userId, System.currentTimeMillis());
    }

    // Search pantry items
    public LiveData<List<PantryItemEntity>> searchPantryItems(String userId, String searchQuery) {
        return pantryItemDao.searchPantryItems(userId, searchQuery);
    }

    // Update quantity
    public void updateQuantity(String itemId, double quantity) {
        executorService.execute(() -> 
            pantryItemDao.updateQuantity(itemId, quantity, System.currentTimeMillis())
        );
    }

    // Get low stock count
    public LiveData<Integer> getLowStockCount(String userId) {
        return pantryItemDao.getLowStockCount(userId);
    }

    // Get items by location
    public LiveData<List<PantryItemEntity>> getPantryItemsByLocation(String userId, String location) {
        return pantryItemDao.getPantryItemsByLocation(userId, location);
    }

    // Get all items synchronously
    public List<PantryItemEntity> getAllPantryItemsSync(String userId) {
        return pantryItemDao.getAllPantryItemsSync(userId);
    }
}
