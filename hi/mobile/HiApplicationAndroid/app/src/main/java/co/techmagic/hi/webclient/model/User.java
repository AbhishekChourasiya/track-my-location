package co.techmagic.hi.webclient.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("fb_id")
    private String facebookId;
    @SerializedName("name")
    private String name;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("gender")
    private String gender;

    public User(String facebookId, String name, String imageUrl, String gender) {
        this.facebookId = facebookId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.gender = gender;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "facebookId is " + facebookId
                + ", name is " + name
                + ", gender is " + gender;
    }

}
