package com.techmagic.locationapp.webclient.model;

public class TrackLocationResponse {

    public static final int RESPONSE_CODE_OK = 200;

    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
