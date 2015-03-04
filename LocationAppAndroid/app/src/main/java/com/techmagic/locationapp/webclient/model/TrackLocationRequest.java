package com.techmagic.locationapp.webclient.model;

import com.google.gson.annotations.SerializedName;
import com.techmagic.locationapp.TrackLocationApplication;

import java.util.List;

public class TrackLocationRequest {

    @SerializedName("device_id")
    public static String deviceId;
    @SerializedName("track")
    private List<LatLonTime> locations;

    public List<LatLonTime> getLocations() {
        return locations;
    }

    public void setLocations(List<LatLonTime> locations) {
        this.locations = locations;
    }
}
