package com.techmagic.locationapp;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.location.LocationRequest;

public class LocationApplication extends com.activeandroid.app.Application {

    private LocationRequestData locationRequestData;

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

    public LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(locationRequestData.getInterval());
        locationRequest.setFastestInterval(locationRequestData.getFastestInterval());
        locationRequest.setPriority(locationRequestData.getPriority());
        return locationRequest;
    }
}
