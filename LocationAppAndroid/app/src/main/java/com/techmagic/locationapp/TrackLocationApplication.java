package com.techmagic.locationapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.LocationRequest;
import com.techmagic.locationapp.data.model.LocationData;
import com.techmagic.locationapp.webclient.model.TrackLocationRequest;

import io.fabric.sdk.android.Fabric;

public class TrackLocationApplication extends Application {

    private static final String KEY_REQUEST_DATA_NAME = "KEY_REQUEST_DATA_NAME";
    private LocationRequestData locationRequestData;
    private Location startLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        TrackLocationRequest.deviceId = Utils.getUniqueDeviceId(getApplicationContext());
        if (!retrieveLocationRequestData()) {
            setLocationRequestData(LocationRequestData.FREQUENCY_MEDIUM);
        }
        initializeDB();
    }

    private boolean retrieveLocationRequestData() {
        String name = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_REQUEST_DATA_NAME, null);
        if (!TextUtils.isEmpty(name)) {
            LocationRequestData data = LocationRequestData.valueOf(name);
            if (data != null) {
                locationRequestData = data;
                return true;
            }
        }
        return false;
    }

    public void setLocationRequestData(LocationRequestData requestData) {
        locationRequestData = requestData;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString(KEY_REQUEST_DATA_NAME, requestData.name()).apply();
    }

    public LocationRequestData getLocationRequestData() {
        return locationRequestData;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(locationRequestData.getInterval());
        locationRequest.setFastestInterval(locationRequestData.getFastestInterval());
        locationRequest.setPriority(locationRequestData.getPriority());
        locationRequest.setSmallestDisplacement(locationRequestData.getSmallestDisplacement());
        return locationRequest;
    }

    protected void initializeDB() {
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        configurationBuilder.addModelClasses(LocationData.class);
        ActiveAndroid.initialize(configurationBuilder.create());
    }

}
