package com.shoppinglist.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.shoppinglist.database.AppDatabase;
import com.shoppinglist.database.entities.ReminderEntity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderId = intent.getStringExtra("reminder_id");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String reminderType = intent.getStringExtra("reminder_type");

        if (reminderId == null || title == null || message == null) {
            return;
        }

        // Show notification
        NotificationHelper.showReminderNotification(
                context,
                reminderId.hashCode(),
                title,
                message
        );

        // Update database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                AppDatabase database = AppDatabase.getInstance(context);
                
                // Mark as triggered
                database.reminderDao().markAsTriggered(reminderId, System.currentTimeMillis());

                // Check if it's a repeating reminder - get directly from database
                ReminderEntity reminder = database.reminderDao().getReminderByIdSync(reminderId);
                if (reminder != null && reminder.isRepeating()) {
                    ReminderManager reminderManager = new ReminderManager(context);
                    reminderManager.rescheduleRepeatingReminder(reminder);
                }
            } catch (Exception e) {
                android.util.Log.e("ReminderReceiver", "Error processing reminder", e);
            } finally {
                executor.shutdown();
            }
        });
    }
}
