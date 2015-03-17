package co.techmagic.hi.data;

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

    public static final class HiFriendRecord {
        public static final String TABLE = "HiFriendRecord";
        public static final Uri URI = Uri.withAppendedPath(URI_MAIN, TABLE);

        public static final String COLUMN_FACEBOOK_ID = "COLUMN_FACEBOOK_ID";
        public static final String COLUMN_NAME = "COLUMN_NAME";
        public static final String COLUMN_IMAGE_URL = "COLUMN_IMAGE_URL";
        public static final String COLUMN_GENDER = "COLUMN_GENDER";
        public static final String COLUMN_TIME = "COLUMN_TIME";
    }

}
