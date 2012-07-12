package com.jbalboni.vacation.ui;

import java.util.List;

import com.viewpagerindicator.TitlePageIndicator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jbalboni.vacation.LeaveStateManager;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.R.drawable;
import com.jbalboni.vacation.R.id;
import com.jbalboni.vacation.R.layout;
import com.jbalboni.vacation.R.string;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class VacationTrackerActivity extends SherlockFragmentActivity {

	LeaveAdapter mAdapter;
	ViewPager mPager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mAdapter = new LeaveAdapter(getSupportFragmentManager(), LeaveStateManager.getTitles(
				PreferenceManager.getDefaultSharedPreferences(this), getApplicationContext()));

		mPager = (ViewPager) findViewById(R.id.leavePager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(1, false);

		// Bind the title indicator to the adapter
		TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
		titleIndicator.setViewPager(mPager);
		titleIndicator.setCurrentItem(1);

	}

	public static class LeaveAdapter extends FragmentPagerAdapter {
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
		public SherlockFragment getItem(int position) {
			return LeaveFragment.newInstance(position);
		}

		@Override
		public String getPageTitle(int position) {
			return titles.get(position);
		}

		public void updateTitles(List<String> newTitles) {
			titles = newTitles;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.updateTitles(LeaveStateManager.getTitles(PreferenceManager.getDefaultSharedPreferences(this),
				getApplicationContext()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(getString(R.string.menu_settings)).setIcon(R.drawable.ic_menu_settings)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(getString(R.string.menu_history)).setIcon(R.drawable.ic_menu_recent_history)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		String title = item.getTitle().toString();
		if (title.equals(getString(R.string.menu_settings))) {
			Intent test = new Intent(this, SettingsActivity.class);
			startActivity(test);
		} else if (title.equals(getString(R.string.menu_history))) {
			Intent leaveCategory = new Intent(this, LeaveCategoryActivity.class);
			startActivity(leaveCategory);
		}
		return true;
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, VacationTrackerActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

}