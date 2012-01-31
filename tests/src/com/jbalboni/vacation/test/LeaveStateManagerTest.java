package com.jbalboni.vacation.test;

import org.joda.time.LocalDate;

import com.jbalboni.vacation.LeaveCapType;
import com.jbalboni.vacation.LeaveCategory;
import com.jbalboni.vacation.LeaveStateManager;
import com.jbalboni.vacation.VacationTracker;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

public class LeaveStateManagerTest extends AndroidTestCase {
	public LeaveStateManagerTest() {
		super();
	}
	
	@Override
	public void setUp() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}

	public void testNoPrefs() {
		float initialHours = 0;
		float hoursUsed = 0;
		float hoursPerYear = 80;
		String leaveInterval = "Weekly";
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		VacationTracker prefTracker = LeaveStateManager.createVacationTracker(prefs, LeaveCategory.LEFT.getPrefix());
		assertEquals(tracker,prefTracker);
	}
	public void testSavePrefs() {
		float initialHours = 2;
		float hoursUsed = 5;
		float hoursPerYear = 70;
		String leaveInterval = "Monthly";
		boolean accrualOn = true;
		LocalDate startDate = (new LocalDate()).minusYears(1);
		float leaveCap = 6;
		LeaveCapType leaveCapType = LeaveCapType.MAX;

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		LeaveStateManager.saveVacationTracker(tracker,prefs,LeaveCategory.CENTER.getPrefix());
		
		VacationTracker prefTracker = LeaveStateManager.createVacationTracker(prefs, LeaveCategory.CENTER.getPrefix());
		assertEquals(tracker,prefTracker);
	}
	public void testOldPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		Editor prefsEditor = prefs.edit();
		
		prefsEditor.putString("leaveInterval","Month");
		prefsEditor.commit();
		VacationTracker prefTracker = LeaveStateManager.createVacationTracker(prefs, LeaveCategory.CENTER.getPrefix());
		assertEquals(prefTracker.getLeaveInterval(),"Monthly");
		
		prefsEditor.putString("leaveInterval","Day");
		prefsEditor.commit();
		prefTracker = LeaveStateManager.createVacationTracker(prefs, LeaveCategory.CENTER.getPrefix());
		assertEquals(prefTracker.getLeaveInterval(),"Daily");
		
		prefsEditor.putString("leaveInterval","Week");
		prefsEditor.commit();
		prefTracker = LeaveStateManager.createVacationTracker(prefs, LeaveCategory.CENTER.getPrefix());
		assertEquals(prefTracker.getLeaveInterval(),"Weekly");

		
	}
}
