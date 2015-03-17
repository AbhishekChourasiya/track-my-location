package co.techmagic.hi.data.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.gson.annotations.SerializedName;

import co.techmagic.hi.data.Data;

public class HiFriendRecord extends Model {
    @SerializedName("fb_id")
    @Column(name = Data.HiFriendRecord.COLUMN_FACEBOOK_ID)
    private String facebookId;
    @SerializedName("name")
    @Column(name = Data.HiFriendRecord.COLUMN_NAME)
    private String name;
    @SerializedName("image_url")
    @Column(name = Data.HiFriendRecord.COLUMN_IMAGE_URL)
    private String imageUrl;
    @SerializedName("gender")
    @Column(name = Data.HiFriendRecord.COLUMN_GENDER)
    private String gender;
    @Column(name = Data.HiFriendRecord.COLUMN_TIME)
    private transient long time;

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
