package com.techmagic.locationapp;

import android.location.Location;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.location.LocationRequest;

public class LocationApplication extends com.activeandroid.app.Application {

    private LocationRequestData locationRequestData;
    private Location startLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        setLocationRequestData(LocationRequestData.FREQUENCY_MEDIUM);
    }

    public void setLocationRequestData(LocationRequestData requestData) {
        locationRequestData = requestData;
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
        return locationRequest;
    }
}
