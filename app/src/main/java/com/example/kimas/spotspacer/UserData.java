package com.example.kimas.spotspacer;

public class UserData {
    private String userName;
    private String userId;
    private String customId;
    private String userUrl;
    private int userSpots;
    private int userThanks;
    private String objectId;


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getCustomId() {
        return customId;
    }

    public void setUserSpots(int userSpots) {
        this.userSpots = userSpots;
    }

    public int getUserSpots() {
        return userSpots;
    }

    public void setUserThanks(int userThanks) {
        this.userThanks = userThanks;
    }

    public int getUserThanks() {
        return userThanks;
    }
}