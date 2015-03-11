package com.techmagic.locationapp.webclient.model;

import com.google.gson.annotations.SerializedName;
import com.techmagic.locationapp.TrackLocationApplication;
import com.techmagic.locationapp.data.model.LocationData;

import java.util.ArrayList;
import java.util.List;

public class TrackLocationRequest {

    @SerializedName("device_id")
    private String deviceId;
    @SerializedName("device_name")
    private String deviceName;
    @SerializedName("track")
    private List<LatLonTime> locations;

    public static TrackLocationRequest getInstance(List<LocationData> locations, String deviceId, String deviceName) {
        TrackLocationRequest request = new TrackLocationRequest();
        request.setDeviceId(deviceId);
        request.setDeviceName(deviceName);
        List<LatLonTime> latLonTimeList = new ArrayList<>();
        for (LocationData d : locations) {
            LatLonTime latLonTime = LatLonTime.getInstance(d.getLatitude(), d.getLongitude(), d.getTimestamp());
            latLonTimeList.add(latLonTime);
        }
        request.setLocations(latLonTimeList);
        return request;
    }

    public List<LatLonTime> getLocations() {
        return locations;
    }

    public void setLocations(List<LatLonTime> locations) {
        this.locations = locations;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
