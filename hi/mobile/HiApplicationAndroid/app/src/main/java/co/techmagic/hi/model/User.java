package co.techmagic.hi.model;

public class User {
    private String facebookId;
    private String name;
    private Gender gender;

    public User(String facebookId, String name, Gender gender) {
        this.facebookId = facebookId;
        this.name = name;
        this.gender = gender;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "facebookId is " + facebookId
                + ", name is " + name
                + ", gender is " + gender.name();
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }

}
