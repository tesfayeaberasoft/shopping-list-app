package com.shoppinglist.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.shoppinglist.database.entities.PantryItemEntity;
import java.util.List;

@Dao
public interface PantryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PantryItemEntity item);

    @Update
    void update(PantryItemEntity item);

    @Delete
    void delete(PantryItemEntity item);

    @Query("SELECT * FROM pantry_items WHERE user_id = :userId ORDER BY name ASC")
    LiveData<List<PantryItemEntity>> getAllPantryItems(String userId);

    @Query("SELECT * FROM pantry_items WHERE user_id = :userId ORDER BY name ASC")
    List<PantryItemEntity> getAllPantryItemsSync(String userId);

    @Query("SELECT * FROM pantry_items WHERE id = :itemId")
    LiveData<PantryItemEntity> getPantryItemById(String itemId);

    @Query("SELECT * FROM pantry_items WHERE user_id = :userId AND category = :category ORDER BY name ASC")
    LiveData<List<PantryItemEntity>> getPantryItemsByCategory(String userId, String category);

    // Get low stock items (current_quantity <= minimum_quantity)
    @Query("SELECT * FROM pantry_items WHERE user_id = :userId AND current_quantity <= minimum_quantity ORDER BY name ASC")
    LiveData<List<PantryItemEntity>> getLowStockItems(String userId);

    @Query("SELECT * FROM pantry_items WHERE user_id = :userId AND current_quantity <= minimum_quantity ORDER BY name ASC")
    List<PantryItemEntity> getLowStockItemsSync(String userId);

    // Get items expiring soon (within 3 days)
    @Query("SELECT * FROM pantry_items WHERE user_id = :userId AND expiry_date > 0 AND expiry_date <= :threeDaysFromNow ORDER BY expiry_date ASC")
    LiveData<List<PantryItemEntity>> getExpiringSoonItems(String userId, long threeDaysFromNow);

    // Get expired items
    @Query("SELECT * FROM pantry_items WHERE user_id = :userId AND expiry_date > 0 AND expiry_date < :currentTime ORDER BY expiry_date ASC")
    LiveData<List<PantryItemEntity>> getExpiredItems(String userId, long currentTime);

    // Search pantry items
    @Query("SELECT * FROM pantry_items WHERE user_id = :userId AND name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    LiveData<List<PantryItemEntity>> searchPantryItems(String userId, String searchQuery);

    // Update quantity
    @Query("UPDATE pantry_items SET current_quantity = :quantity, updated_at = :updatedAt WHERE id = :itemId")
    void updateQuantity(String itemId, double quantity, long updatedAt);

    // Get count of low stock items
    @Query("SELECT COUNT(*) FROM pantry_items WHERE user_id = :userId AND current_quantity <= minimum_quantity")
    LiveData<Integer> getLowStockCount(String userId);

    // Get items by location
    @Query("SELECT * FROM pantry_items WHERE user_id = :userId AND location = :location ORDER BY name ASC")
    LiveData<List<PantryItemEntity>> getPantryItemsByLocation(String userId, String location);

    // Delete all pantry items for a user
    @Query("DELETE FROM pantry_items WHERE user_id = :userId")
    void deleteAllForUser(String userId);
}
