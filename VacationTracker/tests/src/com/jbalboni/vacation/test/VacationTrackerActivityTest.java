package com.jbalboni.vacation.test;

import org.joda.time.LocalDate;

import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.TextView;
import android.support.v4.view.ViewPager;

import com.jbalboni.vacation.LeaveCategory;
import com.jbalboni.vacation.LeaveFragment;
import com.jbalboni.vacation.LeaveStateManager;
import com.jbalboni.vacation.VacationTracker;
import com.jbalboni.vacation.VacationTrackerActivity.LeaveAdapter;

import com.jbalboni.vacation.VacationTrackerActivity;

public class VacationTrackerActivityTest extends ActivityInstrumentationTestCase2<VacationTrackerActivity> {

	VacationTrackerActivity mActivity;
	
	public VacationTrackerActivityTest() {
		super("com.jbalboni.vacation", VacationTrackerActivity.class);
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
//		float hoursUsed = 5;
//		float hoursPerYear = 70;
//		String leaveInterval = "Monthly";
//		boolean accrualOn = true;
//		LocalDate startDate = (new LocalDate()).minusYears(1);
//
//		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
//				accrualOn);
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
//		assertEquals(hoursView.getText().toString(),hoursAvailable);
//	}
}
