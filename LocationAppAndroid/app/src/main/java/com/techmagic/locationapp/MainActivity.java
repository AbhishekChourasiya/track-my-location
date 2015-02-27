package com.techmagic.locationapp;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.LocationData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final int REQUEST_RESOLVE_ERROR = 9999;
    private GoogleApiClient googleApiClient ;
    private LocationApplication app;
    private boolean resolvingError;

    private Location startLocation;

    @InjectView(R.id.tv_latitude) TextView tvLatitude;
    @InjectView(R.id.tv_longitude) TextView tvLongitude;
    @InjectView(R.id.tv_latitude_delta) TextView tvLatitudeDelta;
    @InjectView(R.id.tv_longitude_delta) TextView tvLongitudeDelta;
    @InjectView(R.id.tv_last_update) TextView tvLastUpdate;
    @InjectView(R.id.tv_distance) TextView tvDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        app = (LocationApplication) getApplication();
        app.setLocationRequestData(LocationRequestData.FREQUENCY_MEDIUM);

        createGoogleApiClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectGoogleApiClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            resolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                connectGoogleApiClient();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (startLocation == null) {
            startLocation = location;
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
        if (resolvingError) {
            return;
        } else if (result.hasResolution()) {
            try {
                resolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {
            showErrorDialog(result.getErrorCode());
            resolvingError = true;
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = app.createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);

        startService(new Intent(this, TrackLocationService.class));
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    private void updateLocationData(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            DataHelper.getInstance().saveLocation(LocationData.getInstance(latitude,longitude));

            tvLatitude.setText(String.valueOf(latitude));
            tvLongitude.setText(String.valueOf(longitude));
            double deltaLatitude = Math.abs(startLocation.getLatitude() - latitude);
            double deltaLongitude = Math.abs(startLocation.getLongitude() - longitude);
            tvLatitudeDelta.setText(String.format("%.9f", deltaLatitude));
            tvLongitudeDelta.setText(String.format("%.9f", deltaLongitude));

            String time = Utils.formatTime(System.currentTimeMillis());
            tvLastUpdate.setText(time);
            float distance = Utils.distFromCoordinates((float) startLocation.getLatitude(),
                    (float) startLocation.getLongitude(),
                    (float) latitude,
                    (float) longitude);
            String distanceText = String.format("%.2f m.", distance);
            tvDistance.setText(distanceText);
        }
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
            if (!(googleApiClient.isConnected() || googleApiClient.isConnecting())
                    && !resolvingError) {
                googleApiClient.connect();
            } else {
                Log.d(TAG, "Client is connected");
                startLocationUpdates();
            }
        } else {
            Log.d(TAG, "Client is null");
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
