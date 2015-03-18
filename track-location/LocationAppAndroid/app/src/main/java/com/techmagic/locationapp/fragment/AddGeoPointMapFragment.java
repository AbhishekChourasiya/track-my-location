package com.techmagic.locationapp.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.GeoPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import co.techmagic.hi.R;

public class AddGeoPointMapFragment extends Fragment {

    private static final int ZOOM_LEVEL = 15;

    private MapFragment mapFragment;
    private Map<String, GeoPoint> geoPointsMap;
    private DataHelper dataHelper;

    public static AddGeoPointMapFragment newInstance() {
        AddGeoPointMapFragment fragment = new AddGeoPointMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddGeoPointMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataHelper = DataHelper.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_geopoint, null);
        ButterKnife.inject(this, view);

        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        setupMap();
        refreshGeoPointsMap();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapFragment != null){
            getActivity().getFragmentManager().beginTransaction()
                    .remove(mapFragment)
                    .commit();
        }
    }

    @OnClick(R.id.btb_clear_geo_points)
    public void clearGeoPoints() {
        dataHelper.deleteAllGeoPoints();
        refreshGeoPointsMap();
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

        if (geoPointsList == null || !(geoPointsList.size() > 0)) {
            Toast.makeText(getActivity(), "No points to display", Toast.LENGTH_SHORT).show();
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
        fragment.show(getActivity().getSupportFragmentManager(), null);
    }

    public boolean nameExists(String name) {
        return geoPointsMap.containsKey(name);
    }

}
