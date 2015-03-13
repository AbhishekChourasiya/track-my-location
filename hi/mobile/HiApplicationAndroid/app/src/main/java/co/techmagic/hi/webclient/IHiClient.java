package co.techmagic.hi.webclient;


import co.techmagic.hi.webclient.model.User;
import co.techmagic.hi.webclient.model.SignInResponse;
import co.techmagic.hi.webclient.model.TrackLocationRequest;
import co.techmagic.hi.webclient.model.TrackLocationResponse;

public interface IHiClient {
    public static final int RESPONSE_CODE_OK = 200;

    TrackLocationResponse addTrack(TrackLocationRequest request);
    SignInResponse signIn(User request);
}
