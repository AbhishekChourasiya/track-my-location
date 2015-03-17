package co.techmagic.hi.webclient;

import android.util.Log;

import java.lang.reflect.UndeclaredThrowableException;

import co.techmagic.hi.webclient.model.User;
import co.techmagic.hi.webclient.model.SignInResponse;
import co.techmagic.hi.webclient.model.TrackLocationRequest;
import co.techmagic.hi.webclient.model.TrackLocationResponse;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

public class HiClient implements IHiClient {

    private static final String TAG = HiClient.class.getCanonicalName();
    private static final String API_URL = "http://hi-track-location.herokuapp.com";
    private TrackApi trackApi;

    public HiClient() {
        HiErrorHandler trackLocationErrorHandler = new HiErrorHandler();
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
        @Headers("Content-Type: application/json")
        @POST("/_s/sign_in/fb")
        SignInResponse signIn(@Body User request);
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

    @Override
    public SignInResponse signIn(User request) {
        SignInResponse response = null;
        try {
            response = trackApi.signIn(request);
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
