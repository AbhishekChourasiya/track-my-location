package co.techmagic.hi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
