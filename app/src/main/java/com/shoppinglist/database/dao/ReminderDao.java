package com.shoppinglist.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.shoppinglist.database.entities.ReminderEntity;
import java.util.List;

@Dao
public interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReminderEntity reminder);

    @Update
    void update(ReminderEntity reminder);

    @Delete
    void delete(ReminderEntity reminder);

    @Query("SELECT * FROM reminders WHERE user_id = :userId ORDER BY scheduled_time ASC")
    LiveData<List<ReminderEntity>> getAllReminders(String userId);

    @Query("SELECT * FROM reminders WHERE user_id = :userId ORDER BY scheduled_time ASC")
    List<ReminderEntity> getAllRemindersSync(String userId);

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    LiveData<ReminderEntity> getReminderById(String reminderId);

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    ReminderEntity getReminderByIdSync(String reminderId);

    @Query("SELECT * FROM reminders WHERE user_id = :userId AND is_active = 1 ORDER BY scheduled_time ASC")
    LiveData<List<ReminderEntity>> getActiveReminders(String userId);

    @Query("SELECT * FROM reminders WHERE user_id = :userId AND is_active = 1 ORDER BY scheduled_time ASC")
    List<ReminderEntity> getActiveRemindersSync(String userId);

    @Query("SELECT * FROM reminders WHERE user_id = :userId AND reminder_type = :type AND is_active = 1 ORDER BY scheduled_time ASC")
    LiveData<List<ReminderEntity>> getRemindersByType(String userId, String type);

    @Query("SELECT * FROM reminders WHERE user_id = :userId AND list_id = :listId AND is_active = 1 ORDER BY scheduled_time ASC")
    LiveData<List<ReminderEntity>> getRemindersForList(String userId, String listId);

    // Get reminders that should trigger now
    @Query("SELECT * FROM reminders WHERE user_id = :userId AND is_active = 1 AND is_triggered = 0 AND scheduled_time <= :currentTime ORDER BY scheduled_time ASC")
    List<ReminderEntity> getPendingReminders(String userId, long currentTime);

    // Mark reminder as triggered
    @Query("UPDATE reminders SET is_triggered = 1, updated_at = :updatedAt WHERE id = :reminderId")
    void markAsTriggered(String reminderId, long updatedAt);

    // Toggle active status
    @Query("UPDATE reminders SET is_active = :isActive, updated_at = :updatedAt WHERE id = :reminderId")
    void setActive(String reminderId, boolean isActive, long updatedAt);

    // Delete all reminders for a user
    @Query("DELETE FROM reminders WHERE user_id = :userId")
    void deleteAllForUser(String userId);

    // Delete all reminders for a list
    @Query("DELETE FROM reminders WHERE list_id = :listId")
    void deleteAllForList(String listId);

    // Get count of active reminders
    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND is_active = 1")
    LiveData<Integer> getActiveReminderCount(String userId);

    // Reset triggered status for repeating reminders
    @Query("UPDATE reminders SET is_triggered = 0, scheduled_time = :newScheduledTime, updated_at = :updatedAt WHERE id = :reminderId")
    void rescheduleReminder(String reminderId, long newScheduledTime, long updatedAt);
}
