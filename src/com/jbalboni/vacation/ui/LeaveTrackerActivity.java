package com.jbalboni.vacation.ui;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.viewpagerindicator.TitlePageIndicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jbalboni.vacation.LeaveStateManager;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

/*
 * Main activity. Manages a fragment for each category in a view pager
 */
public class LeaveTrackerActivity extends SherlockFragmentActivity {

	LeaveAdapter mAdapter;
	ViewPager mPager;

	private static final DateTimeFormatter fmt = ISODateTimeFormat.localDateParser();

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

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean newUser = prefs.getBoolean(getString(R.string.new_user_pref), true);
		if (newUser) {
			NewUserFragment userFrag = new NewUserFragment();
			userFrag.show(getSupportFragmentManager(), "newUser");
		}
	}

	public static class NewUserFragment extends SherlockDialogFragment implements OnClickListener {

		private SharedPreferences prefs;
		private Resources res;
		private TypedArray intervals;

		@SuppressLint("NewApi")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String startDateStr = prefs.getString("startDate", null);
			LocalDate startDate = startDateStr == null ? new LocalDate() : fmt.parseLocalDate(startDateStr);
			String leaveInterval = prefs.getString("leaveInterval", "Weekly");
			leaveInterval = LeaveStateManager.fixLeaveInterval(leaveInterval);

			getDialog().setTitle(getString(R.string.new_user_title));

			View view = inflater.inflate(R.layout.new_user_fragment, container);
			DatePicker dateView = (DatePicker) view.findViewById(R.id.datePicker);
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				dateView.setCalendarViewShown(false);
			}
			dateView.init(startDate.getYear(), startDate.getMonthOfYear() - 1, startDate.getDayOfMonth(), null);

			Spinner intervalView = (Spinner) view.findViewById(R.id.leaveInterval);
			res = getResources();
			intervals = res.obtainTypedArray(R.array.leave_intervals);
			for (int i = 0; i < intervals.length(); i++) {
				if (intervals.getString(i).equals(leaveInterval)) {
					intervalView.setSelection(i);
					break;
				}
			}

			Button button = (Button) view.findViewById(R.id.save);
			button.setOnClickListener(this);

			return view;
		}

		@Override
		public void onClick(View v) {
			Editor prefsEditor = prefs.edit();

			prefsEditor.putString("leaveInterval", intervals.getString(((Spinner) getView().findViewById(
					R.id.leaveInterval)).getSelectedItemPosition()));
			DatePicker dateView = (DatePicker) getView().findViewById(R.id.datePicker);
			prefsEditor.putString(
					"startDate",
					String.format("%4d-%02d-%02d", dateView.getYear(), dateView.getMonth() + 1,
							dateView.getDayOfMonth()));
			prefsEditor.putBoolean(getString(R.string.new_user_pref), false);
			prefsEditor.commit();
			Toast.makeText(getActivity(), R.string.saved_msg, Toast.LENGTH_LONG).show();
			((LeaveTrackerActivity) getSherlockActivity()).mAdapter.notifyDataSetChanged();
			getDialog().dismiss();
		}
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

		// This gets called when notify changed is called from the new user
		// dialog
		@Override
		public int getItemPosition(Object item) {
			LeaveFragment leaveFrag = (LeaveFragment) item;
			leaveFrag.updateFragment();
			return POSITION_UNCHANGED;
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
		menu.add(getString(R.string.menu_history)).setIcon(R.drawable.ic_menu_list)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(getString(R.string.menu_settings)).setIcon(R.drawable.ic_menu_settings)
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
		// I should figure out a way to remove this title from the intent
		intent.putExtra(getString(R.string.intent_catname), mAdapter.getPageTitle(catID - 1));
		startActivity(intent);
	}

}