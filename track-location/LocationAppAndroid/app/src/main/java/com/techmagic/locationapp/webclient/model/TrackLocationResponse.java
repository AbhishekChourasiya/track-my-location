package com.techmagic.locationapp.webclient.model;

import java.util.List;

public class TrackLocationResponse {

    public static final int RESPONSE_CODE_OK = 200;

    private int status;
    private String message;
    List<FriendResult> result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<FriendResult> getResult() {
        return result;
    }
}
