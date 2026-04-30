package com.shoppinglist.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.shoppinglist.database.entities.ShoppingHistoryEntity;
import java.util.List;

@Dao
public interface ShoppingHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ShoppingHistoryEntity history);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ShoppingHistoryEntity> histories);

    @Update
    void update(ShoppingHistoryEntity history);

    @Delete
    void delete(ShoppingHistoryEntity history);

    // Get all purchase history for user
    @Query("SELECT * FROM shopping_history WHERE user_id = :userId ORDER BY purchase_date DESC")
    LiveData<List<ShoppingHistoryEntity>> getAllHistory(String userId);

    @Query("SELECT * FROM shopping_history WHERE user_id = :userId ORDER BY purchase_date DESC")
    List<ShoppingHistoryEntity> getAllHistorySync(String userId);

    // Get history by date range
    @Query("SELECT * FROM shopping_history WHERE user_id = :userId AND purchase_date BETWEEN :startDate AND :endDate ORDER BY purchase_date DESC")
    LiveData<List<ShoppingHistoryEntity>> getHistoryByDateRange(String userId, long startDate, long endDate);

    @Query("SELECT * FROM shopping_history WHERE user_id = :userId AND purchase_date BETWEEN :startDate AND :endDate ORDER BY purchase_date DESC")
    List<ShoppingHistoryEntity> getHistoryByDateRangeSync(String userId, long startDate, long endDate);

    // Get history by category
    @Query("SELECT * FROM shopping_history WHERE user_id = :userId AND category = :category ORDER BY purchase_date DESC")
    LiveData<List<ShoppingHistoryEntity>> getHistoryByCategory(String userId, String category);

    // Get history for specific list
    @Query("SELECT * FROM shopping_history WHERE user_id = :userId AND list_id = :listId ORDER BY purchase_date DESC")
    LiveData<List<ShoppingHistoryEntity>> getHistoryForList(String userId, String listId);

    // Most bought items (by quantity)
    @Query("SELECT item_name, category, SUM(quantity) as total_quantity, COUNT(*) as purchase_count, AVG(price) as avg_price " +
           "FROM shopping_history WHERE user_id = :userId " +
           "GROUP BY item_name, category ORDER BY total_quantity DESC LIMIT :limit")
    List<MostBoughtItem> getMostBoughtItems(String userId, int limit);

    // Most expensive purchases
    @Query("SELECT * FROM shopping_history WHERE user_id = :userId ORDER BY total_cost DESC LIMIT :limit")
    List<ShoppingHistoryEntity> getMostExpensivePurchases(String userId, int limit);

    // Spending by category
    @Query("SELECT category, SUM(total_cost) as total_spent, COUNT(*) as item_count " +
           "FROM shopping_history WHERE user_id = :userId " +
           "GROUP BY category ORDER BY total_spent DESC")
    List<CategorySpending> getSpendingByCategory(String userId);

    // Spending by category in date range
    @Query("SELECT category, SUM(total_cost) as total_spent, COUNT(*) as item_count " +
           "FROM shopping_history WHERE user_id = :userId AND purchase_date BETWEEN :startDate AND :endDate " +
           "GROUP BY category ORDER BY total_spent DESC")
    List<CategorySpending> getSpendingByCategoryInRange(String userId, long startDate, long endDate);

    // Monthly spending totals
    @Query("SELECT strftime('%Y-%m', datetime(purchase_date/1000, 'unixepoch')) as month, " +
           "SUM(total_cost) as total_spent, COUNT(*) as item_count " +
           "FROM shopping_history WHERE user_id = :userId " +
           "GROUP BY month ORDER BY month DESC LIMIT :months")
    List<MonthlySpending> getMonthlySpending(String userId, int months);

    // Weekly spending totals
    @Query("SELECT strftime('%Y-W%W', datetime(purchase_date/1000, 'unixepoch')) as week, " +
           "SUM(total_cost) as total_spent, COUNT(*) as item_count " +
           "FROM shopping_history WHERE user_id = :userId " +
           "GROUP BY week ORDER BY week DESC LIMIT :weeks")
    List<WeeklySpending> getWeeklySpending(String userId, int weeks);

    // Total spending
    @Query("SELECT SUM(total_cost) FROM shopping_history WHERE user_id = :userId")
    LiveData<Double> getTotalSpending(String userId);

    @Query("SELECT SUM(total_cost) FROM shopping_history WHERE user_id = :userId")
    double getTotalSpendingSync(String userId);

    // Total spending in date range
    @Query("SELECT SUM(total_cost) FROM shopping_history WHERE user_id = :userId AND purchase_date BETWEEN :startDate AND :endDate")
    double getSpendingInRange(String userId, long startDate, long endDate);

    // Average spending per shopping trip
    @Query("SELECT AVG(trip_total) FROM (" +
           "SELECT list_id, SUM(total_cost) as trip_total " +
           "FROM shopping_history WHERE user_id = :userId " +
           "GROUP BY list_id)")
    double getAverageSpendingPerTrip(String userId);

    // Search history
    @Query("SELECT * FROM shopping_history WHERE user_id = :userId AND " +
           "(item_name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR store_name LIKE '%' || :query || '%') " +
           "ORDER BY purchase_date DESC")
    LiveData<List<ShoppingHistoryEntity>> searchHistory(String userId, String query);

    // Delete all history for user
    @Query("DELETE FROM shopping_history WHERE user_id = :userId")
    void deleteAllForUser(String userId);

    // Delete history older than date
    @Query("DELETE FROM shopping_history WHERE user_id = :userId AND purchase_date < :beforeDate")
    void deleteHistoryBefore(String userId, long beforeDate);

    // Get unique categories
    @Query("SELECT DISTINCT category FROM shopping_history WHERE user_id = :userId ORDER BY category")
    List<String> getUniqueCategories(String userId);

    // Get unique stores
    @Query("SELECT DISTINCT store_name FROM shopping_history WHERE user_id = :userId AND store_name IS NOT NULL ORDER BY store_name")
    List<String> getUniqueStores(String userId);

    // Data classes for aggregated results
    class MostBoughtItem {
        public String item_name;
        public String category;
        public double total_quantity;
        public int purchase_count;
        public double avg_price;
    }

    class CategorySpending {
        public String category;
        public double total_spent;
        public int item_count;
    }

    class MonthlySpending {
        public String month;
        public double total_spent;
        public int item_count;
    }

    class WeeklySpending {
        public String week;
        public double total_spent;
        public int item_count;
    }
}