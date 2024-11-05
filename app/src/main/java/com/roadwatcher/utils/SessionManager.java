package com.roadwatcher.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TOKEN = "token";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save user data
    public void createLoginSession(String userId, String token) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // Check user login
    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_TOKEN);
    }

    // Save user logged
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }


    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // delete user data
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
