package com.techmagic.locationapp.webclient;

import android.util.Log;

import com.techmagic.locationapp.webclient.model.TrackLocationRequest;
import com.techmagic.locationapp.webclient.model.TrackLocationResponse;

import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.POST;

public class TrackLocationClient implements ITrackLocationClient {

    private static final String TAG = TrackLocationClient.class.getCanonicalName();
    private static final String API_URL = "http://10.3.1.115:5000";
    private TrackApi trackApi;

    public TrackLocationClient() {
        TrackLocationErrorHandler trackLocationErrorHandler = new TrackLocationErrorHandler();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setErrorHandler(trackLocationErrorHandler)
                .build();
        trackApi = restAdapter.create(TrackApi.class);
    }

    interface TrackApi {
        @POST("/track/add")
        TrackLocationResponse addTrack(@Body TrackLocationRequest request);
    }

    public TrackLocationResponse addTrack(TrackLocationRequest request) {
        TrackLocationResponse response = trackApi.addTrack(request);
        Log.d(TAG, "response status : " + response != null ?
                String.valueOf(response.getStatus()) : String.valueOf(0));
        return response;
    }

}
