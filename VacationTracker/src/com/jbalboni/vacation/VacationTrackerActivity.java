package com.jbalboni.vacation;

import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class VacationTrackerActivity extends FragmentActivity {
    
    LeaveAdapter mAdapter;
    ViewPager mPager;
    
    static final int DATE_DIALOG_ID = 0;
    static final int HOURS_DIALOG_ID = 1;
    static final String HOURS_IN_DAY = "8";
    DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        mAdapter = new LeaveAdapter(getSupportFragmentManager(),LeaveStateManager.getTitles(PreferenceManager.getDefaultSharedPreferences(this),getApplicationContext()));

        mPager = (ViewPager)findViewById(R.id.leavePager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(1,false);
        

        //Bind the title indicator to the adapter
        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);
        titleIndicator.setCurrentItem(1);
        
    }
    
    public static class LeaveAdapter extends FragmentPagerAdapter implements TitleProvider {
        List<String> titles;
        public LeaveAdapter(FragmentManager fm, List<String> titles) {
            super(fm);
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public Fragment getItem(int position) {
            return LeaveFragment.newInstance(position);
        }

        @Override
        public String getTitle(int position)
        {
            return titles.get(position);
        }
        
        public void updateTitles(List<String> newTitles) {
            titles = newTitles;
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mAdapter.updateTitles(LeaveStateManager.getTitles(PreferenceManager.getDefaultSharedPreferences(this),getApplicationContext()));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.leave_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.settings:
            Intent test = new Intent(this,SettingsActivity.class);
            startActivity(test);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, VacationTrackerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }

}