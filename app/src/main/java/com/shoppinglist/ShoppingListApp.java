package com.shoppinglist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.firebase.FirebaseApp;
import com.shoppinglist.services.NotificationHelper;
import com.shoppinglist.utils.LocaleHelper;

public class ShoppingListApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        FirebaseApp.initializeApp(this);
        
        // Initialize notification channels
        NotificationHelper.createChannel(this);
        
        // Apply saved language preference
        LocaleHelper.applySavedLocale(this);
        
        // Apply saved theme preference
        SharedPreferences prefs = getSharedPreferences("app_preferences", MODE_PRIVATE);
        int themeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeMode);
        
        // WorkManager 2.6+ auto-initializes via androidx.startup — no manual call needed.
    }

    public static Context getAppContext() {
        return context;
    }
}
