package co.techmagic.hi.webclient.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import co.techmagic.hi.data.model.LocationData;

public class TrackLocationRequest {

    @SerializedName("fb_id")
    private String facebookId;

    @SerializedName("track")
    private List<LatLonTime> locations;

    public static TrackLocationRequest getInstance(List<LocationData> locations, String facebookId) {
        TrackLocationRequest request = new TrackLocationRequest();
        request.setFacebookId(facebookId);
        List<LatLonTime> latLonTimeList = new ArrayList<>();
        for (LocationData d : locations) {
            LatLonTime latLonTime = LatLonTime.getInstance(d.getLatitude(), d.getLongitude(), d.getTimestamp());
            latLonTimeList.add(latLonTime);
        }
        request.setLocations(latLonTimeList);
        return request;
    }

    public List<LatLonTime> getLocations() {
        return locations;
    }

    public void setLocations(List<LatLonTime> locations) {
        this.locations = locations;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

}
