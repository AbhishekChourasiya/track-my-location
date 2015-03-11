package com.techmagic.locationapp.webclient;

import android.util.Log;

import com.techmagic.locationapp.webclient.model.TrackLocationRequest;
import com.techmagic.locationapp.webclient.model.TrackLocationResponse;

import java.lang.reflect.UndeclaredThrowableException;

import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

public class TrackLocationClient implements ITrackLocationClient {

    private static final String TAG = TrackLocationClient.class.getCanonicalName();
    private static final String API_URL = "http://track-my-location.herokuapp.com/";
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
        @Headers("Content-Type: application/json")
        @POST("/track/add")
        TrackLocationResponse addTrack(@Body TrackLocationRequest request);
    }

    public TrackLocationResponse addTrack(TrackLocationRequest request) {
        TrackLocationResponse response = null;
        try {
           response = trackApi.addTrack(request);
        } catch (UndeclaredThrowableException e) {
            e.printStackTrace();
        }

        if (response != null) {
            Log.d(TAG, "response status : " + String.valueOf(response.getStatus()));
        } else {
            Log.d(TAG, "response status : " + "error");
        }

        return response;
    }

}
