package com.techmagic.locationapp.map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.activeandroid.Model;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techmagic.locationapp.R;
import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.LocationData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;


public class MapResultsActivity extends ActionBarActivity {

    private static final int ZOOM_LEVEL = 15;
    private MapFragment mapFragment;
    private Map<String, Integer> filterData;
    private String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        initFilterData();
        showData(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            showFilterDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showData(int lastMilliSeconds) {
        List<LocationData> locations = null;
        if (lastMilliSeconds == 0) {
            locations = DataHelper.getInstance(getApplicationContext()).getAllLocations();
        } else {
            locations = DataHelper.getInstance(getApplicationContext()).getLastLocations(lastMilliSeconds);
        }

        mapFragment.getMap().clear();

        if (locations == null || !(locations.size() > 0)) {
            Toast.makeText(this, "No locations to display", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationData locationToZoom = locations.get(0);
        mapFragment.getMap().moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(locationToZoom.getLatitude(), locationToZoom.getLongitude()), ZOOM_LEVEL));

        for (LocationData d : locations) {
            MarkerOptions mo = new MarkerOptions();
            mo.position(new LatLng(d.getLatitude(), d.getLongitude()));
            mapFragment.getMap().addMarker(mo);
        }
    }

    private void initFilterData() {
        filterData = new HashMap<String, Integer>();
        filterData.put(getString(R.string.one_hour), Integer.valueOf(60 * 60));
        filterData.put(getString(R.string.one_day), Integer.valueOf(24 * 60 * 60));
        filterData.put(getString(R.string.one_week), Integer.valueOf(7 * 24 * 60 * 60));
        Set<String> keys = filterData.keySet();
        items = keys.toArray(new String[keys.size()]);
    }

    private void showFilterDialog() {
        new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String key = items[which];
                        int value = filterData.get(key);
                        showData(value * 1000);
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
