package com.techmagic.locationapp.webclient.model;

public class LatLonTime {

    public static LatLonTime getInstance(double lat, double lon, long time) {
        LatLonTime latLonTime = new LatLonTime();
        latLonTime.setLat(lat);
        latLonTime.setLon(lon);
        latLonTime.setTime(time);
        return  latLonTime;
    }

    private double lat;
    private double lon;
    private long time;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
