package com.example.kimas.spotspacer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class RadiusFragment extends Fragment {
    public TextView metters;
    public SeekBar mSeekBar;
    private FrameLayout ff;
    public static final String radiusSt = "radius";
    SharedPreferences sharedpreferences;
    public static final String SPOTSPACER_PREFERENCES = "spotSpacer_Prefs";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ff = (FrameLayout) inflater.inflate(R.layout.fragment_radius, container, false);
        metters = (TextView) ff.findViewById(R.id.meterText);

        mSeekBar = (SeekBar) ff.findViewById(R.id.seekBar);
//        metters.setTranslationY(-250);
//        metters.setTranslationX(-(ff.getWidth() / 2));
        sharedpreferences = this.getActivity().getSharedPreferences(SPOTSPACER_PREFERENCES, Context.MODE_PRIVATE);
//        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int m = (int) getDouble(sharedpreferences, radiusSt, 500.0);
        mSeekBar.setProgress(m-500);
        metters.setText(m+"M");
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                setRadius(progressChanged);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        return ff;
    }



    public void setRadius(int radius){
        metters.setText(radius + 500 + "M");

        double t = radius + 500.0;
        Log.e("RADIUS", t + "");
        SharedPreferences.Editor editer = sharedpreferences.edit();
        putDouble(editer, radiusSt, t);
        editer.apply();
//        int rH = (int)((radius*(mSeekBar.getWidth()))/2500)+250;
//        metters.setTranslationY(-rH);
    }
    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }
    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
