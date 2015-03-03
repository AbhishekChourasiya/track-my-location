package com.techmagic.locationapp.webclient.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrackLocationRequest {

    @SerializedName("device_id")
    private String deviceId;
    @SerializedName("track")
    private List<LatLonTime> locations;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<LatLonTime> getLocations() {
        return locations;
    }

    public void setLocations(List<LatLonTime> locations) {
        this.locations = locations;
    }
}
