package com.techmagic.locationapp.activity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.techmagic.locationapp.BaseActivity;
import com.techmagic.locationapp.TrackGeofenceService;
import com.techmagic.locationapp.TrackLocationApplication;
import com.techmagic.locationapp.data.model.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.techmagic.hi.R;



public class TrackGeoFenceActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final int REQUEST_RESOLVE_ERROR = 9999;
    private static final int AREA_RADIUS = 100;
    private static final int TRACKING_DURATION = 2 * 60 * 60 * 1000;

    private PendingIntent geofencePendingIntent;
    private GoogleApiClient googleApiClient ;

    @InjectView(R.id.btn_toggle_tracking)
    Button btnToggleTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_geo_fence);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_track_geo_fence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_geo_fence) {
            Intent i = new Intent(this, AddGeoPointActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == RESULT_OK) {
                connectGoogleApiClient();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        startTrackingGeofences();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed");
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {
            showErrorDialog(result.getErrorCode());
        }
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "Success");
        } else {
            Log.d(TAG, "Fail " + status.getStatusCode());
        }
    }

    @OnClick(R.id.btn_toggle_tracking)
    public void startTracking() {
        connectGoogleApiClient();
    }

    @OnClick(R.id.btn_stop_tracking)
    public void stopTracking() {
        stopTrackingGeofences();
    }

    private GeofencingRequest getGeofencingRequest() {
        List<GeoPoint> geoPoints = dataHelper.getAllGeoPoints();
        List<Geofence> geofences = new ArrayList<>();
        if (geoPoints != null) {
            for (GeoPoint p : geoPoints) {
                Geofence geofence = new Geofence.Builder()
                        .setRequestId(p.getName())
                        .setCircularRegion(
                                p.getLatitude(),
                                p.getLongitude(),
                                AREA_RADIUS
                        )
                        .setExpirationDuration(TRACKING_DURATION)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build();
                geofences.add(geofence);
            }
        }
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, TrackGeofenceService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void startTrackingGeofences() {
        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            securityException.printStackTrace();
        }
    }

    private void stopTrackingGeofences() {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            return;
        }
        LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    private void refreshUI() {
        //TODO
//        if (TrackLocationService.isServiceRunning()) {
//            btnToggleTracking.setText(R.string.btn_stop_tracking);
//        } else {
//            btnToggleTracking.setText(R.string.btn_start_tracking);
//        }
    }

    private int createGoogleApiClient() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (status) {
            case ConnectionResult.SUCCESS:
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                break;
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, REQUEST_RESOLVE_ERROR);
                dialog.show();
                break;
        }
        return status;
    }

    private void connectGoogleApiClient() {
        if (googleApiClient == null) {
            if (createGoogleApiClient() != ConnectionResult.SUCCESS) {
                return;
            }
        }

        if (!(googleApiClient.isConnected() || googleApiClient.isConnecting())) {
            googleApiClient.connect();
        } else {
            Log.d(TAG, "Client is connected");
            startTrackingGeofences();
        }
    }

    private void showErrorDialog(int errorCode) {
        //TODO add errors handling
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt("dialog_error", errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

}

