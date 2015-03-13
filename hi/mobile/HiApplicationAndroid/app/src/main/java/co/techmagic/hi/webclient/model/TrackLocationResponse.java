package co.techmagic.hi.webclient.model;

import java.util.List;

public class TrackLocationResponse {
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
