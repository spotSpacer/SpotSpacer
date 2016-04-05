package com.example.kimas.spotspacer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    public IBackendlessDB db;
    public boolean firstboot = true;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    SupportMapFragment mFragment;
    Marker currLocationMarker;

    public GoogleApiClient client;
    private double Last_Lat = 0;
    private double Last_Lon = 0;

    private long[] startTime = new long[1];
    private long[] endTime = new long[1];
    private String daysLen;
    final private int ASK_PERMISSION_LOCATION = 123;
    final private String TAG = "places_api";
    private NavigationView navigationView;
    private View headerView;
    //issaugota informacija
    public static final String SPOTSPACER_PREFERENCES = "spotSpacer_Prefs";
    public static final String loginStatus = "loginStatus";
    public static final String userName = "userName";
    public static final String userAvatar = "userAvatar";
    public static final String userTags = "userTags";
    public static final String userThanks = "userThanks";
    public static final String userId = "userId";
    public static final String lastLon = "lastLong";
    public static final String lastLat = "lastLat";
    public static final String custId = "custId";
    public static final String gOrFb = "gOrFb";
    SharedPreferences sharedpreferences;
    private boolean searchActive = false;
    private static String userIdToken;
    private GoogleApiClient client2;
    private BottomSheetBehavior behavior;
    private BottomSheetBehavior netError;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    private TextView addPop;
    private TextView limTxt;
    private boolean isRegistered;
    private boolean stateChange = false;
    double radiusInMeters = 500.0;
    private TextView mPlaceDetailsText;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private TextView mPlaceAttribution;
    int strokeColor = 0xff33FF66;//outline
    int shadeColor = 0x4433FF66;//opaque fill
    private static Context ctx;
    // Add this inside your class
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();
            Boolean message = b.getBoolean("connected");
            Log.e("newmesage", "" + message);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

            View bottomSheet = coordinatorLayout.findViewById(R.id.internetError);
            netError = BottomSheetBehavior.from(bottomSheet);
            if (!message) {
                netError.setState(BottomSheetBehavior.STATE_EXPANDED);
                if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    fab.setTranslationY(-bottomSheet.getHeight() + 50);
                netError.setPeekHeight(bottomSheet.getHeight());
            } else {
                netError.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    fab.setTranslationY(-50);
                netError.setPeekHeight(0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new BackendlessDB(this);

        sharedpreferences = getSharedPreferences(SPOTSPACER_PREFERENCES, Context.MODE_PRIVATE);
        Last_Lat = getDouble(sharedpreferences, lastLat, 0);
        Last_Lon = getDouble(sharedpreferences, lastLon, 0);
        userIdToken = sharedpreferences.getString(userId, "0");

        addPop = (TextView) findViewById(R.id.textaddress); //adreso eilute
        limTxt = (TextView) findViewById(R.id.textLimit); //limituotu dienu eilute

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);
        buildGoogleApiClient();

        fab = (FloatingActionButton) findViewById(R.id.button_gps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabButton();
            }
        });
        fab.setTranslationY(-50);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);


        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
// The View with the BottomSheetBehavior
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float i = (bottomSheet.getHeight() * slideOffset);
                if (i >= ((fab.getHeight() / 2) + 119))
                    fab.setTranslationY(-i + (fab.getHeight() / 2) + 66);
                else
                    fab.setTranslationY(-50);
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    ///========================================================================================
    //Searchas
    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(), 0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());
                latLng = place.getLatLng();
                db.getParkingList(place.getLatLng().latitude, place.getLatLng().longitude, 1000);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude)).zoom(mMap.getCameraPosition().zoom).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                searchActive = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName().toString()).icon(getBitmapDescriptor(R.drawable.search_pin)));
                }else {
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.search_pin)));
                }
                CharSequence attributions = place.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                } else {

                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e("Place tag:", res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    ///========================================================================================
    //mygtukai pereiti i kitus activity ir atlikti task'us
    public void toogleButton(View view) {
        SwitchCompat toogler = (SwitchCompat) findViewById(R.id.switchRealTime);
        if (toogler.isChecked()) {
            toogler.setThumbResource(R.drawable.ic_realtime_24dp);
        } else {
            toogler.setThumbResource(R.drawable.ic_realtime_24dp_off);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                break;

            case MotionEvent.ACTION_UP:

                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public void markerWindow(Marker marker) {
        final CharSequence[] items = {getString(R.string.workDays), getString(R.string.weekends), getString(R.string.always)};
        RelativeLayout tt = (RelativeLayout) findViewById(R.id.timeTable);
        TextView cc = (TextView) findViewById(R.id.cityCountry);
        ImageView markerPoint = (ImageView) findViewById(R.id.imageMarker);
        TextView fromNum = (TextView) findViewById(R.id.textView5);
        TextView toNum = (TextView) findViewById(R.id.textView8);

        Gson gson = new GsonBuilder().create();
        ParkingSpot m = gson.fromJson(marker.getSnippet(), ParkingSpot.class);
        addPop.setText(marker.getTitle());
        cc.setText(m.getCity() + ", " + m.getNational());
        String s = "  ";
        if (m.getPartTime() != null && !m.getPartTime().isEmpty()) {
            s = getString(R.string.freeFor) + m.getPartTime() + "h";
            switch (m.getPartTime()) {
                case "1":
                    markerPoint.setImageResource(R.drawable.pin_free_1h);
//                    Picasso.with(this).load(R.drawable.pin_free_1h).into(markerPoint);
                    break;
                case "2":
                    markerPoint.setImageResource(R.drawable.pin_free_2h);
//                    Picasso.with(this).load(R.drawable.pin_free_2h).into(markerPoint);
                    break;
            }
        }
        if (m.getWeekLimit() != null && !m.getWeekLimit().isEmpty()) {
            switch (m.getWeekLimit()) {
                case "work days":
                    limTxt.setText(items[0] + " " + s);
                    break;
                case "weekends":
                    limTxt.setText(items[1] + " " + s);
                    break;
                case "both":
                    limTxt.setText(items[2] + " " + s);
                    break;
            }
        } else {
            limTxt.setText(R.string.freeText);
            markerPoint.setImageResource(R.drawable.pin_free);
//            Picasso.with(this).load(R.drawable.pin_free).into(markerPoint);
        }
        if (m.getStartFrom() != 0 && m.getEndTo() != 0) {
            fromNum.setText(printTime(m.getStartFrom(), DateFormat.is24HourFormat(this)));
            toNum.setText(printTime(m.getEndTo(), DateFormat.is24HourFormat(this)));
            tt.setVisibility(View.VISIBLE);
            markerPoint.setImageResource(R.drawable.pin_paid_time);
            // Picasso.with(this).load(R.drawable.pin_paid_time).into(markerPoint);
        } else
            tt.setVisibility(View.GONE);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(marker.getPosition()).zoom(mMap.getCameraPosition().zoom).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

//    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getMinimumWidth(),
//                vectorDrawable.getMinimumHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas();
//        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        canvas.drawBitmap(bitmap, 0, 0, null);
////        vectorDrawable.draw(canvas);
//        return bitmap;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        super.onOptionsItemSelected(item);
        boolean loginIs = sharedpreferences.getBoolean(loginStatus, false);
        if (id == R.id.action_search && isNetworkAvailable()) {
            openAutocompleteActivity();
            return true;
        }
        if (isNetworkAvailable()) {
            if (loginIs) {
                if (item.getItemId() == R.id.action_add) {
                    alertLimitationList();
                    return true;
                } else if (item.getItemId() == R.id.action_add_free) {
                    getCurrentLocationFree();
                    return true;
                }
            } else {
                Toast.makeText(this, R.string.needLogin, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //activitys navigator
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorite) {

            startActivity(new Intent(this, FavoritesActivity.class));
        } else if (id == R.id.nav_account) {

            startActivity(new Intent(this, AccountActivity.class));
        } else if (id == R.id.nav_settings) {

            startActivity(new Intent(this, Settings0Activity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().findItem(id).setChecked(false);
        return true;
    }

    //dabartines vietos suradimo mygtukas
    private void fabButton() {
        if (isNetworkAvailable()) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, R.string.accessDenied, Toast.LENGTH_LONG).show();
                return;
            }
            mMap.setMyLocationEnabled(true);
            Location myLocation = mMap.getMyLocation();


            if (myLocation != null) {
                double dLatitude = myLocation.getLatitude();
                double dLongitude = myLocation.getLongitude();
                db.getParkingList(Last_Lat, Last_Lon, 1000);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(dLatitude, dLongitude)).zoom(mMap.getCameraPosition().zoom).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                searchActive = false;
            } else
                Toast.makeText(MainActivity.this, R.string.unableLocation, Toast.LENGTH_SHORT).show();
        }
    }
    ///========================================================================================
    //write to DB when user adds new spot

    public void getCurrentLocation() {
        Location myLocation = mMap.getMyLocation();
        if (myLocation != null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
//            mMap.addMarker(new MarkerOptions().position(
//                    new LatLng(dLatitude, dLongitude)).title(getString(R.string.myLocation)).icon(BitmapDescriptorFactory.fromResource(R.drawable.paid_ic)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 15));

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ParkingSpot parkingSpot = new ParkingSpot();
            if (addresses != null) {
                String streetName = addresses.get(0).getAddressLine(0);
                String cityName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);
                parkingSpot.setStreet(streetName);
                parkingSpot.setCity(cityName);
                parkingSpot.setNational(countryName);
            }
            parkingSpot.setLatitude(dLatitude);
            parkingSpot.setLongtitude(dLongitude);
            parkingSpot.setFreeParking(false);//mokamas parkingas
            parkingSpot.setStartFrom(startTime[0]);//mokama nuo
            parkingSpot.setEndTo(endTime[0]);//mokama iki

            parkingSpot.setWeekLimit(daysLen);
            parkingSpot.setUserId(sharedpreferences.getString(custId, ""));
            db.saveParkingSpot(parkingSpot);
            Toast.makeText(MainActivity.this, R.string.spottedText, Toast.LENGTH_SHORT).show();
            mMap.clear();
            updateSpotsThanks(sharedpreferences.getInt(userTags, 0), sharedpreferences.getInt(userThanks, 0), true);
            db.getParkingList(Last_Lat, Last_Lon, 1000);

        } else {
            Toast.makeText(MainActivity.this, R.string.unableLocation, Toast.LENGTH_SHORT).show();
        }
    }

    public void getCurrentLocationHour(int h) {
        Location myLocation = mMap.getMyLocation();
        if (myLocation != null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
//            mMap.addMarker(new MarkerOptions().position(
//                    new LatLng(dLatitude, dLongitude)).title(getString(R.string.myLocation)).icon(BitmapDescriptorFactory.fromResource(R.drawable.paid_ic)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 15));


            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ParkingSpot parkingSpot = new ParkingSpot();
            if (addresses != null) {
                String streetName = addresses.get(0).getAddressLine(0);
                String cityName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);
                parkingSpot.setStreet(streetName);
                parkingSpot.setCity(cityName);
                parkingSpot.setNational(countryName);
            }
            parkingSpot.setLatitude(dLatitude);
            parkingSpot.setLongtitude(dLongitude);
            parkingSpot.setFreeParking(false);//mokamas parkingas
            parkingSpot.setLimitedTime(true);
            parkingSpot.setWeekLimit(daysLen);
            parkingSpot.setPartTime(h + "");
            parkingSpot.setUserId(sharedpreferences.getString(custId, ""));

            db.saveParkingSpot(parkingSpot);
            Toast.makeText(MainActivity.this, R.string.spottedText, Toast.LENGTH_SHORT).show();
            updateSpotsThanks(sharedpreferences.getInt(userTags, 0), sharedpreferences.getInt(userThanks, 0), true);
            db.getParkingList(Last_Lat, Last_Lon, 1000);
        } else {
            Toast.makeText(MainActivity.this, R.string.unableLocation, Toast.LENGTH_SHORT).show();
        }
    }

    public void getCurrentLocationFree() {
        Location myLocation = mMap.getMyLocation();
        if (myLocation != null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
//            mMap.addMarker(new MarkerOptions().position(
//                    new LatLng(dLatitude, dLongitude)).title(getString(R.string.myLocation)).icon(BitmapDescriptorFactory.fromResource(R.drawable.free_ic)));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 15));

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ParkingSpot parkingSpot = new ParkingSpot();
            if (addresses != null) {
                String streetName = addresses.get(0).getAddressLine(0);
                String cityName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);
                parkingSpot.setStreet(streetName);
                parkingSpot.setCity(cityName);
                parkingSpot.setNational(countryName);
            }
            parkingSpot.setLatitude(dLatitude);
            parkingSpot.setLongtitude(dLongitude);
            parkingSpot.setFreeParking(true);
            parkingSpot.setUserId(sharedpreferences.getString(custId, ""));
            db.saveParkingSpot(parkingSpot);
            Toast.makeText(MainActivity.this, R.string.freeAdded, Toast.LENGTH_SHORT).show();
            mMap.clear();
            updateSpotsThanks(sharedpreferences.getInt(userTags, 0), sharedpreferences.getInt(userThanks, 0), true);
            db.getParkingList(Last_Lat, Last_Lon, 1000);

        } else {
            Toast.makeText(MainActivity.this, R.string.unableLocation, Toast.LENGTH_SHORT).show();
        }
    }

    ///========================================================================================
    //Markeriu sudejimas
    public void drawMarkers(List<ParkingSpot> spots) {
        mMap.clear();
        int resMark = R.drawable.pin_free;
        for (ParkingSpot spot : spots) {
            String Json = new Gson().toJson(spot);
            if (spot.getPartTime() != null) {
                switch (spot.getPartTime()) {
                    case "1":
                        resMark = R.drawable.pin_free_1h;
                        break;
                    case "2":
                        resMark = R.drawable.pin_free_2h;
                        break;
                }
            } else if (spot.getStartFrom() != 0 && spot.getEndTo() != 0) {
                resMark = R.drawable.pin_paid_time;
            }else{
                resMark = R.drawable.pin_free;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLatitude(), spot.getLongtitude())).title(spot.getStreet()).snippet(Json).icon(getBitmapDescriptor(resMark)));
            }else {
                mMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLatitude(), spot.getLongtitude())).title(spot.getStreet()).snippet(Json).icon(BitmapDescriptorFactory.fromResource(resMark)));
            }
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerWindow(marker);
                return true;
            }
        });
    if(latLng!= null) {
            CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
            mMap.addCircle(circleOptions);
        }
    }

    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(id);

            int h = vectorDrawable.getMinimumHeight();
            int w = vectorDrawable.getMinimumWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bm);
            canvas.drawBitmap(bm, 0, 0, null);
//            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

    ///========================================================================================
    //Map ready
    @Override
    public void onMapReady(GoogleMap googleMap) {

        sendBroadcast(new Intent("broadcastInternet").putExtra("connected", isNetworkAvailable()));
//        db.getNonParkingLists(Last_Lat, Last_Lon, 1000);
//        db.getFreeParkingLists(Last_Lat, Last_Lon, 1000);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (Build.VERSION.SDK_INT >= 23) {
            int hasFineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCorseLocationPermision = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED && hasCorseLocationPermision != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel(getString(R.string.pleaseAllow),
                            new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            ASK_PERMISSION_LOCATION);
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                        ASK_PERMISSION_LOCATION);
                return;
            }

        }
        mMap.setMyLocationEnabled(true);
        if (Last_Lat != 0 && Last_Lon != 0)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Last_Lat, Last_Lon), 15));
        db.getParkingList(Last_Lat, Last_Lon, 1000);
    }


    @Override
    public void onStart() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) mGoogleApiClient.connect();
        super.onStart();
        client2.connect();
        if (!isRegistered) {
            registerReceiver(broadcastReceiver, new IntentFilter("broadcastInternet"));
            isRegistered = true;
        }
        headerUserUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        client2.disconnect();
        if (isRegistered) {
            unregisterReceiver(broadcastReceiver);
            isRegistered = false;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this,"buildGoogleApiClient",Toast.LENGTH_SHORT).show();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        sendBroadcast(new Intent("broadcastInternet").putExtra("connected", isNetworkAvailable()));
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        sendBroadcast(new Intent("broadcastInternet").putExtra("connected", isNetworkAvailable()));
        Toast.makeText(this, R.string.connectionError, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        sendBroadcast(new Intent("broadcastInternet").putExtra("connected", isNetworkAvailable()));
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        Last_Lat = location.getLatitude();
        Last_Lon = location.getLongitude();
        SharedPreferences.Editor editer = sharedpreferences.edit();
        putDouble(editer, lastLat, Last_Lat);
        putDouble(editer, lastLon, Last_Lon);
        editer.apply(); //jeigu neveiks buvusios pozicijos isaugojimas irasyti commit();
        //zoom to current position:
        if (!searchActive) {
            latLng = new LatLng(Last_Lat, Last_Lon);
            db.getParkingList(Last_Lat, Last_Lon, 1000);
        }
        if (firstboot) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(15).build();

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            firstboot = false;
        }
    }

    @Override

    public void onSaveInstanceState(Bundle myBundle) {
        super.onSaveInstanceState(myBundle);
    }

    public void updateSpotsThanks(int s, int t, boolean ss) {
        SharedPreferences.Editor edi = sharedpreferences.edit();
        UserData upU = new UserData();
        upU.setUserName(sharedpreferences.getString(userName, getString(R.string.userInfoName)));
        if (sharedpreferences.getString(gOrFb, "fb").equals("g"))
            upU.setUserId(sharedpreferences.getString(userId, ""));
        if (sharedpreferences.getString(gOrFb, "g").equals("fb"))
            upU.setCustomId(sharedpreferences.getString(userId, ""));
        upU.setUserUrl(sharedpreferences.getString(userAvatar, "none"));
        upU.setObjectId(sharedpreferences.getString(custId, ""));
        if (ss) {
            edi.putInt(userTags, s + 1);
            upU.setUserSpots(s + 1);
            upU.setUserThanks(t);
        } else {
            edi.putInt(userThanks, t + 1);
            upU.setUserSpots(s);
            upU.setUserThanks(t + 1);
        }
        db.updateUserData(upU);
        edi.commit();
    }
    ///========================================================================================
    //Dialog boxes

    public void alertLimitationList() {

        final CharSequence[] items = {getString(R.string.oneHour), getString(R.string.twoHours), getString(R.string.paidFromTo)};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.specifyLimit));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0: //1h
                        alertDaySpan(true, 1);
                        break;
                    case 1: //2h
                        alertDaySpan(true, 2);
                        break;
                    case 2: //from-to
                        alertDaySpan(false, 0);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        }).show();
    }

    public void alertDaySpan(final boolean hour, final int a) {

        final CharSequence[] items = {getString(R.string.workDays), getString(R.string.weekends), getString(R.string.always)};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.specifyLimit));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0: //work days
                        daysLen = "work days";
                        break;
                    case 1: //weekends
                        daysLen = "weekends";
                        break;
                    case 2: //always
                        daysLen = "both";
                        break;
                    default:
                        break;
                }
                if (hour) getCurrentLocationHour(a);
                else alertTimePickerFirst();
                dialog.dismiss();

            }
        }).show();
    }

    public void alertTimePickerFirst() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_picker, null, false);
        final TimePicker myTimePicker = (TimePicker) view
                .findViewById(R.id.myTimePicker);
        if (Build.VERSION.SDK_INT >= 23)
            myTimePicker.setMinute(0);
        else
            myTimePicker.setCurrentMinute(0);
        new AlertDialog.Builder(MainActivity.this).setView(view)
                .setTitle(getString(R.string.paySpotStartTime))
                .setPositiveButton(getString(R.string.goBut), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        if (Build.VERSION.SDK_INT >= 23) {
                            startTime[0] = timeStampMaker(myTimePicker.getHour(), myTimePicker.getMinute());
                        } else {
                            startTime[0] = timeStampMaker(myTimePicker.getCurrentHour(), myTimePicker.getCurrentMinute());
                        }
                        alertTimePickerLast();
                        dialog.cancel();

                    }

                }).show();
    }

    public void alertTimePickerLast() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_picker, null, false);
        final TimePicker myTimePicker = (TimePicker) view
                .findViewById(R.id.myTimePicker);
        if (Build.VERSION.SDK_INT >= 23)
            myTimePicker.setMinute(0);
        else
            myTimePicker.setCurrentMinute(0);
        new AlertDialog.Builder(MainActivity.this).setView(view)
                .setTitle(getString(R.string.paySpotEndTime))
                .setPositiveButton(getString(R.string.goBut), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        if (Build.VERSION.SDK_INT >= 23) {
                            endTime[0] = timeStampMaker(myTimePicker.getHour(), myTimePicker.getMinute());
                        } else {
                            endTime[0] = timeStampMaker(myTimePicker.getCurrentHour(), myTimePicker.getCurrentMinute());
                        }
                        getCurrentLocation();
                        dialog.cancel();
                    }

                }).show();
    }


    ///========================================================================================
    // USER headerio atnaujinimas
    private void headerUserUI() {
        TextView textView = (TextView) headerView.findViewById(R.id.textUserName);
        ImageView imgProfilePic = (ImageView) headerView.findViewById(R.id.imageAvatar);
        String textData = sharedpreferences.getString(userName, R.string.userInfoName + "");
        String avatarData = sharedpreferences.getString(userAvatar, "avatar");
        textView.setText(textData);

        Picasso.with(this)
                .load(avatarData)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(imgProfilePic);
    }

    //=======================================================================================
    //Permision result stuff
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ASK_PERMISSION_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, R.string.locationDenied, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.okButt), okListener)
                .setNegativeButton(getString(R.string.cancelButt), null)
                .create()
                .show();
    }

    ///========================================================================================
    //Some methods we need to use
    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public long timeStampMaker(int h, int m) {
        return (h * 3600) + (m * 60);
    }

    public String printTime(long timestamp, boolean is24form) {
        long hh = timestamp / 3600;
        long mm = timestamp % 3600 / 60;
        SimpleDateFormat f12h = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat f24h = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = f24h.parse(hh + ":" + mm + ":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (is24form) return f24h.format(d);
        return f12h.format(d);
    }

    public static Context getContext() {
        return ctx;
    }
}
