package com.example.kimas.spotspacer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.util.Locale;

public class FirstStepsActivity extends AppIntro {
    SharedPreferences sharedpreferences;
    public static final String SPOTSPACER_PREFERENCES = "spotSpacer_Prefs";
    public static final String language = "language";
    public static final String firstStep = "firstStep";
    public static final String loginStatus = "loginStatus";
    public static final String userName = "userName";
    public static final String userAvatar = "userAvatar";
    public static final String userTags = "userTags";
    public static final String userThanks = "userThanks";
    public static final String userId = "userId";
    private Locale locale;
    @Override
    public void init(Bundle savedInstanceState) {


        addSlide(SampleSlide.newInstance(R.layout.intro_slide0));
        addSlide(SampleSlide.newInstance(R.layout.intro_slide1));
        addSlide(SampleSlide.newInstance(R.layout.intro_slide2));

        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);


        showSkipButton(true);
        setProgressButtonEnabled(true);
//        setDepthAnimation();
    }

    private void loadMainActivity(){
        firstSteps();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNextPressed() {
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
//        Toast.makeText(getApplicationContext(),
//                "Skiped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {
    }

    public void getStarted(View v){
        loadMainActivity();
    }

    public void firstSteps(){
        sharedpreferences = getSharedPreferences(SPOTSPACER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(firstStep,true);
        editor.putBoolean(loginStatus, false);
        editor.putString(userName, getString(R.string.userInfoName));
        editor.putString(userAvatar, "none");
        editor.putInt(userTags, 0);
        editor.putInt(userThanks, 0);
        editor.apply();
    }

    public void alertTranslate(View view) {


        final CharSequence[] items = {"English", "Lietuvių"};
        final Button bt = (Button) view.findViewById(R.id.translateBut);
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstStepsActivity.this);
        builder.setTitle("Choose language");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                sharedpreferences = getSharedPreferences(SPOTSPACER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                switch (item) {
                    case 0: //english
                        editor.putString(language,"en");
                        setLangRecreate("en");
                        break;
                    case 1: //lietuviu
                        editor.putString(language, "lt");
                        setLangRecreate("lt");
                        break;
                    case 2: //

                        break;
                    default:
                        break;
                }
                editor.apply();
                bt.setText(items[item]);
                dialog.dismiss();
            }
        }).show();
    }
    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

}
