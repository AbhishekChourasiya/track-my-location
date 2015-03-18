package com.techmagic.locationapp.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.techmagic.locationapp.activity.TrackGeoFenceActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.techmagic.hi.R;

public class TrackGeoFenceFragment extends Fragment {

    private TrackGeoFenceActivity activity;
    @InjectView(R.id.btn_toggle_tracking)
    Button btnToggleTracking;
    @InjectView(R.id.btn_stop_tracking)
    Button btnStopTracking;

    public TrackGeoFenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (TrackGeoFenceActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_geo_fence, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @OnClick(R.id.btn_toggle_tracking)
    public void startTracking() {
        activity.connectGoogleApiClient();
    }

    @OnClick(R.id.btn_stop_tracking)
    public void stopTracking() {
        activity.stopTrackingGeofences();
    }

    private void refreshUI() {
        //TODO
//        if (TrackLocationService.isServiceRunning()) {
//            btnToggleTracking.setText(R.string.btn_stop_tracking);
//        } else {
//            btnToggleTracking.setText(R.string.btn_start_tracking);
//        }
    }

}
