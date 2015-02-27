package com.techmagic.locationapp.data;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.techmagic.locationapp.data.model.LocationData;

import java.util.List;

public class DataHelper {

    private static DataHelper instance;

    private DataHelper() {

    }

    public static DataHelper getInstance() {
        if (instance == null) {
            instance = new DataHelper();
        }
        return instance;
    }

    public void saveLocation(LocationData data) {
        data.save();
    }

    public List<LocationData> getAllLocations() {
        List<LocationData> data = new Select().from(LocationData.class).execute();
        return data;
    }

    public void deleteAllLocations() {
        new Delete().from(LocationData.class).execute();
    }

}
