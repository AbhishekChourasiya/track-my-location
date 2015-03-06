package com.techmagic.locationapp.webclient.model;

public class TLLatLon {
    private double lat;
    private double lon;

    public static TLLatLon getInstance(double lat, double lon) {
        TLLatLon loc = new TLLatLon();
        loc.setLat(lat);
        loc.setLon(lon);
        return loc;
    }

    private TLLatLon() {

    }

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
}
