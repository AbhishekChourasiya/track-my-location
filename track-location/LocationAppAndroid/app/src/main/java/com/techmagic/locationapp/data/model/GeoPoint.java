package com.techmagic.locationapp.data.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.techmagic.locationapp.data.Data;

@Table(name = Data.GeoPoint.TABLE)
public class GeoPoint extends Model {
    @Column(name = Data.GeoPoint.COLUMN_NAME)
    private String name;
    @Column(name = Data.GeoPoint.COLUMN_LATITUDE)
    private double latitude;
    @Column(name = Data.GeoPoint.COLUMN_LONGITUDE)
    private double longitude;
    @Column(name = Data.GeoPoint.COLUMN_RADIUS)
    private int radius;

    public static GeoPoint getInstance(String name, double latitude, double longitude, int radius) {
        GeoPoint geoPoint = new GeoPoint();
        geoPoint.setName(name);
        geoPoint.setLatitude(latitude);
        geoPoint.setLongitude(longitude);
        geoPoint.setRadius(radius);
        return geoPoint;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
