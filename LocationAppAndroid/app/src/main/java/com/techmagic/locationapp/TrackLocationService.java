package com.techmagic.locationapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.LocationData;
import com.techmagic.locationapp.event.AppEvent;
import co.techmagic.hi.R;

import de.greenrobot.event.EventBus;

public class TrackLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long SYNCHRONIZATION_INTERVAL = 60 * 60 * 1000;
    private static boolean isServiceRunning;
    private static final String TAG = TrackLocationService.class.getCanonicalName();
    private int notificationId = 9999;
    private GoogleApiClient googleApiClient;

    private TrackLocationApplication app;

    public static boolean isServiceRunning() {
        return isServiceRunning;
    }

    private static void setIsServiceRunning(boolean isServiceRunning) {
        TrackLocationService.isServiceRunning = isServiceRunning;
        EventBus.getDefault().post(AppEvent.SERVICE_STATE_CHANGED);
    }

    public TrackLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        app = (TrackLocationApplication) getApplication();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        createGoogleApiClient();
        connectGoogleApiClient();

        TrackLocationService.setIsServiceRunning(true);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        stopLocationUpdates();
        cancelNotification();
        app.setStartLocation(null);

        TrackLocationService.setIsServiceRunning(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        if (app.getStartLocation() == null) {
            app.setStartLocation(location);
        }
        updateLocationData(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved");
    }

    private void createGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void connectGoogleApiClient() {
        if (googleApiClient != null) {
            if (!(googleApiClient.isConnected() || googleApiClient.isConnecting())) {
                googleApiClient.connect();
            } else {
                Log.d(TAG, "Client is connected");
                startLocationUpdates();
            }
        } else {
            Log.d(TAG, "Client is null");
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = app.createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);

        scheduleDataSynchronization();
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        stopDataSynchronization();
    }

    private void scheduleDataSynchronization() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TrackLocationSyncReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                SYNCHRONIZATION_INTERVAL, alarmIntent);
    }

    private void stopDataSynchronization() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TrackLocationSyncReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.cancel(alarmIntent);
    }

    private void updateLocationData(Location location) {
        Location startLocation = app.getStartLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float distance = Utils.distFromCoordinates((float) startLocation.getLatitude(),
                (float) startLocation.getLongitude(),
                (float) latitude,
                (float) longitude);

        String distanceText = String.format("%.2f m.", distance);
        String timeText = Utils.formatTime(System.currentTimeMillis());

        DataHelper.getInstance(this).saveLocation(LocationData.getInstance(latitude, longitude));
        updateNotification(timeText);
    }

    private void updateNotification(String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(text);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        mNotificationManager.notify(notificationId, notification);
    }

    private void cancelNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }

}
