package com.techmagic.locationapp;

import android.content.Intent;
import android.content.IntentSender;
import android.database.ContentObserver;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.techmagic.locationapp.data.Data;
import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.LocationData;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final int REQUEST_RESOLVE_ERROR = 9999;
    private GoogleApiClient googleApiClient ;
    private LocationApplication app;
    private boolean resolvingError;
    private Handler handler = new Handler();

    private ContentObserver contentObserver = new ContentObserver(handler) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateLocationData();
        }
    };

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerContentObservers();
        updateLocationData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterContentObservers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            resolvingError = false;
            if (resultCode == RESULT_OK) {
                connectGoogleApiClient();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        startTrackLocationService();
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

    @OnClick(R.id.btn_start_tracking)
    public void startTracking() {
        if (googleApiClient == null) {
            createGoogleApiClient();
        }
        connectGoogleApiClient();
    }

    @OnClick(R.id.btn_stop_tracking)
    public void stopTracking() {
        stopService(new Intent(this, TrackLocationService.class));
    }

    private void startTrackLocationService() {
        startService(new Intent(this, TrackLocationService.class));
    }

    private void registerContentObservers() {
        getContentResolver().registerContentObserver(Data.LocationData.URI, false, contentObserver);
    }

    private void unRegisterContentObservers() {
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    private void updateLocationData() {
        DataHelper dataHelper = DataHelper.getInstance(this);
        LocationData location = dataHelper.getLastLocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            tvLatitude.setText(String.valueOf(latitude));
            tvLongitude.setText(String.valueOf(longitude));
            String time = Utils.formatTime(location.getTimestamp());
            tvLastUpdate.setText(time);
            Location startLocation = app.getStartLocation();
            if (startLocation != null) {
                double deltaLatitude = Math.abs(startLocation.getLatitude() - latitude);
                double deltaLongitude = Math.abs(startLocation.getLongitude() - longitude);
                tvLatitudeDelta.setText(String.format("%.9f", deltaLatitude));
                tvLongitudeDelta.setText(String.format("%.9f", deltaLongitude));
                float distance = Utils.distFromCoordinates((float) startLocation.getLatitude(),
                        (float) startLocation.getLongitude(),
                        (float) latitude,
                        (float) longitude);
                String distanceText = String.format("%.2f m.", distance);
                tvDistance.setText(distanceText);
            }
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
                startTrackLocationService();
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
