package com.shoppinglist.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.database.AppDatabase;
import com.shoppinglist.database.entities.ReminderEntity;
import com.shoppinglist.services.ReminderManager;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReminderViewModel extends AndroidViewModel {
    private AppDatabase database;
    private SessionManager sessionManager;
    private ReminderManager reminderManager;
    private ExecutorService executorService;
    private String userId;

    public ReminderViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        sessionManager = new SessionManager(application);
        reminderManager = new ReminderManager(application);
        executorService = Executors.newSingleThreadExecutor();
        userId = sessionManager.getUser().getId();
    }

    public LiveData<List<ReminderEntity>> getAllReminders() {
        return database.reminderDao().getAllReminders(userId);
    }

    public LiveData<List<ReminderEntity>> getActiveReminders() {
        return database.reminderDao().getActiveReminders(userId);
    }

    public LiveData<List<ReminderEntity>> getRemindersByType(String type) {
        return database.reminderDao().getRemindersByType(userId, type);
    }

    public LiveData<Integer> getActiveReminderCount() {
        return database.reminderDao().getActiveReminderCount(userId);
    }

    public void createReminder(String title, String message, String reminderType,
                              long scheduledTime, String repeatInterval) {
        executorService.execute(() -> {
            ReminderEntity reminder = new ReminderEntity();
            reminder.setId(UUID.randomUUID().toString());
            reminder.setUserId(userId);
            reminder.setTitle(title);
            reminder.setMessage(message);
            reminder.setReminderType(reminderType);
            reminder.setScheduledTime(scheduledTime);
            reminder.setRepeatInterval(repeatInterval);
            reminder.setActive(true);
            reminder.setTriggered(false);
            reminder.setCreatedAt(System.currentTimeMillis());
            reminder.setUpdatedAt(System.currentTimeMillis());

            database.reminderDao().insert(reminder);
            
            // Schedule the reminder
            reminderManager.scheduleReminder(reminder);
        });
    }

    public void updateReminder(ReminderEntity reminder) {
        executorService.execute(() -> {
            reminder.setUpdatedAt(System.currentTimeMillis());
            database.reminderDao().update(reminder);
        });
    }

    public void deleteReminder(ReminderEntity reminder) {
        executorService.execute(() -> {
            database.reminderDao().delete(reminder);
            reminderManager.cancelReminder(reminder.getId());
        });
    }

    public void setReminderActive(String reminderId, boolean isActive) {
        executorService.execute(() -> {
            database.reminderDao().setActive(reminderId, isActive, System.currentTimeMillis());
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
