package com.jbalboni.vacation.data;

import org.joda.time.LocalDate;

import com.jbalboni.vacation.LeaveCapType;
import com.jbalboni.vacation.LeaveCategory;
import com.jbalboni.vacation.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

public class LeaveTrackerDatabase extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	private SharedPreferences prefs;
	private Context context;
	public static String LEAVE_HISTORY_TABLE = "leave_history";
	public static String LEAVE_CATEGORY_TABLE = "leave_categories";

	// public static String ID = "_id";
	// public static String TITLE = "title";
	// public static String CATEGORY_ID = "leave_category_id";

	public LeaveTrackerDatabase(Context context) {
		super(context, context.getString(R.string.database_name), null, DATABASE_VERSION);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LeaveCategory.LEFT.setTitle(prefs.getString(LeaveCategory.LEFT.getPrefix() + "title",
				context.getString(R.string.default_left_name)));
		LeaveCategory.CENTER.setTitle(prefs.getString(LeaveCategory.CENTER.getPrefix() + "title",
				context.getString(R.string.default_center_name)));
		LeaveCategory.RIGHT.setTitle(prefs.getString(LeaveCategory.RIGHT.getPrefix() + "title",
				context.getString(R.string.default_right_name)));

		db.execSQL(LeaveCategoryTable.getTableCreate());

		// sick leave
		ContentValues categories = new ContentValues();
		categories.put(LeaveCategoryTable.ID.toString(), 1);
		categories.put(LeaveCategoryTable.TITLE.toString(), LeaveCategory.LEFT.getTitle());
		categories.put(LeaveCategoryTable.DISPLAY.toString(), 0);
		categories
				.put(LeaveCategoryTable.HOURS_PER_YEAR.toString(), getFloatPref(LeaveCategory.LEFT.getPrefix() + "hoursPerYear", 80));
		categories.put(LeaveCategoryTable.INITIAL_HOURS.toString(), getFloatPref(LeaveCategory.LEFT.getPrefix() + "initialHours", 0));
		categories.put(LeaveCategoryTable.ACCRUAL.toString(), prefs.getBoolean(LeaveCategory.LEFT.getPrefix() + "accrualOn", true));
		categories.put(LeaveCategoryTable.CAP_TYPE.toString(), LeaveCapType.NONE.getVal());
		categories.put(LeaveCategoryTable.CAP_VAL.toString(), 0);
		db.insert(LEAVE_CATEGORY_TABLE, null, categories);

		// vacation
		categories = new ContentValues();
		categories.put(LeaveCategoryTable.ID.toString(), 2);
		categories.put(LeaveCategoryTable.TITLE.toString(), LeaveCategory.CENTER.getTitle());
		categories.put(LeaveCategoryTable.DISPLAY.toString(), 1);
		categories.put(LeaveCategoryTable.HOURS_PER_YEAR.toString(),
				getFloatPref(LeaveCategory.CENTER.getPrefix() + "hoursPerYear", 80));
		categories
				.put(LeaveCategoryTable.INITIAL_HOURS.toString(), getFloatPref(LeaveCategory.CENTER.getPrefix() + "initialHours", 0));
		categories.put(LeaveCategoryTable.ACCRUAL.toString(), prefs.getBoolean(LeaveCategory.CENTER.getPrefix() + "accrualOn", true));
		categories.put(LeaveCategoryTable.CAP_TYPE.toString(), LeaveCapType.NONE.getVal());
		categories.put(LeaveCategoryTable.CAP_VAL.toString(), 0);
		db.insert(LEAVE_CATEGORY_TABLE, null, categories);

		// comp time
		categories = new ContentValues();
		categories.put(LeaveCategoryTable.ID.toString(), 3);
		categories.put(LeaveCategoryTable.TITLE.toString(), LeaveCategory.RIGHT.getTitle());
		categories.put(LeaveCategoryTable.DISPLAY.toString(), 2);
		categories
				.put(LeaveCategoryTable.HOURS_PER_YEAR.toString(), getFloatPref(LeaveCategory.RIGHT.getPrefix() + "hoursPerYear", 0));
		categories.put(LeaveCategoryTable.INITIAL_HOURS.toString(), getFloatPref(LeaveCategory.RIGHT.getPrefix() + "initialHours", 0));
		categories.put(LeaveCategoryTable.ACCRUAL.toString(), prefs.getBoolean(LeaveCategory.RIGHT.getPrefix() + "accrualOn", false));
		categories.put(LeaveCategoryTable.CAP_TYPE.toString(), LeaveCapType.NONE.getVal());
		categories.put(LeaveCategoryTable.CAP_VAL.toString(), 0);
		db.insert(LeaveCategoryTable.getName(), null, categories);

		db.execSQL(LeaveHistoryTable.getTableCreate());

		if (getFloatPref(LeaveCategory.LEFT.getPrefix() + "hoursUsed", 0) > 0) {
			ContentValues leaveValues = new ContentValues();
			leaveValues.put(LeaveHistoryTable.CATEGORY.toString(), 1);
			leaveValues.put(LeaveHistoryTable.NOTES.toString(), "Leave from Hours Used preference");
			leaveValues.put(LeaveHistoryTable.NUMBER.toString(), getFloatPref(LeaveCategory.LEFT.getPrefix() + "hoursUsed", 0));
			leaveValues.put(LeaveHistoryTable.DATE.toString(), (new LocalDate()).toString());
			db.insert(LEAVE_HISTORY_TABLE, null, leaveValues);
		}
		if (getFloatPref(LeaveCategory.CENTER.getPrefix() + "hoursUsed", 0) > 0) {
			ContentValues leaveValues = new ContentValues();
			leaveValues.put(LeaveHistoryTable.CATEGORY.toString(), 2);
			leaveValues.put(LeaveHistoryTable.NOTES.toString(), "Leave from Hours Used preference");
			leaveValues.put(LeaveHistoryTable.NUMBER.toString(), getFloatPref(LeaveCategory.CENTER.getPrefix() + "hoursUsed", 0));
			leaveValues.put(LeaveHistoryTable.DATE.toString(), (new LocalDate()).toString());
			db.insert(LEAVE_HISTORY_TABLE, null, leaveValues);
		}
		if (getFloatPref(LeaveCategory.RIGHT.getPrefix() + "hoursUsed", 0) > 0) {
			ContentValues leaveValues = new ContentValues();
			leaveValues.put(LeaveHistoryTable.CATEGORY.toString(), 3);
			leaveValues.put(LeaveHistoryTable.NOTES.toString(), "Leave from Hours Used preference");
			leaveValues.put(LeaveHistoryTable.NUMBER.toString(), getFloatPref(LeaveCategory.RIGHT.getPrefix() + "hoursUsed", 0));
			leaveValues.put(LeaveHistoryTable.DATE.toString(), (new LocalDate()).toString());
			db.insert(LEAVE_HISTORY_TABLE, null, leaveValues);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1) {
			db.execSQL("alter table leave_history add column add_or_use INTEGER");
			db.execSQL("update leave_history set add_or_use = 0");
		}
	}

	private float getFloatPref(String pref, float defValue) {
		String val = prefs.getString(pref, "");
		if (val == null || val.equals("")) {
			return defValue;
		} else {
			return Float.parseFloat(val);
		}
	}

	public static float getFloat(String val) {
		if (val == null || val.equals("")) {
			return 0;
		} else {
			return Float.parseFloat(val);
		}
	}

	public static float getFloat(String val, float defValue) {
		if (val == null || val.equals("")) {
			return defValue;
		} else {
			return Float.parseFloat(val);
		}
	}

}
