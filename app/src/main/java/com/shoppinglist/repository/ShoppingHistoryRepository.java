package com.shoppinglist.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.shoppinglist.database.AppDatabase;
import com.shoppinglist.database.dao.ShoppingHistoryDao;
import com.shoppinglist.database.entities.ShoppingHistoryEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShoppingHistoryRepository {
    private ShoppingHistoryDao historyDao;
    private ExecutorService executorService;

    public ShoppingHistoryRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        historyDao = database.shoppingHistoryDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Basic CRUD operations
    public void insert(ShoppingHistoryEntity history) {
        executorService.execute(() -> historyDao.insert(history));
    }

    public void insertAll(List<ShoppingHistoryEntity> histories) {
        executorService.execute(() -> historyDao.insertAll(histories));
    }

    public void update(ShoppingHistoryEntity history) {
        executorService.execute(() -> historyDao.update(history));
    }

    public void delete(ShoppingHistoryEntity history) {
        executorService.execute(() -> historyDao.delete(history));
    }

    // Get history data
    public LiveData<List<ShoppingHistoryEntity>> getAllHistory(String userId) {
        return historyDao.getAllHistory(userId);
    }

    public List<ShoppingHistoryEntity> getAllHistorySync(String userId) {
        return historyDao.getAllHistorySync(userId);
    }

    public LiveData<List<ShoppingHistoryEntity>> getHistoryByDateRange(String userId, long startDate, long endDate) {
        return historyDao.getHistoryByDateRange(userId, startDate, endDate);
    }

    public List<ShoppingHistoryEntity> getHistoryByDateRangeSync(String userId, long startDate, long endDate) {
        return historyDao.getHistoryByDateRangeSync(userId, startDate, endDate);
    }

    public LiveData<List<ShoppingHistoryEntity>> getHistoryByCategory(String userId, String category) {
        return historyDao.getHistoryByCategory(userId, category);
    }

    public LiveData<List<ShoppingHistoryEntity>> getHistoryForList(String userId, String listId) {
        return historyDao.getHistoryForList(userId, listId);
    }

    // Analytics methods
    public List<ShoppingHistoryDao.MostBoughtItem> getMostBoughtItems(String userId, int limit) {
        return historyDao.getMostBoughtItems(userId, limit);
    }

    public List<ShoppingHistoryEntity> getMostExpensivePurchases(String userId, int limit) {
        return historyDao.getMostExpensivePurchases(userId, limit);
    }

    public List<ShoppingHistoryDao.CategorySpending> getSpendingByCategory(String userId) {
        return historyDao.getSpendingByCategory(userId);
    }

    public List<ShoppingHistoryDao.CategorySpending> getSpendingByCategoryInRange(String userId, long startDate, long endDate) {
        return historyDao.getSpendingByCategoryInRange(userId, startDate, endDate);
    }

    public List<ShoppingHistoryDao.MonthlySpending> getMonthlySpending(String userId, int months) {
        return historyDao.getMonthlySpending(userId, months);
    }

    public List<ShoppingHistoryDao.WeeklySpending> getWeeklySpending(String userId, int weeks) {
        return historyDao.getWeeklySpending(userId, weeks);
    }

    // Spending totals
    public LiveData<Double> getTotalSpending(String userId) {
        return historyDao.getTotalSpending(userId);
    }

    public double getTotalSpendingSync(String userId) {
        return historyDao.getTotalSpendingSync(userId);
    }

    public double getSpendingInRange(String userId, long startDate, long endDate) {
        return historyDao.getSpendingInRange(userId, startDate, endDate);
    }

    public double getAverageSpendingPerTrip(String userId) {
        return historyDao.getAverageSpendingPerTrip(userId);
    }

    // Search and filters
    public LiveData<List<ShoppingHistoryEntity>> searchHistory(String userId, String query) {
        return historyDao.searchHistory(userId, query);
    }

    public List<String> getUniqueCategories(String userId) {
        return historyDao.getUniqueCategories(userId);
    }

    public List<String> getUniqueStores(String userId) {
        return historyDao.getUniqueStores(userId);
    }

    // Cleanup methods
    public void deleteAllForUser(String userId) {
        executorService.execute(() -> historyDao.deleteAllForUser(userId));
    }

    public void deleteHistoryBefore(String userId, long beforeDate) {
        executorService.execute(() -> historyDao.deleteHistoryBefore(userId, beforeDate));
    }
}