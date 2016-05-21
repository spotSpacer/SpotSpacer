package com.example.kimas.spotspacer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class Settings0Activity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static final String SPOTSPACER_PREFERENCES = "spotSpacer_Prefs";
    public static final String language = "language";
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_settings0);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        sharedpreferences = getSharedPreferences(SPOTSPACER_PREFERENCES, Context.MODE_PRIVATE);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        sharedpreferences = getSharedPreferences(SPOTSPACER_PREFERENCES, Context.MODE_PRIVATE);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (sharedpreferences.getString(language, "en").equals("lt")) {
            adapter.addFragment(new RadiusFragment(), "Spindulys");
            adapter.addFragment(new OptionsFragment(), "Kita");
        }else{
            adapter.addFragment(new RadiusFragment(), "Radius");
            adapter.addFragment(new OptionsFragment(), "Other");
        }
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
