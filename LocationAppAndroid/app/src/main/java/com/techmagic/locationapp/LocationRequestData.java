package com.techmagic.locationapp;

import com.google.android.gms.location.LocationRequest;

public enum LocationRequestData {
    FREQUENCY_HIGH(5000, 5000, LocationRequest.PRIORITY_HIGH_ACCURACY),
    FREQUENCY_MEDIUM(5 * 60 * 1000, 5 * 60 * 1000, LocationRequest.PRIORITY_HIGH_ACCURACY),
    FREQUENCY_LOW(60 * 60 * 1000, 25 * 60 * 1000, LocationRequest.PRIORITY_LOW_POWER);

    LocationRequestData(int interval, int fastestInterval, int priority) {
        this.interval = interval;
        this.fastestInterval = fastestInterval;
        this.priority = priority;
    }

    private int interval;
    private int fastestInterval;
    private int priority;

    public int getInterval() {
        return interval;
    }

    public int getFastestInterval() {
        return fastestInterval;
    }

    public int getPriority() {
        return priority;
    }
}
