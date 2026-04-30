package com.shoppinglist.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

/**
 * LocaleHelper - Language and Locale Management
 * Handles language switching and locale configuration
 * Supports multiple languages with persistent storage
 * 
 * Features:
 * - Set language preference
 * - Apply saved locale
 * - Wrap context with locale
 * - Get saved language
 * - Support for Android API 16+
 * 
 * Supported Languages:
 * - English (en)
 * - Amharic (am)
 * - German (de)
 * 
 * Implementation:
 * - SharedPreferences for persistence
 * - Configuration for locale
 * - Context wrapping for compatibility
 * - Locale.setDefault for system-wide effect
 */
public class LocaleHelper {
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_LANGUAGE = "language";

    /**
     * Set language preference and apply immediately
     * @param context Application context
     * @param languageCode Language code (en, am, de)
     */
    public static void setLocale(Context context, String languageCode) {
        // Save preference
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
        
        // Apply locale immediately
        applyLocale(context, languageCode);
    }

    /**
     * Apply saved locale from SharedPreferences
     * @param context Application context
     */
    public static void applySavedLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String lang = prefs.getString(KEY_LANGUAGE, "en");
        applyLocale(context, lang);
    }

    /**
     * Wrap context with saved locale configuration
     * Called in attachBaseContext for all activities
     * @param context Base context
     * @return Context with locale applied
     */
    public static Context wrap(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String lang = prefs.getString(KEY_LANGUAGE, "en");
        
        return createLocaleContext(context, lang);
    }

    /**
     * Create a new context with specified locale
     * @param context Base context
     * @param languageCode Language code
     * @return Context with locale applied
     */
    private static Context createLocaleContext(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return context;
        }
    }

    /**
     * Apply locale to current context resources
     * @param context Application context
     * @param languageCode Language code
     */
    private static void applyLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    /**
     * Get saved language preference
     * @param context Application context
     * @return Language code (default: "en")
     */
    public static String getSavedLanguage(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_LANGUAGE, "en");
    }
}
