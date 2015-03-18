package com.techmagic.locationapp.activity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.techmagic.locationapp.TrackGeofenceService;
import com.techmagic.locationapp.data.model.GeoPoint;
import com.techmagic.locationapp.fragment.GeoFenceMapFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.techmagic.hi.R;

public class TrackGeoFenceActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = TrackLocationActivity.class.getCanonicalName();
    private static final int REQUEST_RESOLVE_ERROR = 9999;
    private static final int AREA_RADIUS = 100;
    private static final int TRACKING_DURATION = 2 * 60 * 60 * 1000;

    private PendingIntent geofencePendingIntent;
    private GoogleApiClient googleApiClient ;
    private GeoFenceMapFragment geoFenceMapFragment;
    @InjectView(R.id.dl)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_geo_fence);
        ButterKnife.inject(this);

        initDrawerToggle(drawerLayout, R.string.title_activity_main);

        addGeoFenceMapFragment();
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

    private void addGeoFenceMapFragment() {
        geoFenceMapFragment = GeoFenceMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, geoFenceMapFragment)
                .commit();
    }

    private GeofencingRequest getGeofencingRequest() {
        List<GeoPoint> geoPoints = dataHelper.getAllGeoPoints();
        List<Geofence> geofences = new ArrayList<>();
        if (geoPoints != null && geoPoints.size() > 0) {
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
            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            builder.addGeofences(geofences);
            return builder.build();
        }
        return null;
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, TrackGeofenceService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void startTrackingGeofences() {
        GeofencingRequest request = getGeofencingRequest();
        if (request == null) {
            Toast.makeText(this, "No geo fence chosen to track", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            securityException.printStackTrace();
        }
    }

    public void stopTrackingGeofences() {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            return;
        }
        LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(this);
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

    public void connectGoogleApiClient() {
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

    public void addGeoPoint(GeoPoint geoPoint) {
        dataHelper.saveGeoPoint(geoPoint);
        if (geoFenceMapFragment != null) {
            geoFenceMapFragment.refreshGeoPointsMap();
        }
    }

    public boolean nameExists(String name) {
        if (geoFenceMapFragment != null) {
            return geoFenceMapFragment.nameExists(name);
        }
        return false;
    }

}

