package com.techmagic.locationapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.LocationData;
import com.techmagic.locationapp.webclient.ITrackLocationClient;
import com.techmagic.locationapp.webclient.TrackLocationClient;
import com.techmagic.locationapp.webclient.model.TrackLocationRequest;
import com.techmagic.locationapp.webclient.model.TrackLocationResponse;
import co.techmagic.hi.R;

import java.util.List;

public class TrackLocationSyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        new AsyncTask<Void, Void, TrackLocationResponse>() {
            private List<LocationData> locations;
            private DataHelper dataHelper;

            @Override
            protected TrackLocationResponse doInBackground(Void[] params) {
                TrackLocationResponse response = null;
                dataHelper = DataHelper.getInstance(context);
                locations = dataHelper.getLocationsToSync();
                if (locations != null && locations.size() > 0) {
                    TrackLocationRequest request = TrackLocationRequest.getInstance(locations);
                    ITrackLocationClient client = new TrackLocationClient();
                    response = client.addTrack(request);
                    if (response != null && response.getStatus() == TrackLocationResponse.RESPONSE_CODE_OK) {
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

                if (response != null && response.getStatus() == TrackLocationResponse.RESPONSE_CODE_OK) {
                    String message = "Sync " + locations.size() + " items at " + Utils.formatTime(System.currentTimeMillis());
                    updateNotification(message, context);
                }
            }
        }.execute();
    }

    private void updateNotification(String text, Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(text);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        mNotificationManager.notify(1234, notification);
    }
}
