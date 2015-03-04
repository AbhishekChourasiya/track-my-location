package com.techmagic.locationapp;

import android.test.InstrumentationTestCase;

import com.techmagic.locationapp.webclient.ITrackLocationClient;
import com.techmagic.locationapp.webclient.TrackLocationClient;
import com.techmagic.locationapp.webclient.model.LatLonTime;
import com.techmagic.locationapp.webclient.model.TrackLocationRequest;
import com.techmagic.locationapp.webclient.model.TrackLocationResponse;

import java.util.ArrayList;
import java.util.List;

public class ApiTest extends InstrumentationTestCase {

    public void testAddTrack() {
        ITrackLocationClient client = new TrackLocationClient();

        TrackLocationRequest request = new TrackLocationRequest();
        List<LatLonTime> locations = new ArrayList<LatLonTime>();
        locations.add(LatLonTime.getInstance(0, 0, System.currentTimeMillis()));

        request.setLocations(locations);

        TrackLocationResponse response = client.addTrack(request);
        assertNotNull(response);
        assertEquals(response.getStatus(), 200);
    }
}
