package com.shoppinglist.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import com.shoppinglist.repository.LocalRepository;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyticsViewModel extends AndroidViewModel {
    private LocalRepository repository;
    private SessionManager sessionManager;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private MutableLiveData<Double> totalSpent = new MutableLiveData<>(0.0);
    private MutableLiveData<Double> averagePerList = new MutableLiveData<>(0.0);
    private MutableLiveData<Integer> totalLists = new MutableLiveData<>(0);
    private MutableLiveData<Integer> totalItems = new MutableLiveData<>(0);
    private MutableLiveData<Double> totalBudget = new MutableLiveData<>(0.0);
    private MutableLiveData<Double> totalActual = new MutableLiveData<>(0.0);
    private MutableLiveData<String> shoppingFrequency = new MutableLiveData<>("N/A");
    private MutableLiveData<Long> lastShoppingDate = new MutableLiveData<>(0L);
    private MutableLiveData<String> mostFrequentCategory = new MutableLiveData<>("N/A");
    private MutableLiveData<List<ShoppingItemEntity>> mostExpensiveItems = new MutableLiveData<>();

    public AnalyticsViewModel(Application application) {
        super(application);
        repository = new LocalRepository(application);
        sessionManager = new SessionManager(application);
        loadAnalytics();
    }

    private void loadAnalytics() {
        executor.execute(() -> {
            String userId = sessionManager.getUser().getId();
            
            // Get all lists for the user
            List<com.shoppinglist.database.entities.ShoppingListEntity> lists = 
                    repository.getAllListsForUser(userId);
            
            if (lists == null || lists.isEmpty()) {
                return;
            }

            // Calculate analytics
            double totalSpentValue = 0.0;
            double totalBudgetValue = 0.0;
            int totalItemsCount = 0;
            long latestDate = 0;
            java.util.Map<String, Integer> categoryCount = new java.util.HashMap<>();
            java.util.List<ShoppingItemEntity> allItems = new java.util.ArrayList<>();

            for (com.shoppinglist.database.entities.ShoppingListEntity list : lists) {
                // Get items for this list
                List<ShoppingItemEntity> items = repository.getItemsByListIdSync(list.getId());
                
                if (items != null) {
                    allItems.addAll(items);
                    totalItemsCount += items.size();
                    
                    for (ShoppingItemEntity item : items) {
                        totalSpentValue += item.getPrice();
                        
                        // Track category frequency
                        String category = item.getCategory();
                        categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
                        
                        // Track latest date
                        if (item.getCreatedAt() > latestDate) {
                            latestDate = item.getCreatedAt();
                        }
                    }
                }
                
                // Sum budgets
                totalBudgetValue += list.getBudget();
            }

            // Calculate average per list
            double avgPerList = lists.size() > 0 ? totalSpentValue / lists.size() : 0.0;

            // Find most frequent category
            String mostFrequent = "N/A";
            int maxCount = 0;
            for (java.util.Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequent = entry.getKey();
                }
            }

            // Calculate shopping frequency (lists per month)
            long currentTime = System.currentTimeMillis();
            long thirtyDaysAgo = currentTime - (30L * 24 * 60 * 60 * 1000);
            int recentLists = 0;
            for (com.shoppinglist.database.entities.ShoppingListEntity list : lists) {
                if (list.getCreatedAt() > thirtyDaysAgo) {
                    recentLists++;
                }
            }
            String frequency = recentLists + " lists/month";

            // Get top 10 most expensive items
            allItems.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            List<ShoppingItemEntity> topExpensive = allItems.size() > 10 ? 
                    allItems.subList(0, 10) : allItems;

            // Update LiveData on main thread
            final double finalTotalSpent = totalSpentValue;
            final double finalAvgPerList = avgPerList;
            final int finalTotalLists = lists.size();
            final int finalTotalItems = totalItemsCount;
            final double finalTotalBudget = totalBudgetValue;
            final String finalMostFrequent = mostFrequent;
            final String finalFrequency = frequency;
            final long finalLatestDate = latestDate;
            final List<ShoppingItemEntity> finalTopExpensive = topExpensive;

            totalSpent.postValue(finalTotalSpent);
            averagePerList.postValue(finalAvgPerList);
            totalLists.postValue(finalTotalLists);
            totalItems.postValue(finalTotalItems);
            totalBudget.postValue(finalTotalBudget);
            totalActual.postValue(finalTotalSpent);
            mostFrequentCategory.postValue(finalMostFrequent);
            shoppingFrequency.postValue(finalFrequency);
            lastShoppingDate.postValue(finalLatestDate);
            mostExpensiveItems.postValue(finalTopExpensive);
        });
    }

    public LiveData<Double> getTotalSpent() { return totalSpent; }
    public LiveData<Double> getAveragePerList() { return averagePerList; }
    public LiveData<Integer> getTotalLists() { return totalLists; }
    public LiveData<Integer> getTotalItems() { return totalItems; }
    public LiveData<Double> getTotalBudget() { return totalBudget; }
    public LiveData<Double> getTotalActual() { return totalActual; }
    public LiveData<String> getShoppingFrequency() { return shoppingFrequency; }
    public LiveData<Long> getLastShoppingDate() { return lastShoppingDate; }
    public LiveData<String> getMostFrequentCategory() { return mostFrequentCategory; }
    public LiveData<List<ShoppingItemEntity>> getMostExpensiveItems() { return mostExpensiveItems; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
