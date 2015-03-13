package co.techmagic.hi;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.database.ContentObserver;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.techmagic.hi.adapter.HiFriendsAdapter;
import co.techmagic.hi.data.Data;
import co.techmagic.hi.data.DataHelper;
import co.techmagic.hi.data.model.HiFriendRecord;
import co.techmagic.hi.event.AppEvent;
import co.techmagic.hi.webclient.model.User;
import co.techmagic.hi.util.HiParseUtil;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final int REQUEST_RESOLVE_ERROR = 9999;

    private GoogleApiClient googleApiClient ;
    private User user;
    private Handler handler = new Handler();
    private HiFriendsAdapter hiFriendsAdapter;

    private ContentObserver contentObserver = new ContentObserver(handler) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            refreshHiFriendsRecords();
        }
    };


    @InjectView(R.id.btn_toggle_tracking)
    Button btnToggleTracking;
    @InjectView(R.id.rv_friends)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
        refreshUI();
        EventBus.getDefault().register(this);
        registerContentObservers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        unRegisterContentObservers();
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

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        hiFriendsAdapter = new HiFriendsAdapter(getApplicationContext());
        recyclerView.setAdapter(hiFriendsAdapter);

        List<HiFriendRecord> friends = DataHelper.getInstance(getApplicationContext()).getAllHiFriendRecords();
        hiFriendsAdapter.refresh(friends);
    }

    public void onEvent(AppEvent event) {
        switch (event) {
            case SERVICE_STATE_CHANGED:
                refreshUI();
                break;
        }
    }

    private void checkLogin() {
        if ((user = HiPreferencesManager.getUser(getApplicationContext())) == null) {
            showLoginActivity();
        }
    }

    @OnClick(R.id.btn_logout)
    public void logout() {
        HiParseUtil.unSubscribePushes(HiParseUtil.getChanelNameByFacebookId(user.getFacebookId()));
        HiPreferencesManager.deleteUser(getApplicationContext());
        stopTracking();
        logoutFacebook();
        showLoginActivity();
    }

    @OnClick(R.id.btn_toggle_tracking)
    public void toggleTracking() {
        if (TrackLocationService.isServiceRunning()) {
            stopTracking();
        } else {
            startTracking();
        }
    }

    private void stopTracking() {
        stopService(new Intent(this, TrackLocationService.class));
    }

    private void startTracking() {
        connectGoogleApiClient();
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

    private void startTrackLocationService() {
        startService(new Intent(this, TrackLocationService.class));
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

    private void refreshUI() {
        if (TrackLocationService.isServiceRunning()) {
            btnToggleTracking.setText(R.string.btn_stop_tracking);
        } else {
            btnToggleTracking.setText(R.string.btn_start_tracking);
        }
    }

    private void refreshHiFriendsRecords() {
        List<HiFriendRecord> hiFriendRecords = DataHelper.getInstance(getApplicationContext()).getAllHiFriendRecords();
        hiFriendsAdapter.refresh(hiFriendRecords);
    }

    private void registerContentObservers() {
        getContentResolver().registerContentObserver(Data.HiFriendRecord.URI, false, contentObserver);
    }

    private void unRegisterContentObservers() {
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    private void logoutFacebook() {
        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
        Session session = ParseFacebookUtils.getSession();
        if (session != null && !session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
        ParseUser.logOut();
    }

    private void showLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
