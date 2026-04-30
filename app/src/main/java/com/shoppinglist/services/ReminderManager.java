package com.shoppinglist.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.shoppinglist.database.AppDatabase;
import com.shoppinglist.database.entities.ReminderEntity;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReminderManager {
    private Context context;
    private AppDatabase database;
    private ExecutorService executorService;

    public ReminderManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Schedule a reminder
    public void scheduleReminder(ReminderEntity reminder) {
        executorService.execute(() -> {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
            intent.putExtra("reminder_id", reminder.getId());
            intent.putExtra("title", reminder.getTitle());
            intent.putExtra("message", reminder.getMessage());
            intent.putExtra("reminder_type", reminder.getReminderType());

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    reminder.getId().hashCode(),
                    intent,
                    flags
            );

            // Schedule the alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.getScheduledTime(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminder.getScheduledTime(),
                        pendingIntent
                );
            }
        });
    }

    // Cancel a reminder
    public void cancelReminder(String reminderId) {
        executorService.execute(() -> {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
            
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    reminderId.hashCode(),
                    intent,
                    flags
            );

            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        });
    }

    // Reschedule repeating reminder
    public void rescheduleRepeatingReminder(ReminderEntity reminder) {
        executorService.execute(() -> {
            long newScheduledTime = calculateNextScheduledTime(reminder);
            
            database.reminderDao().rescheduleReminder(
                    reminder.getId(),
                    newScheduledTime,
                    System.currentTimeMillis()
            );

            reminder.setScheduledTime(newScheduledTime);
            reminder.setTriggered(false);
            scheduleReminder(reminder);
        });
    }

    // Calculate next scheduled time for repeating reminders
    private long calculateNextScheduledTime(ReminderEntity reminder) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reminder.getScheduledTime());

        switch (reminder.getRepeatInterval()) {
            case "DAILY":
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case "WEEKLY":
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case "MONTHLY":
                calendar.add(Calendar.MONTH, 1);
                break;
            default:
                // No repeat
                return reminder.getScheduledTime();
        }

        return calendar.getTimeInMillis();
    }

    // Create a low stock reminder
    public void createLowStockReminder(String userId, String itemName, int count) {
        executorService.execute(() -> {
            ReminderEntity reminder = new ReminderEntity();
            reminder.setId(java.util.UUID.randomUUID().toString());
            reminder.setUserId(userId);
            reminder.setTitle("Low Stock Alert");
            reminder.setMessage(count + " items running low, including " + itemName);
            reminder.setReminderType("LOW_STOCK");
            reminder.setScheduledTime(System.currentTimeMillis());
            reminder.setRepeatInterval("NONE");
            reminder.setActive(true);
            reminder.setTriggered(false);
            reminder.setCreatedAt(System.currentTimeMillis());
            reminder.setUpdatedAt(System.currentTimeMillis());

            database.reminderDao().insert(reminder);
            scheduleReminder(reminder);
        });
    }

    // Create an expiry reminder
    public void createExpiryReminder(String userId, String itemName, long expiryDate) {
        executorService.execute(() -> {
            // Schedule reminder 1 day before expiry
            long reminderTime = expiryDate - (24 * 60 * 60 * 1000);
            
            if (reminderTime > System.currentTimeMillis()) {
                ReminderEntity reminder = new ReminderEntity();
                reminder.setId(java.util.UUID.randomUUID().toString());
                reminder.setUserId(userId);
                reminder.setTitle("Item Expiring Soon");
                reminder.setMessage(itemName + " expires tomorrow");
                reminder.setReminderType("EXPIRY");
                reminder.setScheduledTime(reminderTime);
                reminder.setRepeatInterval("NONE");
                reminder.setActive(true);
                reminder.setTriggered(false);
                reminder.setCreatedAt(System.currentTimeMillis());
                reminder.setUpdatedAt(System.currentTimeMillis());

                database.reminderDao().insert(reminder);
                scheduleReminder(reminder);
            }
        });
    }

    // Check and trigger pending reminders
    public void checkPendingReminders(String userId) {
        executorService.execute(() -> {
            java.util.List<ReminderEntity> pendingReminders = 
                    database.reminderDao().getPendingReminders(userId, System.currentTimeMillis());

            for (ReminderEntity reminder : pendingReminders) {
                // Trigger notification
                NotificationHelper.showReminderNotification(
                        context,
                        reminder.getId().hashCode(),
                        reminder.getTitle(),
                        reminder.getMessage()
                );

                // Mark as triggered
                database.reminderDao().markAsTriggered(reminder.getId(), System.currentTimeMillis());

                // Reschedule if repeating
                if (reminder.isRepeating()) {
                    rescheduleRepeatingReminder(reminder);
                }
            }
        });
    }
}
