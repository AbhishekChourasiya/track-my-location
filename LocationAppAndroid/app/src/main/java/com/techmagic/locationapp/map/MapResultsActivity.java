package com.techmagic.locationapp.map;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.Model;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techmagic.locationapp.R;
import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.LocationData;

import java.util.List;

import butterknife.ButterKnife;


public class MapResultsActivity extends ActionBarActivity {

    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
    }

    private void showData() {
        List<LocationData> locations = DataHelper.getInstance(getApplicationContext()).getLastLocations(10);
        if (locations == null || !(locations.size() > 0)) {
            return;
        }
        LocationData locationToZoom = locations.get(0);
        mapFragment.getMap().moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(locationToZoom.getLatitude(), locationToZoom.getLongitude()), 10));
        for (LocationData d : locations) {
            MarkerOptions mo = new MarkerOptions();
            mo.position(new LatLng(d.getLatitude(), d.getLongitude()));
            mapFragment.getMap().addMarker(mo);
        }
    }

}
