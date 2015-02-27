package com.techmagic.locationapp;

public enum LocationRequestData {
    FREQUENCY_HIGH(5000, 5000),
    FREQUENCY_MEDIUM(15 * 60 * 1000, 5 * 60 * 1000),
    FREQUENCY_LOW(60 * 60 * 1000, 25 * 60 * 1000);

    LocationRequestData(int interval, int fastestInterval) {
        this.interval = interval;
        this.fastestInterval = fastestInterval;
    }

    private int interval;
    private int fastestInterval;

    public int getInterval() {
        return interval;
    }

    public int getFastestInterval() {
        return fastestInterval;
    }
}
