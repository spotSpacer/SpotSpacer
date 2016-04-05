package com.example.kimas.spotspacer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    // Required empty public constructor


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ff = (FrameLayout) inflater.inflate(R.layout.fragment_radius, container, false);
        metters = (TextView) ff.findViewById(R.id.meterText);

        mSeekBar = (SeekBar) ff.findViewById(R.id.seekBar);
        metters.setTranslationY(-250);
        metters.setTranslationX(-(ff.getWidth() / 2));
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
        int rH = (int)((radius*(mSeekBar.getWidth()))/2500)+250;
        metters.setTranslationY(-rH);
    }

}
