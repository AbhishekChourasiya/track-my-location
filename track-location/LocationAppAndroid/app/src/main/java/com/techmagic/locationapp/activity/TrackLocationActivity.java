package com.techmagic.locationapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.techmagic.locationapp.LocationRequestData;
import com.techmagic.locationapp.TrackLocationApplication;
import com.techmagic.locationapp.TrackLocationPreferencesManager;
import com.techmagic.locationapp.TrackLocationService;
import com.techmagic.locationapp.Utils;
import com.techmagic.locationapp.data.Data;
import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.LocationData;
import com.techmagic.locationapp.event.AppEvent;

import co.techmagic.hi.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class TrackLocationActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = TrackLocationActivity.class.getCanonicalName();
    private static final int REQUEST_RESOLVE_ERROR = 9999;
    private GoogleApiClient googleApiClient ;
    private TrackLocationApplication app;
    private Handler handler = new Handler();
    private AlertDialog dialogEditName;

    private ContentObserver contentObserver = new ContentObserver(handler) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            refreshUI();
        }
    };

    @InjectView(R.id.tv_last_update) TextView tvLastUpdate;
    @InjectView(R.id.radio_group) RadioGroup radioGroup;
    @InjectView(R.id.btn_toggle_tracking) Button btnToggleTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        if (!TrackLocationPreferencesManager.isUserNameChosen(getApplicationContext())) {
            showEditNameDialog();
        }

        app = (TrackLocationApplication) getApplication();

        setupRadioGroup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        registerContentObservers();
        refreshUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        unRegisterContentObservers();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_map_markers) {
            Intent i = new Intent(this, MapResultsActivity.class);
            i.putExtra(MapResultsActivity.EXTRA_MARKER_MODE, MapResultsActivity.MapMarkerMode.MARKER);
            startActivity(i);
            return true;
        } else if (id == R.id.action_map_circles) {
            Intent i = new Intent(this, MapResultsActivity.class);
            i.putExtra(MapResultsActivity.EXTRA_MARKER_MODE, MapResultsActivity.MapMarkerMode.CIRCLE);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void onEvent(AppEvent event) {
        switch (event) {
            case SERVICE_STATE_CHANGED:
                refreshUI();
                break;
        }

    }

    @OnClick(R.id.btn_toggle_tracking)
    public void toggleTracking() {
        if (TrackLocationService.isServiceRunning()) {
            stopTracking();
        } else {
            startTracking();
        }
    }

    @OnClick(R.id.btn_geofence)
    public void geofence() {
        Intent i = new Intent(this, TrackGeoFenceActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.btn_clear_data)
    public void clearData() {
        dataHelper.deleteAllLocations();
    }

    private void stopTracking() {
        stopService(new Intent(this, TrackLocationService.class));
    }

    private void startTracking() {
        connectGoogleApiClient();
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

    private void refreshUI() {
        LocationData location = dataHelper.getLastLocation();
        if (location != null) {
            String time = Utils.formatTime(location.getTimestamp());
            tvLastUpdate.setText(time);
        }

        if (TrackLocationService.isServiceRunning()) {
            btnToggleTracking.setText(R.string.btn_stop_tracking);
        } else {
            btnToggleTracking.setText(R.string.btn_start_tracking);
        }
    }

    private void setupRadioGroup() {

        switch (app.getLocationRequestData()) {
            case FREQUENCY_HIGH:
                radioGroup.check(R.id.radio_high);
                break;
            case FREQUENCY_MEDIUM:
                radioGroup.check(R.id.radio_medium);
                break;
            case FREQUENCY_MEDIUM_ONE:
                radioGroup.check(R.id.radio_medium_one);
                break;
            case FREQUENCY_MEDIUM_TWO:
                radioGroup.check(R.id.radio_medium_two);
                break;
            case FREQUENCY_LOW:
                radioGroup.check(R.id.radio_low_);
                break;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_high:
                        app.setLocationRequestData(LocationRequestData.FREQUENCY_HIGH);
                        break;
                    case R.id.radio_medium:
                        app.setLocationRequestData(LocationRequestData.FREQUENCY_MEDIUM);
                        break;
                    case R.id.radio_medium_one:
                        app.setLocationRequestData(LocationRequestData.FREQUENCY_MEDIUM_ONE);
                        break;
                    case R.id.radio_medium_two:
                        app.setLocationRequestData(LocationRequestData.FREQUENCY_MEDIUM_TWO);
                        break;
                    case R.id.radio_low_:
                        app.setLocationRequestData(LocationRequestData.FREQUENCY_LOW);
                        break;
                }
                stopTracking();
                startTracking();
            }
        });
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
            startTrackLocationService();
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

    private void showEditNameDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_name, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_name);
        editText.setText(TrackLocationPreferencesManager.getUserName(getApplicationContext()));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Button btnSubmit = dialogEditName.getButton(AlertDialog.BUTTON_POSITIVE);
                if (charSequence.length() == 0) {
                    btnSubmit.setEnabled(false);
                } else {
                    btnSubmit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit user name");
        builder.setView(view);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userName = editText.getText().toString();
                TrackLocationPreferencesManager.setUserName(userName, getApplicationContext());
                TrackLocationPreferencesManager.setUserNameChosen(getApplicationContext());
            }
        });
        dialogEditName = builder.create();
        dialogEditName.show();
    }

}
