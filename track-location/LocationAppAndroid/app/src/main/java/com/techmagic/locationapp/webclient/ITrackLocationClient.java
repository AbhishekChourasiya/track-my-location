package com.techmagic.locationapp.webclient;

import com.techmagic.locationapp.webclient.model.TrackLocationRequest;
import com.techmagic.locationapp.webclient.model.TrackLocationResponse;

public interface ITrackLocationClient {
    TrackLocationResponse addTrack(TrackLocationRequest request);
}
