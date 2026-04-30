package com.shoppinglist.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.shoppinglist.R;
import com.shoppinglist.activities.MainActivity;

public class NotificationHelper {
    public static final String CHANNEL_ID = "shopping_channel";
    public static final String REMINDER_CHANNEL_ID = "reminder_channel";
    public static final String LOW_STOCK_CHANNEL_ID = "low_stock_channel";

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            
            // Shopping List Channel
            CharSequence name = "Shopping List";
            String description = "Notifications for shared lists";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            manager.createNotificationChannel(channel);
            
            // Reminder Channel
            CharSequence reminderName = "Reminders";
            String reminderDesc = "Shopping reminders and alerts";
            NotificationChannel reminderChannel = new NotificationChannel(
                    REMINDER_CHANNEL_ID, reminderName, NotificationManager.IMPORTANCE_HIGH);
            reminderChannel.setDescription(reminderDesc);
            reminderChannel.enableVibration(true);
            manager.createNotificationChannel(reminderChannel);
            
            // Low Stock Channel
            CharSequence lowStockName = "Low Stock Alerts";
            String lowStockDesc = "Alerts when pantry items are running low";
            NotificationChannel lowStockChannel = new NotificationChannel(
                    LOW_STOCK_CHANNEL_ID, lowStockName, NotificationManager.IMPORTANCE_DEFAULT);
            lowStockChannel.setDescription(lowStockDesc);
            manager.createNotificationChannel(lowStockChannel);
        }
    }

    public static void showNotification(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_shopping_cart)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
    
    public static void showReminderNotification(Context context, int notificationId, String title, String message) {
        // Create intent to open app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, intent, flags);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 250, 500});
        
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }
    
    public static void showLowStockNotification(Context context, int notificationId, String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, intent, flags);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, LOW_STOCK_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }
}