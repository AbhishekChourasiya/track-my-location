package com.techmagic.locationapp.activity;

import android.app.DialogFragment;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.techmagic.locationapp.fragment.AddGeoPointDialogFragment;

import co.techmagic.hi.R;

public class AddGeoPointActivity extends ActionBarActivity {

    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geo_point);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        setupMap();
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

    private void showAddPointDialog(LatLng latLng) {
        DialogFragment fragment = AddGeoPointDialogFragment.getInstance(latLng);
        fragment.show(getFragmentManager(), null);
    }

}
