package com.techmagic.locationapp.webclient;

import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class TrackLocationErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        Log.d("handleError", "message is " + cause.getMessage());
        return new Exception(cause.getMessage());
    }

}
