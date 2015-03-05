package com.techmagic.locationapp;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static float distFromCoordinates(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        return dist;
    }

    public static String formatTime(long milis) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date(milis));
        return time;
    }

    public static String formatTimeWithoutSeconds(long milis) {
        String time = new SimpleDateFormat("HH:mm").format(new Date(milis));
        return time;
    }

    public static String formatDate(long milis) {
        String time = new SimpleDateFormat("MM-yyyy-dd").format(new Date(milis));
        return time;
    }

    public static String formatDateAndTime(long milis) {
        String time = new SimpleDateFormat("MMMM-yyyy-dd HH:mm").format(new Date(milis));
        return time;
    }

    public static String getUniqueDeviceId(Context context) {
        String myAndroidDeviceId = "";
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            myAndroidDeviceId = mTelephony.getDeviceId();
        } else {
            myAndroidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return myAndroidDeviceId;
    }

}
