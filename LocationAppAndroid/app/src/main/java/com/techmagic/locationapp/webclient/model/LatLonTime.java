package com.techmagic.locationapp.webclient.model;

import com.google.gson.annotations.SerializedName;

public class LatLonTime {

    public static LatLonTime getInstance(double lat, double lon, long time) {
        LatLonTime latLonTime = new LatLonTime();
        latLonTime.setLoc(TLLatLon.getInstance(lat, lon));
        latLonTime.setTime(time);
        return  latLonTime;
    }

    private LatLonTime() {

    }

    @SerializedName("loc")
    private TLLatLon loc;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public TLLatLon getLoc() {
        return loc;
    }

    public void setLoc(TLLatLon loc) {
        this.loc = loc;
    }
}
