package com.jbalboni.vacation.test;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.TextView;
import android.support.v4.view.ViewPager;

import com.jbalboni.vacation.LeaveCategory;
import com.jbalboni.vacation.ui.LeaveFragment;
import com.jbalboni.vacation.LeaveCapType;
import com.jbalboni.vacation.LeaveItem;
import com.jbalboni.vacation.LeaveStateManager;
import com.jbalboni.vacation.VacationTracker;
import com.jbalboni.vacation.ui.LeaveTrackerActivity.LeaveAdapter;

import com.jbalboni.vacation.ui.LeaveTrackerActivity;

public class LeaveTrackerActivityTest extends ActivityInstrumentationTestCase2<LeaveTrackerActivity> {

	LeaveTrackerActivity mActivity;
	
	public LeaveTrackerActivityTest() {
		super("com.jbalboni.vacation.ui", LeaveTrackerActivity.class);
	} // end of SpinnerActivityTest constructor definition

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mActivity = getActivity();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
		
		setActivityInitialTouchMode(false);
	}
	
//	@UiThreadTest
//	public void testHoursDisplay() {
//		float initialHours = 2;
//		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
//		hoursUsed.add(new LeaveItem(new LocalDate(),5));
//		float hoursPerYear = 70;
//		String leaveInterval = "Monthly";
//		boolean accrualOn = true;
//		LocalDate startDate = (new LocalDate()).minusYears(1);
//		float leaveCap = 0;
//		LeaveCapType leaveCapType = LeaveCapType.NONE;
//
//		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
//				accrualOn, leaveCapType, leaveCap);
//		
//		String hoursAvailable = String.format("%.2f",tracker.calculateHours(new LocalDate()));
//		
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());
//		LeaveStateManager.saveVacationTracker(tracker,prefs,LeaveCategory.CENTER.getPrefix());
//		
//		Instrumentation mInstr = this.getInstrumentation();
//		mInstr.callActivityOnResume(mActivity);
//		ViewPager pager = (ViewPager)mActivity.findViewById(com.jbalboni.vacation.R.id.leavePager);
//		LeaveAdapter adapter = (LeaveAdapter)pager.getAdapter();
//		LeaveFragment currentFrag = (LeaveFragment)adapter.getItem(pager.getCurrentItem());
//		TextView hoursView = (TextView) currentFrag.getView().findViewById(com.jbalboni.vacation.R.id.hoursAvailable);
//		assertEquals(hoursAvailable,hoursView.getText().toString());
//	}
}
