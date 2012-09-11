package com.jbalboni.vacation;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.jbalboni.vacation.data.LeaveCategoryProvider;
import com.jbalboni.vacation.data.LeaveHistoryProvider;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri.Builder;

/*
 * Converts leave history stored in database into list for VacationTracker
 */
public final class LeaveStateManager {
	private static final DateTimeFormatter fmt = ISODateTimeFormat.localDateParser();

	public static VacationTracker createVacationTracker(SharedPreferences prefs, ContentResolver resolver, int pos) {
		String startDateStr = prefs.getString("startDate", null);
		String leaveInterval = prefs.getString("leaveInterval", "Weekly");

		leaveInterval = fixLeaveInterval(leaveInterval);

		String[] projection = { LeaveTrackerDatabase.LEAVE_CATEGORY.ID, LeaveTrackerDatabase.LEAVE_CATEGORY.ACCRUAL,
				LeaveTrackerDatabase.LEAVE_CATEGORY.HOURS_PER_YEAR, LeaveTrackerDatabase.LEAVE_CATEGORY.TITLE,
				LeaveTrackerDatabase.LEAVE_CATEGORY.CAP_TYPE, LeaveTrackerDatabase.LEAVE_CATEGORY.CAP_VAL,
				LeaveTrackerDatabase.LEAVE_CATEGORY.INITIAL_HOURS };
		Builder itemUri = LeaveCategoryProvider.CONTENT_URI.buildUpon().appendPath(Integer.toString(pos));
		Cursor category = resolver.query(itemUri.build(), projection, null, null, null);
		category.moveToFirst();

		LocalDate startDate = startDateStr == null ? new LocalDate() : fmt.parseLocalDate(startDateStr);
		float hoursPerYear = category.getFloat(category
				.getColumnIndex(LeaveTrackerDatabase.LEAVE_CATEGORY.HOURS_PER_YEAR));
		float initialHours = category.getFloat(category
				.getColumnIndex(LeaveTrackerDatabase.LEAVE_CATEGORY.INITIAL_HOURS));
		boolean accrualOn = category.getInt(category.getColumnIndex(LeaveTrackerDatabase.LEAVE_CATEGORY.ACCRUAL)) == 1;
		float leaveCap = category.getFloat(category.getColumnIndex(LeaveTrackerDatabase.LEAVE_CATEGORY.CAP_VAL));
		LeaveCapType leaveCapType = LeaveCapType.getLeaveCapType(category.getInt(category
				.getColumnIndex(LeaveTrackerDatabase.LEAVE_CATEGORY.CAP_TYPE)));

		category.close();

		String[] historyProjection = { LeaveTrackerDatabase.LEAVE_HISTORY.DATE,
				LeaveTrackerDatabase.LEAVE_HISTORY.NUMBER, LeaveTrackerDatabase.LEAVE_HISTORY.ADD_OR_USE };
		itemUri = LeaveHistoryProvider.LIST_URI.buildUpon().appendPath(Integer.toString(pos));
		Cursor history = resolver.query(itemUri.build(), historyProjection, null, null, null);

		List<LeaveItem> historyList = convertToList(history);

		history.close();

		return new VacationTracker(startDate, historyList, hoursPerYear, initialHours, leaveInterval, accrualOn,
				leaveCapType, leaveCap);
	}

	public static String fixLeaveInterval(String leaveInterval) {
		// Changed labels, so converting to new style
		if (leaveInterval.equals("Day"))
			leaveInterval = "Daily";
		else if (leaveInterval.equals("Week"))
			leaveInterval = "Weekly";
		else if (leaveInterval.equals("Month"))
			leaveInterval = "Monthly";
		return leaveInterval;
	}

	private static List<LeaveItem> convertToList(Cursor history) {
		history.moveToFirst();
		List<LeaveItem> list = new LinkedList<LeaveItem>();
		while (!history.isAfterLast()) {
			LeaveItem item = new LeaveItem(new LocalDate(fmt.parseLocalDate(history.getString(history
					.getColumnIndex(LeaveTrackerDatabase.LEAVE_HISTORY.DATE)))), history.getFloat(history
					.getColumnIndex(LeaveTrackerDatabase.LEAVE_HISTORY.NUMBER)), LeaveRecType.getLeaveRecType(history
					.getInt(history.getColumnIndex(LeaveTrackerDatabase.LEAVE_HISTORY.ADD_OR_USE))));
			list.add(item);
			history.moveToNext();
		}
		return list;
	}

	public static void saveVacationTracker(VacationTracker vacationTracker, SharedPreferences prefs,
			String categoryPrefix) {
		Editor prefsEditor = prefs.edit();

		prefsEditor.putString("leaveInterval", vacationTracker.getLeaveInterval());
		prefsEditor.putString("startDate", String.format("%4d-%02d-%02d", vacationTracker.getStartDate().getYear(),
				vacationTracker.getStartDate().getMonthOfYear(), vacationTracker.getStartDate().getDayOfMonth()));
		prefsEditor.commit();
	}
}
