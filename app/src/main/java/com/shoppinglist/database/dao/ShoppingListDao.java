package com.shoppinglist.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.shoppinglist.database.entities.ShoppingListEntity;
import java.util.List;

@Dao
public interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ShoppingListEntity list);

    @Update
    void update(ShoppingListEntity list);

    @Delete
    void delete(ShoppingListEntity list);

    @Query("SELECT * FROM shopping_lists WHERE owner_id = :userId ORDER BY created_at DESC")
    LiveData<List<ShoppingListEntity>> getListsByOwner(String userId);

    @Query("SELECT * FROM shopping_lists WHERE id = :listId")
    LiveData<ShoppingListEntity> getListById(String listId);

    @Query("SELECT * FROM shopping_lists WHERE shared_with LIKE '%' || :userId || '%'")
    LiveData<List<ShoppingListEntity>> getSharedLists(String userId);

    @Query("SELECT * FROM shopping_lists")
    LiveData<List<ShoppingListEntity>> getAllLists(); // for export

    @Query("SELECT * FROM shopping_lists")
    List<ShoppingListEntity> getAllListsSync(); // synchronous version for export/sync
    
    @Query("SELECT budget FROM shopping_lists WHERE id = :listId")
    LiveData<Double> getListBudget(String listId);
    
    @Query("UPDATE shopping_lists SET budget = :budget WHERE id = :listId")
    void updateBudget(String listId, double budget);
    
    @Query("SELECT COUNT(*) FROM shopping_items WHERE list_id = :listId")
    int getItemCountForList(String listId);
    
    @Query("SELECT COUNT(*) FROM shopping_items WHERE list_id = :listId AND is_purchased = 1")
    int getCompletedItemCountForList(String listId);
    
    @Query("SELECT * FROM shopping_lists WHERE owner_id = :userId")
    List<ShoppingListEntity> getAllListsForUser(String userId);
}