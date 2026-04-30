package com.shoppinglist.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.database.dao.ShoppingHistoryDao;
import com.shoppinglist.database.entities.ShoppingHistoryEntity;
import com.shoppinglist.repository.ShoppingHistoryRepository;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShoppingHistoryViewModel extends AndroidViewModel {
    private ShoppingHistoryRepository repository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private String userId;

    // LiveData for UI
    private MutableLiveData<List<ShoppingHistoryDao.MostBoughtItem>> mostBoughtItems = new MutableLiveData<>();
    private MutableLiveData<List<ShoppingHistoryDao.CategorySpending>> categorySpending = new MutableLiveData<>();
    private MutableLiveData<List<ShoppingHistoryDao.MonthlySpending>> monthlySpending = new MutableLiveData<>();
    private MutableLiveData<List<ShoppingHistoryDao.WeeklySpending>> weeklySpending = new MutableLiveData<>();
    private MutableLiveData<Double> averageSpendingPerTrip = new MutableLiveData<>();

    public ShoppingHistoryViewModel(Application application) {
        super(application);
        repository = new ShoppingHistoryRepository(application);
        sessionManager = new SessionManager(application);
        executorService = Executors.newSingleThreadExecutor();
        userId = sessionManager.getUser().getId();
    }

    // Basic data access
    public LiveData<List<ShoppingHistoryEntity>> getAllHistory() {
        return repository.getAllHistory(userId);
    }

    public LiveData<List<ShoppingHistoryEntity>> getHistoryByDateRange(long startDate, long endDate) {
        return repository.getHistoryByDateRange(userId, startDate, endDate);
    }

    public LiveData<List<ShoppingHistoryEntity>> getHistoryByCategory(String category) {
        return repository.getHistoryByCategory(userId, category);
    }

    public LiveData<List<ShoppingHistoryEntity>> searchHistory(String query) {
        return repository.searchHistory(userId, query);
    }

    public LiveData<Double> getTotalSpending() {
        return repository.getTotalSpending(userId);
    }

    // Analytics data
    public LiveData<List<ShoppingHistoryDao.MostBoughtItem>> getMostBoughtItems() {
        return mostBoughtItems;
    }

    public LiveData<List<ShoppingHistoryDao.CategorySpending>> getCategorySpending() {
        return categorySpending;
    }

    public LiveData<List<ShoppingHistoryDao.MonthlySpending>> getMonthlySpending() {
        return monthlySpending;
    }

    public LiveData<List<ShoppingHistoryDao.WeeklySpending>> getWeeklySpending() {
        return weeklySpending;
    }

    public LiveData<Double> getAverageSpendingPerTrip() {
        return averageSpendingPerTrip;
    }

    // Load analytics data
    public void loadMostBoughtItems(int limit) {
        executorService.execute(() -> {
            List<ShoppingHistoryDao.MostBoughtItem> items = repository.getMostBoughtItems(userId, limit);
            mostBoughtItems.postValue(items);
        });
    }

    public void loadCategorySpending() {
        executorService.execute(() -> {
            List<ShoppingHistoryDao.CategorySpending> spending = repository.getSpendingByCategory(userId);
            categorySpending.postValue(spending);
        });
    }

    public void loadCategorySpendingInRange(long startDate, long endDate) {
        executorService.execute(() -> {
            List<ShoppingHistoryDao.CategorySpending> spending = 
                    repository.getSpendingByCategoryInRange(userId, startDate, endDate);
            categorySpending.postValue(spending);
        });
    }

    public void loadMonthlySpending(int months) {
        executorService.execute(() -> {
            List<ShoppingHistoryDao.MonthlySpending> spending = repository.getMonthlySpending(userId, months);
            monthlySpending.postValue(spending);
        });
    }

    public void loadWeeklySpending(int weeks) {
        executorService.execute(() -> {
            List<ShoppingHistoryDao.WeeklySpending> spending = repository.getWeeklySpending(userId, weeks);
            weeklySpending.postValue(spending);
        });
    }

    public void loadAverageSpendingPerTrip() {
        executorService.execute(() -> {
            double average = repository.getAverageSpendingPerTrip(userId);
            averageSpendingPerTrip.postValue(average);
        });
    }

    // Create history entry when item is purchased
    public void recordPurchase(String listId, String listName, String itemName, String category,
                              double quantity, String unit, double price, String storeName, String notes) {
        executorService.execute(() -> {
            ShoppingHistoryEntity history = new ShoppingHistoryEntity();
            history.setId(UUID.randomUUID().toString());
            history.setUserId(userId);
            history.setListId(listId);
            history.setListName(listName);
            history.setItemName(itemName);
            history.setCategory(category);
            history.setQuantity(quantity);
            history.setUnit(unit);
            history.setPrice(price);
            history.setTotalCost(quantity * price);
            history.setPurchaseDate(System.currentTimeMillis());
            history.setStoreName(storeName);
            history.setNotes(notes);
            history.setCreatedAt(System.currentTimeMillis());

            repository.insert(history);
        });
    }

    // Bulk record purchases (when completing a shopping list)
    public void recordListCompletion(String listId, String listName, List<PurchaseItem> items, String storeName) {
        executorService.execute(() -> {
            long currentTime = System.currentTimeMillis();
            for (PurchaseItem item : items) {
                ShoppingHistoryEntity history = new ShoppingHistoryEntity();
                history.setId(UUID.randomUUID().toString());
                history.setUserId(userId);
                history.setListId(listId);
                history.setListName(listName);
                history.setItemName(item.name);
                history.setCategory(item.category);
                history.setQuantity(item.quantity);
                history.setUnit(item.unit);
                history.setPrice(item.price);
                history.setTotalCost(item.quantity * item.price);
                history.setPurchaseDate(currentTime);
                history.setStoreName(storeName);
                history.setNotes(item.notes);
                history.setCreatedAt(currentTime);

                repository.insert(history);
            }
        });
    }

    // Get spending for current month
    public void getCurrentMonthSpending(SpendingCallback callback) {
        executorService.execute(() -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long startOfMonth = cal.getTimeInMillis();
            
            cal.add(Calendar.MONTH, 1);
            long endOfMonth = cal.getTimeInMillis();
            
            double spending = repository.getSpendingInRange(userId, startOfMonth, endOfMonth);
            callback.onResult(spending);
        });
    }

    // Get spending for current week
    public void getCurrentWeekSpending(SpendingCallback callback) {
        executorService.execute(() -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long startOfWeek = cal.getTimeInMillis();
            
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            long endOfWeek = cal.getTimeInMillis();
            
            double spending = repository.getSpendingInRange(userId, startOfWeek, endOfWeek);
            callback.onResult(spending);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    // Helper classes
    public static class PurchaseItem {
        public String name;
        public String category;
        public double quantity;
        public String unit;
        public double price;
        public String notes;

        public PurchaseItem(String name, String category, double quantity, String unit, double price, String notes) {
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.unit = unit;
            this.price = price;
            this.notes = notes;
        }
    }

    public interface SpendingCallback {
        void onResult(double spending);
    }
}