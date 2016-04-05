package com.example.kimas.spotspacer;

public class ParkingSpot {
    private String objectId;
    private String city;
    private String national;
    private String number;
    private double latitude;
    private double longtitude;
    private String userId;
    private boolean freeParking;
    private String street;
    private long startFrom;
    private long endTo;
    private boolean limitedTime;
    private String weekLimit;
    private String partTime;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNational() {
        return national;
    }

    public void setNational(String national) {
        this.national = national;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getPartTime() {
        return partTime;
    }

    public void setPartTime(String partTime) {
        this.partTime = partTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isFreeParking() {
        return freeParking;
    }

    public void setFreeParking(boolean freeParking) {
        this.freeParking = freeParking;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public long getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(long startFrom) {
        this.startFrom = startFrom;
    }

    public long getEndTo() {
        return endTo;
    }

    public void setEndTo(long endTo) {
        this.endTo = endTo;
    }

    public boolean isLimitedTime() {
        return limitedTime;
    }

    public void setLimitedTime(boolean limitedTime) {
        this.limitedTime = limitedTime;
    }

    public void setWeekLimit(String weekLimit){this.weekLimit = weekLimit; }

    public String getWeekLimit() {return weekLimit;}

}
