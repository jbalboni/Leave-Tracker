package com.jbalboni.vacation;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class LeaveStateManager {
	private static final DateTimeFormatter fmt = ISODateTimeFormat.localDateParser();

	public static VacationTracker createVacationTracker(SharedPreferences prefs, String categoryPrefix) {
		String startDateStr = prefs.getString("startDate", null);
		String leaveInterval = prefs.getString("leaveInterval", "Weekly");

		if (leaveInterval.equals("Day"))
			leaveInterval = "Daily";
		else if (leaveInterval.equals("Week"))
			leaveInterval = "Weekly";
		else if (leaveInterval.equals("Month"))
			leaveInterval = "Monthly";

		LocalDate startDate = startDateStr == null ? new LocalDate() : fmt.parseLocalDate(startDateStr);
		float hoursUsed = Float.parseFloat(prefs.getString(categoryPrefix + "hoursUsed", "0"));
		float hoursPerYear = Float.parseFloat(prefs.getString(categoryPrefix + "hoursPerYear", "80"));
		float initialHours = Float.parseFloat(prefs.getString(categoryPrefix + "initialHours", "0"));
		boolean accrualOn = prefs.getBoolean(categoryPrefix + "accrualOn", true);
		float leaveCap = prefs.getFloat(categoryPrefix+"leaveCap", 0);
		LeaveCapType leaveCapType = LeaveCapType.getLeaveCapType(prefs.getString(categoryPrefix+"leaveCapType", "None"));

		return new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval, accrualOn, leaveCapType, leaveCap);
	}

	public static void saveVacationTracker(VacationTracker vacationTracker, SharedPreferences prefs,
			String categoryPrefix) {
		Editor prefsEditor = prefs.edit();
		prefsEditor.putString(categoryPrefix + "hoursUsed", Float.toString(vacationTracker.getHoursUsed()));
		prefsEditor.putString(categoryPrefix + "hoursPerYear", Float.toString(vacationTracker.getHoursPerYear()));
		prefsEditor.putString(categoryPrefix + "initialHours", Float.toString(vacationTracker.getInitialHours()));
		prefsEditor.putBoolean(categoryPrefix + "accrualOn", vacationTracker.isAccrualOn());
		prefsEditor.putFloat(categoryPrefix + "leaveCap", vacationTracker.getLeaveCap());
		prefsEditor.putString(categoryPrefix + "leaveCapType", vacationTracker.getLeaveCapType().toString());
		
		prefsEditor.putString("leaveInterval", vacationTracker.getLeaveInterval());
		prefsEditor.putString("startDate", String.format("%4d-%02d-%02d", vacationTracker.getStartDate().getYear(),
				vacationTracker.getStartDate().getMonthOfYear(), vacationTracker.getStartDate().getDayOfMonth()));
		prefsEditor.commit();
	}

	public static List<String> getTitles(SharedPreferences prefs, Context ctxt) {
		List<String> titles = new ArrayList<String>();
		titles.add(prefs.getString(LeaveCategory.LEFT.getPrefix() + "title", ctxt.getString(R.string.default_left_name)));
		titles.add(prefs.getString(LeaveCategory.CENTER.getPrefix() + "title",
				ctxt.getString(R.string.default_center_name)));
		titles.add(prefs.getString(LeaveCategory.RIGHT.getPrefix() + "title",
				ctxt.getString(R.string.default_right_name)));
		return titles;
	}
}
