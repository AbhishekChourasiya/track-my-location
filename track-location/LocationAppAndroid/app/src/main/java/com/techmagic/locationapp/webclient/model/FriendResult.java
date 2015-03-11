package com.techmagic.locationapp.webclient.model;

import com.google.gson.annotations.SerializedName;

public class FriendResult {

    private String title;
    @SerializedName("device_name")
    private String deviceName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
