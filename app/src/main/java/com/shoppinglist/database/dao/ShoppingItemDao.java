package com.shoppinglist.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import java.util.List;

@Dao
public interface ShoppingItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ShoppingItemEntity item);

    @Update
    void update(ShoppingItemEntity item);

    @Delete
    void delete(ShoppingItemEntity item);

    @Query("SELECT * FROM shopping_items WHERE list_id = :listId ORDER BY order_index ASC")
    LiveData<List<ShoppingItemEntity>> getItemsByListId(String listId);

    @Query("SELECT * FROM shopping_items WHERE id = :itemId")
    LiveData<ShoppingItemEntity> getItemById(String itemId);

    @Query("SELECT * FROM shopping_items WHERE list_id = :listId")
    List<ShoppingItemEntity> getItemsByListIdSync(String listId); // for sync/export

    @Query("DELETE FROM shopping_items WHERE list_id = :listId")
    void deleteAllByListId(String listId);

    @Query("UPDATE shopping_items SET is_purchased = :purchased WHERE id = :itemId")
    void updatePurchasedStatus(String itemId, boolean purchased);

    @Query("UPDATE shopping_items SET order_index = :orderIndex WHERE id = :itemId")
    void updateOrderIndex(String itemId, int orderIndex);
    
    // NEW: Budget tracking queries
    @Query("SELECT SUM(price * quantity) FROM shopping_items WHERE list_id = :listId")
    LiveData<Double> getTotalCostForList(String listId);
    
    @Query("SELECT SUM(price * quantity) FROM shopping_items WHERE list_id = :listId AND is_purchased = 1")
    LiveData<Double> getSpentAmountForList(String listId);
    
    @Query("SELECT SUM(price * quantity) FROM shopping_items WHERE list_id = :listId AND is_purchased = 0")
    LiveData<Double> getRemainingCostForList(String listId);
}