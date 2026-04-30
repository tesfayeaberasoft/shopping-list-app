package com.shoppinglist.auth;

import android.content.Context;
import android.content.SharedPreferences;
import com.shoppinglist.models.User;

public class SessionManager {
    private static final String PREF_NAME = "ShoppingListSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final String KEY_PHOTO_URL = "photoUrl";
    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_DISPLAY_NAME, user.getDisplayName());
        editor.putString(KEY_PHOTO_URL, user.getPhotoUrl());
        editor.apply();
    }

    public User getUser() {
        String id = prefs.getString(KEY_USER_ID, null);
        if (id == null) return null;
        String email = prefs.getString(KEY_EMAIL, "");
        String displayName = prefs.getString(KEY_DISPLAY_NAME, "");
        String photoUrl = prefs.getString(KEY_PHOTO_URL, "");
        return new User(id, email, displayName, photoUrl);
    }

    public boolean isLoggedIn() {
        return prefs.getString(KEY_USER_ID, null) != null;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    // Update only display name and photo
    public void updateProfile(String displayName, String photoUrl) {
        prefs.edit().putString(KEY_DISPLAY_NAME, displayName).putString(KEY_PHOTO_URL, photoUrl).apply();
    }
}