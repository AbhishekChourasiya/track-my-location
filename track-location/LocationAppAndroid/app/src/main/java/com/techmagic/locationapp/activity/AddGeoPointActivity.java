package com.techmagic.locationapp.activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techmagic.locationapp.BaseActivity;
import com.techmagic.locationapp.data.model.GeoPoint;
import com.techmagic.locationapp.fragment.AddGeoPointDialogFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import co.techmagic.hi.R;

public class AddGeoPointActivity extends BaseActivity {

    private static final int ZOOM_LEVEL = 15;

    private MapFragment mapFragment;
    private Map<String, GeoPoint> geoPointsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geo_point);
        ButterKnife.inject(this);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        setupMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGeoPointsMap();
    }

    @OnClick(R.id.btb_clear_geo_points)
    public void clearGeoPoints() {
        dataHelper.deleteAllGeoPoints();
        refreshGeoPointsMap();
    }

    public void addGeoPoint(GeoPoint geoPoint) {
        dataHelper.saveGeoPoint(geoPoint);
        refreshGeoPointsMap();
    }

    public boolean nameExists(String name) {
        return geoPointsMap.containsKey(name);
    }

    private void setupMap() {
        GoogleMap map = mapFragment.getMap();

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showAddPointDialog(latLng);
            }
        });
    }

    private void refreshGeoPointsMap() {
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

        if (geoPointsList == null || !(geoPointsList.size() > 0)) {
            Toast.makeText(this, "No points to display", Toast.LENGTH_SHORT).show();
            return;
        }

        GoogleMap map = mapFragment.getMap();
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
        fragment.show(getFragmentManager(), null);
    }

}
