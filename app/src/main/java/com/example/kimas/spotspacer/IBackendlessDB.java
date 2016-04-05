package com.example.kimas.spotspacer;

import java.util.List;
import java.util.Map;

public interface IBackendlessDB {
    void getNonParkingLists();

    void getFreeParkingLists();
    void getNonParkingLists( double lat, double lon, double radius);

    void getFreeParkingLists( double lat, double lon, double radius);
    void getParkingList(double lat, double lon, double radius);

    void getParkingList(double lat, double lon, double radius, List<String> categories);

    void getParkingList(double lat, double lon, double radius, List<String> categories, Map<String, Object> meta);

    void saveParkingSpot(ParkingSpot parkingSpot);

    void checkUserData(String userid);

    void getUserData(String objID);

    void getBootUserData(String objID);

    void setNewUserData(UserData userFellow);

    void updateUserData(UserData userFellow);
}
