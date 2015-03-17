package co.techmagic.hi.webclient.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import co.techmagic.hi.data.model.HiFriendRecord;

public class FriendsPushResponse {

    @SerializedName("friends")
    private List<HiFriendRecord> hiFriendRecords;

    public List<HiFriendRecord> getHiFriendRecords() {
        return hiFriendRecords;
    }

    public void setHiFriendRecords(List<HiFriendRecord> hiFriendRecords) {
        this.hiFriendRecords = hiFriendRecords;
    }
}
