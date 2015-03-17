package com.techmagic.locationapp.data;

import android.net.Uri;

public class Data {

    private static final Uri URI_MAIN = Uri.parse("content://com.techmagic.locationapp");

    public static final class LocationData {
        public static final String TABLE = "LocationData";
        public static final Uri URI = Uri.withAppendedPath(URI_MAIN, TABLE);

        public static final String COLUMN_LATITUDE = "COLUMN_LATITUDE";
        public static final String COLUMN_LONGITUDE = "COLUMN_LONGITUDE";
        public static final String COLUMN_TIMESTAMP = "COLUMN_TIMESTAMP";
        public static final String COLUMN_SYNCED = "COLUMN_SYNCED";
    }

    public static final class GeoPoint {
        public static final String TABLE = "GeoPoint";
        public static final Uri URI = Uri.withAppendedPath(URI_MAIN, TABLE);

        public static final String COLUMN_NAME = "COLUMN_NAME";
        public static final String COLUMN_LATITUDE = "COLUMN_LATITUDE";
        public static final String COLUMN_LONGITUDE = "COLUMN_LONGITUDE";
        public static final String COLUMN_RADIUS = "COLUMN_RADIUS";
    }

}
