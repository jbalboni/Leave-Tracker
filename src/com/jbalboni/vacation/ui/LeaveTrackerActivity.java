package com.jbalboni.vacation.ui;

import com.viewpagerindicator.TitlePageIndicator;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/*
 * Main activity. Manages a fragment for each category in a view pager
 */
public class LeaveTrackerActivity extends SherlockFragmentActivity {

	LeaveAdapter mAdapter;
	ViewPager mPager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mAdapter = new LeaveAdapter(getSupportFragmentManager(), getApplicationContext());

		mPager = (ViewPager) findViewById(R.id.leavePager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(1, false);

		TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
		titleIndicator.setViewPager(mPager);
		titleIndicator.setCurrentItem(1);

	}

	public static class LeaveAdapter extends FragmentPagerAdapter {
		private SQLiteQueryBuilder titleQuery;
		private LeaveTrackerDatabase leaveDB;
		private String selection = LeaveTrackerDatabase.LEAVE_CATEGORY.ID + " in (1,2,3)";
		private String selTitle = LeaveTrackerDatabase.LEAVE_CATEGORY.ID + "=?";
		private String[] projection = { LeaveTrackerDatabase.LEAVE_CATEGORY.TITLE };
		private String sortOrder = LeaveTrackerDatabase.LEAVE_CATEGORY.ID;

		public LeaveAdapter(FragmentManager fm, Context context) {
			super(fm);
			leaveDB = new LeaveTrackerDatabase(context);
			titleQuery = new SQLiteQueryBuilder();
			titleQuery.setTables(LeaveTrackerDatabase.LEAVE_CATEGORY_TABLE);
		}

		@Override
		public int getCount() {
			return titleQuery.query(leaveDB.getReadableDatabase(), projection, selection, null, null, null, sortOrder)
					.getCount();
		}

		@Override
		public SherlockFragment getItem(int position) {
			return LeaveFragment.newInstance(position + 1);
		}

		@Override
		public String getPageTitle(int position) {
			Cursor cursor = titleQuery.query(leaveDB.getReadableDatabase(), projection, selTitle,
					new String[] { Integer.toString(position + 1) }, null, null, sortOrder);
			cursor.moveToFirst();
			return cursor.getString(0);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(getString(R.string.menu_settings)).setIcon(R.drawable.ic_menu_settings)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(getString(R.string.menu_history)).setIcon(R.drawable.ic_menu_list)
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
		Intent i = new Intent(context, LeaveTrackerActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	/*
	 * Clicking on hours available calls this
	 */
	public void onClickAvailable(View v) {
		Intent intent = new Intent();
		intent.setClass(this, LeaveHistoryActivity.class);
		int catID = mPager.getCurrentItem() + 1;
		intent.putExtra(getString(R.string.intent_catid), catID);
		//I should figure out a way to remove this title from the intent
		intent.putExtra(getString(R.string.intent_catname), mAdapter.getPageTitle(catID - 1));
		startActivity(intent);
	}

}