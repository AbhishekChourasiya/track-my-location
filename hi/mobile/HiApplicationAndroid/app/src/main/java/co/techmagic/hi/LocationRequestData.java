package co.techmagic.hi;

import com.google.android.gms.location.LocationRequest;

public enum LocationRequestData {
    FREQUENCY_HIGH(5000, 5000, LocationRequest.PRIORITY_HIGH_ACCURACY, 1.0f),
    FREQUENCY_MEDIUM(5 * 60 * 1000, 5 * 60 * 1000, LocationRequest.PRIORITY_HIGH_ACCURACY, 100f),
    FREQUENCY_MEDIUM_ONE(15 * 60 * 1000, 5 * 60 * 1000, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 100f),
    FREQUENCY_MEDIUM_TWO(30 * 60 * 1000, 5 * 60 * 1000, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 100f),
    FREQUENCY_LOW(60 * 60 * 1000, 25 * 60 * 1000, LocationRequest.PRIORITY_LOW_POWER, 500f);

    LocationRequestData(int interval, int fastestInterval, int priority, float smallestDisplacement) {
        this.interval = interval;
        this.fastestInterval = fastestInterval;
        this.priority = priority;
        this.smallestDisplacement = smallestDisplacement;
    }

    private int interval;
    private int fastestInterval;
    private int priority;
    //meters
    private  float smallestDisplacement;

    public int getInterval() {
        return interval;
    }

    public int getFastestInterval() {
        return fastestInterval;
    }

    public int getPriority() {
        return priority;
    }

    public float getSmallestDisplacement() {
        return smallestDisplacement;
    }
}
