package co.techmagic.hi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import co.techmagic.hi.data.DataHelper;
import co.techmagic.hi.data.model.LocationData;
import co.techmagic.hi.event.AppEvent;
import co.techmagic.hi.util.Utils;
import co.techmagic.hi.webclient.HiClient;
import co.techmagic.hi.webclient.IHiClient;
import co.techmagic.hi.webclient.model.TrackLocationRequest;
import co.techmagic.hi.webclient.model.TrackLocationResponse;
import de.greenrobot.event.EventBus;

public class TrackLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static boolean isServiceRunning;
    private static final String TAG = TrackLocationService.class.getCanonicalName();
    private int notificationId = 9999;
    private GoogleApiClient googleApiClient;

    private HiApplication app;

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

        app = (HiApplication) getApplication();
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

        TrackLocationService.setIsServiceRunning(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateLocationData(location);
        synchronizeData();
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
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    private void updateLocationData(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String timeText = "Location update at " + Utils.formatTime(System.currentTimeMillis());

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

    private void synchronizeData() {
        new AsyncTask<Void, Void, TrackLocationResponse>() {
            private List<LocationData> locations;
            private DataHelper dataHelper;

            @Override
            protected TrackLocationResponse doInBackground(Void[] params) {
                TrackLocationResponse response = null;
                dataHelper = DataHelper.getInstance(getApplicationContext());
                locations = dataHelper.getLocationsToSync();
                if (locations != null && locations.size() > 0) {
                    String facebookId = HiPreferencesManager.getFacebookId(getApplicationContext());
                    TrackLocationRequest request = TrackLocationRequest.getInstance(locations, facebookId);
                    IHiClient client = new HiClient();
                    response = client.addTrack(request);
                    if (response != null && response.getStatus() == IHiClient.RESPONSE_CODE_OK) {
                        Log.d("TrackLocationSync", "Synced " + locations.size() + " items");
                        dataHelper.markLocationsSynced(locations);
                    }
                } else {
                    Log.d("TrackLocationSync", "No data to be synced");
                }
                return response;
            }

            @Override
            protected void onPostExecute(TrackLocationResponse response) {
                super.onPostExecute(response);
                if (response != null && response.getStatus() == IHiClient.RESPONSE_CODE_OK
                        && locations != null && locations.size() > 0) {
                    String message = "Synchronized " + locations.size() + " items at " + Utils.formatTime(System.currentTimeMillis());
                    updateNotification(message);
                }
            }
        }.execute();
    }

}
