package com.jbalboni.vacation;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.jbalboni.vacation.data.LeaveCategoryProvider;
import com.jbalboni.vacation.data.LeaveCategoryTable;
import com.jbalboni.vacation.data.LeaveHistoryProvider;
import com.jbalboni.vacation.data.LeaveHistoryTable;

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
		LeaveFrequency leaveInterval = LeaveFrequency.getLeaveFrequency(fixLeaveInterval(prefs.getString("leaveInterval", "Weekly")));

		String[] projection = { LeaveCategoryTable.ID.toString(), LeaveCategoryTable.ACCRUAL.toString(),
				LeaveCategoryTable.HOURS_PER_YEAR.toString(), LeaveCategoryTable.TITLE.toString(),
				LeaveCategoryTable.CAP_TYPE.toString(), LeaveCategoryTable.CAP_VAL.toString(),
				LeaveCategoryTable.INITIAL_HOURS.toString() };
		Builder itemUri = LeaveCategoryProvider.CONTENT_URI.buildUpon().appendPath(Integer.toString(pos));
		Cursor category = resolver.query(itemUri.build(), projection, null, null, null);
		category.moveToFirst();

		LocalDate startDate = startDateStr == null ? new LocalDate() : fmt.parseLocalDate(startDateStr);
		float hoursPerYear = category.getFloat(category
				.getColumnIndex(LeaveCategoryTable.HOURS_PER_YEAR.toString()));
		float initialHours = category.getFloat(category
				.getColumnIndex(LeaveCategoryTable.INITIAL_HOURS.toString()));
		boolean accrualOn = category.getInt(category.getColumnIndex(LeaveCategoryTable.ACCRUAL.toString())) == 1;
		float leaveCap = category.getFloat(category.getColumnIndex(LeaveCategoryTable.CAP_VAL.toString()));
		LeaveCapType leaveCapType = LeaveCapType.getLeaveCapType(category.getInt(category
				.getColumnIndex(LeaveCategoryTable.CAP_TYPE.toString())));

		category.close();

		String[] historyProjection = { LeaveHistoryTable.DATE.toString(),
				LeaveHistoryTable.NUMBER.toString(), LeaveHistoryTable.ADD_OR_USE.toString() };
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
					.getColumnIndex(LeaveHistoryTable.DATE.toString())))), history.getFloat(history
					.getColumnIndex(LeaveHistoryTable.NUMBER.toString())), LeaveRecType.getLeaveRecType(history
					.getInt(history.getColumnIndex(LeaveHistoryTable.ADD_OR_USE.toString()))));
			list.add(item);
			history.moveToNext();
		}
		return list;
	}

	public static void saveVacationTracker(VacationTracker vacationTracker, SharedPreferences prefs,
			String categoryPrefix) {
		Editor prefsEditor = prefs.edit();

		prefsEditor.putString("leaveInterval", vacationTracker.getLeaveInterval().toString());
		prefsEditor.putString("startDate", String.format("%4d-%02d-%02d", vacationTracker.getStartDate().getYear(),
				vacationTracker.getStartDate().getMonthOfYear(), vacationTracker.getStartDate().getDayOfMonth()));
		prefsEditor.commit();
	}
}
