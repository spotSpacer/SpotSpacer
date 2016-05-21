package com.example.kimas.spotspacer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.util.Locale;


public class SplashScreen extends Activity {
    SharedPreferences sharedpreferences;
    public static final String SPOTSPACER_PREFERENCES = "spotSpacer_Prefs";
    public static final String firstStep = "firstStep";
    public static final String loginStatus = "loginStatus";
    public static final String custId = "custId";
    public static final String userTags = "userTags";
    public static final String userThanks = "userThanks";
    public static final String radius = "radius";
    public IBackendlessDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        sharedpreferences = getSharedPreferences(SPOTSPACER_PREFERENCES, Context.MODE_PRIVATE);
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = sharedpreferences.getString("language", "");
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
        if (isNetworkAvailable() && sharedpreferences.getBoolean(loginStatus, false)) {
            db = new BackendlessDB(this);
            db.getBootUserData(sharedpreferences.getString(custId, ""));
        }


        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    if (!sharedpreferences.getBoolean(firstStep,false)) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        putDouble(editor, radius, 500.0);
                        startActivity(new Intent(SplashScreen.this, FirstStepsActivity.class));
                    }
                    else {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    }
                }
            }
        };
        timer.start();
    }
    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
    public void updateCurrentUser(UserData userfellow) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(userTags, userfellow.getUserSpots());
        editor.putInt(userThanks, userfellow.getUserThanks());
        editor.putString(custId, userfellow.getObjectId());
        editor.apply();
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
