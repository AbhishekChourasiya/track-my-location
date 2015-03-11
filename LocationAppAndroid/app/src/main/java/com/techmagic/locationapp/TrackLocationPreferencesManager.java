package com.techmagic.locationapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TrackLocationPreferencesManager {

    private static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    private static final String KEY_USER_NAME_CHOSEN = "KEY_USER_NAME_CHOSEN";

    public static void setDeviceId(String deviceId, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_DEVICE_ID, deviceId);
        editor.apply();
    }

    public static String getDeviceId(Context context) {
        String deviceId = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_DEVICE_ID, "");
        return deviceId;
    }

    public static void setUserName(String userName, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    public static String getUserName(Context context) {
        String userName = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_USER_NAME, "");
        return userName;
    }

    public static void setUserNameChosen(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(KEY_USER_NAME_CHOSEN, true);
        editor.apply();
    }

    public static boolean isUserNameChosen(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(KEY_USER_NAME_CHOSEN, false);
    }

}
