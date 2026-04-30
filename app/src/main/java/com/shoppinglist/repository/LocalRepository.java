package com.shoppinglist.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.shoppinglist.database.AppDatabase;
import com.shoppinglist.database.dao.ShoppingItemDao;
import com.shoppinglist.database.dao.ShoppingListDao;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import com.shoppinglist.database.entities.ShoppingListEntity;
import com.shoppinglist.database.entities.UserEntity;
import java.util.List;

public class LocalRepository {
    private ShoppingListDao listDao;
    private ShoppingItemDao itemDao;
    private AppDatabase db;

    public LocalRepository(Context context) {
        db = AppDatabase.getInstance(context);
        listDao = db.shoppingListDao();
        itemDao = db.shoppingItemDao();
    }

    // Shopping Lists
    public LiveData<List<ShoppingListEntity>> getListsByOwner(String userId) {
        return listDao.getListsByOwner(userId);
    }

    public LiveData<ShoppingListEntity> getListById(String listId) {
        return listDao.getListById(listId);
    }

    public void insertList(ShoppingListEntity list) {
        listDao.insert(list);
    }

    public void updateList(ShoppingListEntity list) {
        listDao.update(list);
    }

    public void deleteList(ShoppingListEntity list) {
        listDao.delete(list);
    }

    public List<ShoppingListEntity> getAllListsSync() {
        return listDao.getAllListsSync();
    }

    // Shopping Items
    public LiveData<List<ShoppingItemEntity>> getItemsByListId(String listId) {
        return itemDao.getItemsByListId(listId);
    }

    public List<ShoppingItemEntity> getItemsByListIdSync(String listId) {
        return itemDao.getItemsByListIdSync(listId);
    }

    public LiveData<ShoppingItemEntity> getItemById(String itemId) {
        return itemDao.getItemById(itemId);
    }

    public void insertItem(ShoppingItemEntity item) {
        itemDao.insert(item);
    }

    public void updateItem(ShoppingItemEntity item) {
        itemDao.update(item);
    }

    public void deleteItem(ShoppingItemEntity item) {
        itemDao.delete(item);
    }

    public void deleteAllItemsByListId(String listId) {
        itemDao.deleteAllByListId(listId);
    }

    public void updateItemPurchased(String itemId, boolean purchased) {
        itemDao.updatePurchasedStatus(itemId, purchased);
    }

    public void updateItemOrderIndex(String itemId, int orderIndex) {
        itemDao.updateOrderIndex(itemId, orderIndex);
    }

    // User
    public void insertUser(UserEntity user) {
        db.userDao().insert(user);
    }

    public UserEntity getUser(String userId) {
        return db.userDao().getUserById(userId);
    }
    
    // Budget tracking methods
    public LiveData<Double> getTotalCostForList(String listId) {
        return itemDao.getTotalCostForList(listId);
    }
    
    public LiveData<Double> getSpentAmountForList(String listId) {
        return itemDao.getSpentAmountForList(listId);
    }
    
    public LiveData<Double> getRemainingCostForList(String listId) {
        return itemDao.getRemainingCostForList(listId);
    }
    
    public LiveData<Double> getListBudget(String listId) {
        return listDao.getListBudget(listId);
    }
    
    public void updateListBudget(String listId, double budget) {
        listDao.updateBudget(listId, budget);
    }
    
    public int getItemCountForList(String listId) {
        return listDao.getItemCountForList(listId);
    }
    
    public int getCompletedItemCountForList(String listId) {
        return listDao.getCompletedItemCountForList(listId);
    }
    
    public List<ShoppingListEntity> getAllListsForUser(String userId) {
        return listDao.getAllListsForUser(userId);
    }
}