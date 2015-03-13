package co.techmagic.hi;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.google.android.gms.location.LocationRequest;
import com.parse.Parse;

import co.techmagic.hi.data.model.LocationData;

public class HiApplication extends Application {

    private LocationRequestData locationRequestData = LocationRequestData.FREQUENCY_MEDIUM_TWO;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this,
                "TMG49CgTdo2aVoCXf6YWTnA9zwiQa68aPDblnOJv",
                "hfM1gHyhvoWy389gSFBi3aKQx1awTxL5v9RlDujW"
        );

        initializeDB();
    }

    public LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(locationRequestData.getInterval());
        locationRequest.setFastestInterval(locationRequestData.getFastestInterval());
        locationRequest.setPriority(locationRequestData.getPriority());
        locationRequest.setSmallestDisplacement(locationRequestData.getSmallestDisplacement());
        return locationRequest;
    }

    private void initializeDB() {
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        configurationBuilder.addModelClasses(LocationData.class);
        ActiveAndroid.initialize(configurationBuilder.create());
    }
}
