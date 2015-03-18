package com.techmagic.locationapp.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techmagic.locationapp.activity.TrackGeoFenceActivity;
import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.GeoPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.techmagic.hi.R;

public class GeoFenceMapFragment extends Fragment {

    private static final int ZOOM_LEVEL = 15;

    private TrackGeoFenceActivity activity;
    private MapFragment mapFragment;
    private Map<String, GeoPoint> geoPointsMap;
    private DataHelper dataHelper;
    @InjectView(R.id.btn_toggle_tracking)
    Button btnToggleTracking;
    @InjectView(R.id.btn_stop_tracking)
    Button btnStopTracking;

    public static GeoFenceMapFragment newInstance() {
        GeoFenceMapFragment fragment = new GeoFenceMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public GeoFenceMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (TrackGeoFenceActivity) activity;
        dataHelper = DataHelper.getInstance(activity.getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo_fence, null);
        ButterKnife.inject(this, view);

        mapFragment = (MapFragment) activity.getFragmentManager().findFragmentById(R.id.map);
        setupMap();
        refreshGeoPointsMap();

        return view;
    }

    @OnClick(R.id.btb_clear_geo_points)
    public void clearGeoPoints() {
        dataHelper.deleteAllGeoPoints();
        refreshGeoPointsMap();
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

    private void setupMap() {
        GoogleMap map = mapFragment.getMap();
        map.setMyLocationEnabled(true);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showAddPointDialog(latLng);
            }
        });
    }

    public void refreshGeoPointsMap() {
        List<GeoPoint> geoPointsList = dataHelper.getAllGeoPoints();
        geoPointsMap = new HashMap<>();
        if (geoPointsList != null) {
            for (GeoPoint p : geoPointsList) {
                geoPointsMap.put(p.getName(), p);
            }
        }
        showMarkersOnMap(geoPointsList);
    }

    private void showMarkersOnMap(List<GeoPoint> geoPointsList) {
        mapFragment.getMap().clear();

        GoogleMap map = mapFragment.getMap();

        if (geoPointsList == null || !(geoPointsList.size() > 0)) {
            Location myLocation = map.getMyLocation();
            if (myLocation != null) {
                map.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), ZOOM_LEVEL));
            }
            Toast.makeText(activity, "No points to display", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoPoint locationToZoom = geoPointsList.get(0);
        map.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(locationToZoom.getLatitude(), locationToZoom.getLongitude()), ZOOM_LEVEL));

        for (GeoPoint d : geoPointsList) {
            LatLng position = new LatLng(d.getLatitude(), d.getLongitude());
            map.addCircle(new CircleOptions()
                    .center(position)
                    .radius(d.getRadius())
                    .strokeColor(getResources().getColor(R.color.color_map_circle))
                    .fillColor(getResources().getColor(R.color.color_map_circle)));
            MarkerOptions mo = new MarkerOptions();
            mo.position(position)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_transparent));
            map.addMarker(mo).setTitle(d.getName());
        }
    }

    private void showAddPointDialog(LatLng latLng) {
        DialogFragment fragment = AddGeoPointDialogFragment.getInstance(latLng);
        fragment.show(activity.getSupportFragmentManager(), null);
    }

    public boolean nameExists(String name) {
        return geoPointsMap.containsKey(name);
    }

}
