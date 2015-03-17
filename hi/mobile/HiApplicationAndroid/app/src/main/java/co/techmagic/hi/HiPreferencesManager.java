package co.techmagic.hi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import co.techmagic.hi.webclient.model.User;

public class HiPreferencesManager {

    private static final String TAG = "HiPreferencesManager";
    private static final String KEY_USER_FACEBOOK_ID = "KEY_USER_FACEBOOK_ID";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    private static final String KEY_IMAGE_URL = "KEY_IMAGE_URL";
    private static final String KEY_USER_GENDER = "KEY_USER_GENDER";

    public static String getFacebookId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String facebookId = preferences.getString(KEY_USER_FACEBOOK_ID, "");
        return facebookId;
    }

    public static User getUser(Context context) {
        User user = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.contains(KEY_USER_FACEBOOK_ID)) {
            String facebookId = preferences.getString(KEY_USER_FACEBOOK_ID, "");
            String name = preferences.getString(KEY_USER_NAME, "");
            String imageUrl = preferences.getString(KEY_IMAGE_URL, "");
            String gender = preferences.getString(KEY_USER_GENDER, "");
            user = new User(facebookId, name, imageUrl, gender);
            Log.d(TAG, "Get user " + user.toString());
        }
        return user;
    }

    public static void saveUser(User user, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_FACEBOOK_ID, user.getFacebookId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_IMAGE_URL, user.getImageUrl());
        editor.putString(KEY_USER_GENDER, user.getGender());
        editor.apply();
        Log.d(TAG, "Saved " + user.toString());
    }

    public static void deleteUser(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_USER_FACEBOOK_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_IMAGE_URL);
        editor.remove(KEY_USER_GENDER);
        editor.apply();
    }

}
