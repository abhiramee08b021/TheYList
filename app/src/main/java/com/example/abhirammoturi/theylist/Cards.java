package com.example.abhirammoturi.theylist;

/**
 * Created by Abhiram Moturi on 1/16/2018.
 */

public class Cards {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Cards(String userId, String name, String profileImageUrl, String gender) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
