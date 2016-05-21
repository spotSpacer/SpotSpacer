package com.example.kimas.spotspacer;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.Locale;


public class OptionsFragment extends Fragment {
    SharedPreferences sharedpreferences;
    public static final String SPOTSPACER_PREFERENCES = "spotSpacer_Prefs";
    public static final String language = "language";
    private Locale locale;
    private FrameLayout ff;
    public OptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ff = (FrameLayout) inflater.inflate(R.layout.fragment_options, container, false);
        sharedpreferences = this.getActivity().getSharedPreferences(SPOTSPACER_PREFERENCES, Context.MODE_PRIVATE);

        RelativeLayout win = (RelativeLayout) ff.findViewById(R.id.langSet);
        win.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"English", "Lietuvių"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (sharedpreferences.getString(language,"lt").equals("lt")){
                    builder.setTitle("Pasirinkti kalbą");
                }else{
                    builder.setTitle("Choose language");
                }

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        switch (item) {
                            case 0: //english
                                editor.putString(language, "en");
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
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        return ff;
    }

    public void setLangRecreate(String langval) {
        Configuration config = this.getActivity().getResources().getConfiguration();
        locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());
        getActivity().recreate();


//        Fragment newFragment = new OptionsFragment();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//        transaction.replace(R.id.optFrag, newFragment);
//        transaction.addToBackStack(null);
//
//        transaction.commit();

    }

}
