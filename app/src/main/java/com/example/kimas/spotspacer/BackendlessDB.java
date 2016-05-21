package com.example.kimas.spotspacer;

import android.content.Context;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.BackendlessGeoQuery;
import com.backendless.geo.GeoPoint;
import com.backendless.geo.Units;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BackendlessDB implements IBackendlessDB {
    public static final String[] CATEGORY = {"freeParking", "paidParking"};
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String STREET = "street";
    public static final String TIME_START = "timeStart";
    public static final String TIME_END = "timeEnd";
    public static final String PART_TIME = "partTime";
    public static final String WEEK_LIMIT = "weekLimitation";
    public static final String USER_ID = "userId";

    private Context ctx;

    public BackendlessDB(Context context) {
        ctx = context;
        Backendless.initApp(context, "F9FD0B3C-F5BA-2A22-FF4D-5F12A8B3E900", "0BBE7663-0185-D6CA-FFE1-3E2B61693C00", "v1");
    }

    @Override
    public void getNonParkingLists() {
        List<ParkingSpot> result = new LinkedList<>();
        BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
        geoQuery.addCategory(CATEGORY[1]);
        geoQuery.setIncludeMeta(true);
        Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
            @Override
            public void handleResponse(BackendlessCollection<GeoPoint> points) {
//                ((MainActivity) ctx).drawNonMarkers(convertData(points.getData()));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
            }
        });
    }

    @Override
    public void getFreeParkingLists() {
        List<ParkingSpot> result = new LinkedList<>();
        BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
        geoQuery.addCategory(CATEGORY[0]);
        geoQuery.setIncludeMeta(true);
        Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
            @Override
            public void handleResponse(BackendlessCollection<GeoPoint> points) {
//                ((MainActivity) ctx).drawFreeMarkers(convertData(points.getData()));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
            }
        });
    }

    @Override
    public void getNonParkingLists(double lat, double lon, double radius) {
        List<ParkingSpot> result = new LinkedList<>();
        BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
        geoQuery.setLatitude(lat);
        geoQuery.setLongitude(lon);
        geoQuery.setRadius(radius);
        geoQuery.setIncludeMeta(true);
        geoQuery.setUnits(Units.METERS);
        geoQuery.addCategory(CATEGORY[1]);
        geoQuery.setIncludeMeta(true);
        Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
            @Override
            public void handleResponse(BackendlessCollection<GeoPoint> points) {
//                ((MainActivity) ctx).drawNonMarkers(convertData(points.getData()));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
            }
        });
    }

    @Override
    public void getFreeParkingLists(double lat, double lon, double radius) {
        List<ParkingSpot> result = new LinkedList<>();
        BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
        geoQuery.setLatitude(lat);
        geoQuery.setLongitude(lon);
        geoQuery.setRadius(radius);
        geoQuery.setIncludeMeta(true);
        geoQuery.setUnits(Units.METERS);
        geoQuery.addCategory(CATEGORY[0]);
        geoQuery.setIncludeMeta(true);
        Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
            @Override
            public void handleResponse(BackendlessCollection<GeoPoint> points) {
//                ((MainActivity) ctx).drawFreeMarkers(convertData(points.getData()));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
            }
        });
    }

    @Override
    public void getParkingList(double lat, double lon, double radius) {
        List<ParkingSpot> result = new LinkedList<>();
        BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
        geoQuery.setLatitude(lat);
        geoQuery.setLongitude(lon);
        geoQuery.setRadius(radius);
        geoQuery.setIncludeMeta(true);
        geoQuery.setUnits(Units.METERS);
        geoQuery.addCategory(CATEGORY[0]);
        geoQuery.addCategory(CATEGORY[1]);
        Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
            @Override
            public void handleResponse(BackendlessCollection<GeoPoint> points) {
                ((MainActivity) ctx).drawMarkers(convertData(points.getData()));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
            }
        });
    }

    @Override
    public void getParkingList(double lat, double lon, double radius, List<String> categories) {
        List<ParkingSpot> result = new LinkedList<>();
        BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
        for (String category : categories) {
            geoQuery.addCategory(category);
        }
        geoQuery.setLatitude(lat);
        geoQuery.setLongitude(lon);
        geoQuery.setRadius(radius);
        geoQuery.setIncludeMeta(true);
        geoQuery.setUnits(Units.METERS);

        Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
            @Override
            public void handleResponse(BackendlessCollection<GeoPoint> points) {
                ((MainActivity) ctx).drawMarkers(convertData(points.getData()));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
            }
        });
    }

    @Override
    public void getParkingList(double lat, double lon, double radius, List<String> categories, Map<String, Object> meta) {
        List<ParkingSpot> result = new LinkedList<>();
        BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
        for (String category : categories) {
            geoQuery.addCategory(category);
        }

        geoQuery.setLatitude(lat);
        geoQuery.setLongitude(lon);
        geoQuery.setRadius(radius);
        geoQuery.setIncludeMeta(true);
        geoQuery.setUnits(Units.METERS);
        geoQuery.setMetadata(meta);

        Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
            @Override
            public void handleResponse(BackendlessCollection<GeoPoint> points) {
                ((MainActivity) ctx).drawMarkers(convertData(points.getData()));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
            }
        });
    }

    @Override
    public void saveParkingSpot(ParkingSpot parkingSpot) {
        List<String> categories = new LinkedList<>();
        Map<String, Object> meta = new HashMap<>();
        categories.add(parkingSpot.isFreeParking() ? CATEGORY[0] : CATEGORY[1]);
        fillMetaData(parkingSpot, meta);
        Backendless.Geo.savePoint(parkingSpot.getLatitude(), parkingSpot.getLongtitude(), categories, meta, new AsyncCallback<GeoPoint>() {
            @Override
            public void handleResponse(GeoPoint geoPoint) {
                System.out.println(geoPoint.getObjectId());
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    private void fillMetaData(ParkingSpot parkingSpot, Map<String, Object> meta) {
        if (parkingSpot.getCity() != null && !parkingSpot.getCity().isEmpty()) {
            meta.put(CITY, parkingSpot.getCity());
        }

        if (parkingSpot.getNational() != null && !parkingSpot.getNational().isEmpty()) {
            meta.put(COUNTRY, parkingSpot.getNational());
        }

        if (parkingSpot.getStreet() != null && !parkingSpot.getStreet().isEmpty()) {
            meta.put(STREET, parkingSpot.getStreet());
        }

        if (parkingSpot.getWeekLimit() != null && !parkingSpot.getWeekLimit().isEmpty()) {
            meta.put(WEEK_LIMIT, parkingSpot.getWeekLimit());
        }
        if (parkingSpot.getUserId() != null && !parkingSpot.getUserId().isEmpty())
            meta.put(USER_ID, parkingSpot.getUserId());

        if (!parkingSpot.isFreeParking() && parkingSpot.getStartFrom() != 0 && parkingSpot.getEndTo() != 0) {
            meta.put(TIME_START, parkingSpot.getStartFrom());
            meta.put(TIME_END, parkingSpot.getEndTo());
        } else if (parkingSpot.isLimitedTime()) {
            meta.put(PART_TIME, parkingSpot.getPartTime());
        }
    }

    private List<ParkingSpot> convertData(List<GeoPoint> data) {
        List<ParkingSpot> result = new LinkedList<ParkingSpot>();
        for (GeoPoint point : data) {
            ParkingSpot parkingSpot = new ParkingSpot();
            parkingSpot.setLatitude(point.getLatitude());
            parkingSpot.setLongtitude(point.getLongitude());
            Map<String, Object> mm = point.getMetadata();
            if (mm.get(CITY) != null)
                parkingSpot.setCity(mm.get(CITY).toString());
            if (mm.get(COUNTRY) != null)
                parkingSpot.setNational(mm.get(COUNTRY).toString());
            if (mm.get(STREET) != null)
                parkingSpot.setStreet(mm.get(STREET).toString());
            if (mm.get(USER_ID) != null)
                parkingSpot.setUserId(mm.get(USER_ID).toString());
            if (mm.get(PART_TIME) != null)
                parkingSpot.setPartTime(mm.get(PART_TIME).toString());
            if (mm.get(TIME_START) != null)
                parkingSpot.setStartFrom(Integer.valueOf((String) mm.get(TIME_START)));
            if (mm.get(TIME_END) != null)
                parkingSpot.setEndTo(Integer.valueOf((String) mm.get(TIME_END)));
            if (mm.get(WEEK_LIMIT) != null)
                parkingSpot.setWeekLimit(mm.get(WEEK_LIMIT).toString());

            result.add(parkingSpot);
        }
        return result;
    }


    //User stuff
    @Override
    public void checkUserData(String userid) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(userid);
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause.toString());
        Backendless.Data.of(UserData.class).find(dataQuery, new AsyncCallback<BackendlessCollection<UserData>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserData> responce) {
                List<UserData> nn = new ArrayList<>(responce.getData());
                String d = "";
//                if (nn.get(0) != null)
//                    d = nn.get(0).getUserName();
                Log.d("Size", "" + nn.size());
                if (nn.size() == 0)
                    ((AccountActivity) ctx).saveNewestUser();
                else
                    ((AccountActivity) ctx).updateCurrentUser(nn.get(0));

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
            }
        });
    }

    @Override
    public void getUserData(String objID) {
        if (!objID.isEmpty()) {
            Backendless.Persistence.of(UserData.class).findById(objID, new AsyncCallback<UserData>() {

                public void handleResponse(UserData response) {
                    if (response != null) {
                        ((AccountActivity) ctx).updateCurrentUser(response);
                    }
//                ((AccountActivity) ctx).saveUserInfo(convertData(points.getData()));
                }

                public void handleFault(BackendlessFault fault) {
                    System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
                }
            });
        }
    }

    @Override
    public void getBootUserData(String objID) {
        if (!objID.isEmpty()) {
            Backendless.Persistence.of(UserData.class).findById(objID, new AsyncCallback<UserData>() {

                public void handleResponse(UserData response) {
                    if (response != null) {
                        ((SplashScreen) ctx).updateCurrentUser(response);
                    }
//                ((AccountActivity) ctx).saveUserInfo(convertData(points.getData()));
                }

                public void handleFault(BackendlessFault fault) {
                    System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
                }
            });
        }
    }

    @Override
    public void setNewUserData(UserData userFellow) {
        Backendless.Persistence.save(userFellow, new AsyncCallback<UserData>() {
            public void handleResponse(UserData response) {
                ((AccountActivity) ctx).saveUserInfo(response.getObjectId());
            }

            public void handleFault(BackendlessFault fault) {
                System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
            }
        });
    }

    @Override
    public void updateUserData(UserData userFellowUp) {
        if (userFellowUp.getObjectId() != null && !userFellowUp.getObjectId().isEmpty()) {
            Backendless.Persistence.save(userFellowUp, new AsyncCallback<UserData>() {
                @Override
                public void handleResponse(UserData response) {
                    // Contact instance has been updated
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
                }
            });
        }
    }

    @Override
    public void setNewFavorite(String userId, ParkingSpot parkingSpot) {

    }

    @Override
    public void removeFavorite(String userId, String spotId) {

    }

    @Override
    public void getFavorites(String userId) {
        if (!userId.isEmpty()) {
            String whereClause = "userId = " + userId;
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause);

            Backendless.Persistence.of(Favorite.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Favorite>>() {

                public void handleResponse(BackendlessCollection<Favorite> response) {
                    if (response != null) {
                        ((MainActivity) ctx).saveFavorites(convertFav(response.getData()));
                    }
                }

                public void handleFault(BackendlessFault fault) {
                    System.err.println(String.format("searchByDateInRadius FAULT = %s", fault));
                }
            });
        }
    }

    private List<Favorite> convertFav(List<Favorite> data) {
        List<Favorite> result = new LinkedList<Favorite>();
        for (Favorite point : data) {
            Favorite favSpot = new Favorite();
            favSpot.setLatitude(point.getLatitude());
            favSpot.setLongtitude(point.getLongtitude());
            favSpot.setObjectId(point.getObjectId());
            if (point.getCity() != null)
                favSpot.setCity(point.getCity());
            if (point.getNational() != null)
                favSpot.setNational(point.getNational());
            if (point.getStreet() != null)
                favSpot.setStreet(point.getStreet());
            if (point.getPartTime() != null)
                favSpot.setPartTime(point.getPartTime());
            favSpot.setStartFrom(point.getStartFrom());
            favSpot.setEndTo(point.getEndTo());
            if (point.getWeekLimit() != null)
                favSpot.setWeekLimit(point.getWeekLimit());
            result.add(favSpot);
        }
        return result;
    }
}
