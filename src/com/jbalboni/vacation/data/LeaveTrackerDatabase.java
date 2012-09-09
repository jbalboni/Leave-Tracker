package com.jbalboni.vacation.data;

import org.joda.time.LocalDate;

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
	private static final String LEAVE_HISTORY_CREATE = "CREATE TABLE leave_history ( _id INTEGER PRIMARY KEY AUTOINCREMENT, notes TEXT, number REAL, date DATE, leave_category_id INTEGER NOT NULL, FOREIGN KEY(leave_category_id) REFERENCES leave_categories(_id), add_or_use INTEGER)";
	private static final String LEAVE_CATEGORY_CREATE = "CREATE TABLE leave_categories ( _id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, display_pos INTEGER, hours_per_year REAL NOT NULL, initial_hours REAL NOT NULL, accrual INTEGER NOT NULL, cap_type INTEGER NOT NULL, cap_val REAL)";
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

		db.execSQL(LEAVE_CATEGORY_CREATE);

		// sick leave
		ContentValues categories = new ContentValues();
		categories.put(LEAVE_CATEGORY.ID, 1);
		categories.put(LEAVE_CATEGORY.TITLE, LeaveCategory.LEFT.getTitle());
		categories.put(LEAVE_CATEGORY.DISPLAY, 0);
		categories
				.put(LEAVE_CATEGORY.HOURS_PER_YEAR, getFloatPref(LeaveCategory.LEFT.getPrefix() + "hoursPerYear", 80));
		categories.put(LEAVE_CATEGORY.INITIAL_HOURS, getFloatPref(LeaveCategory.LEFT.getPrefix() + "initialHours", 0));
		categories.put(LEAVE_CATEGORY.ACCRUAL, prefs.getBoolean(LeaveCategory.LEFT.getPrefix() + "accrualOn", true));
		categories.put(LEAVE_CATEGORY.CAP_TYPE, LEAVE_CAP_TYPE.NONE);
		categories.put(LEAVE_CATEGORY.CAP_VAL, 0);
		db.insert(LEAVE_CATEGORY_TABLE, null, categories);

		// vacation
		categories = new ContentValues();
		categories.put(LEAVE_CATEGORY.ID, 2);
		categories.put(LEAVE_CATEGORY.TITLE, LeaveCategory.CENTER.getTitle());
		categories.put(LEAVE_CATEGORY.DISPLAY, 1);
		categories.put(LEAVE_CATEGORY.HOURS_PER_YEAR,
				getFloatPref(LeaveCategory.CENTER.getPrefix() + "hoursPerYear", 80));
		categories
				.put(LEAVE_CATEGORY.INITIAL_HOURS, getFloatPref(LeaveCategory.CENTER.getPrefix() + "initialHours", 0));
		categories.put(LEAVE_CATEGORY.ACCRUAL, prefs.getBoolean(LeaveCategory.CENTER.getPrefix() + "accrualOn", true));
		categories.put(LEAVE_CATEGORY.CAP_TYPE, LEAVE_CAP_TYPE.NONE);
		categories.put(LEAVE_CATEGORY.CAP_VAL, 0);
		db.insert(LEAVE_CATEGORY_TABLE, null, categories);

		// comp time
		categories = new ContentValues();
		categories.put(LEAVE_CATEGORY.ID, 3);
		categories.put(LEAVE_CATEGORY.TITLE, LeaveCategory.RIGHT.getTitle());
		categories.put(LEAVE_CATEGORY.DISPLAY, 2);
		categories
				.put(LEAVE_CATEGORY.HOURS_PER_YEAR, getFloatPref(LeaveCategory.RIGHT.getPrefix() + "hoursPerYear", 0));
		categories.put(LEAVE_CATEGORY.INITIAL_HOURS, getFloatPref(LeaveCategory.RIGHT.getPrefix() + "initialHours", 0));
		categories.put(LEAVE_CATEGORY.ACCRUAL, prefs.getBoolean(LeaveCategory.RIGHT.getPrefix() + "accrualOn", false));
		categories.put(LEAVE_CATEGORY.CAP_TYPE, LEAVE_CAP_TYPE.NONE);
		categories.put(LEAVE_CATEGORY.CAP_VAL, 0);
		db.insert(LEAVE_CATEGORY_TABLE, null, categories);

		db.execSQL(LEAVE_HISTORY_CREATE);

		if (getFloatPref(LeaveCategory.LEFT.getPrefix() + "hoursUsed", 0) > 0) {
			ContentValues leaveValues = new ContentValues();
			leaveValues.put(LEAVE_HISTORY.CATEGORY, 1);
			leaveValues.put(LEAVE_HISTORY.NOTES, "Leave from Hours Used preference");
			leaveValues.put(LEAVE_HISTORY.NUMBER, getFloatPref(LeaveCategory.LEFT.getPrefix() + "hoursUsed", 0));
			leaveValues.put(LEAVE_HISTORY.DATE, (new LocalDate()).toString());
			db.insert(LEAVE_HISTORY_TABLE, null, leaveValues);
		}
		if (getFloatPref(LeaveCategory.CENTER.getPrefix() + "hoursUsed", 0) > 0) {
			ContentValues leaveValues = new ContentValues();
			leaveValues.put(LEAVE_HISTORY.CATEGORY, 2);
			leaveValues.put(LEAVE_HISTORY.NOTES, "Leave from Hours Used preference");
			leaveValues.put(LEAVE_HISTORY.NUMBER, getFloatPref(LeaveCategory.CENTER.getPrefix() + "hoursUsed", 0));
			leaveValues.put(LEAVE_HISTORY.DATE, (new LocalDate()).toString());
			db.insert(LEAVE_HISTORY_TABLE, null, leaveValues);
		}
		if (getFloatPref(LeaveCategory.RIGHT.getPrefix() + "hoursUsed", 0) > 0) {
			ContentValues leaveValues = new ContentValues();
			leaveValues.put(LEAVE_HISTORY.CATEGORY, 3);
			leaveValues.put(LEAVE_HISTORY.NOTES, "Leave from Hours Used preference");
			leaveValues.put(LEAVE_HISTORY.NUMBER, getFloatPref(LeaveCategory.RIGHT.getPrefix() + "hoursUsed", 0));
			leaveValues.put(LEAVE_HISTORY.DATE, (new LocalDate()).toString());
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

	// I'm not happy with this method, but at least my columns are defined
	// somewhere consistently
	public static class LEAVE_HISTORY {
		public static final String ID = "_id";
		public static final String TITLE = "title";
		public static final String NUMBER = "number";
		public static final String DATE = "date";
		public static final String NOTES = "notes";
		public static final String CATEGORY = "leave_category_id";
		public static final String ADD_OR_USE = "add_or_use";
	};

	public static class LEAVE_CATEGORY {
		public static final String ID = "_id";
		public static final String TITLE = "title";
		public static final String ACCRUAL = "accrual";
		public static final String HOURS_PER_YEAR = "hours_per_year";
		public static final String CAP_TYPE = "cap_type";
		public static final String CAP_VAL = "cap_val";
		public static final String INITIAL_HOURS = "initial_hours";
		public static final String DISPLAY = "display_pos";
	};

	public static class LEAVE_CAP_TYPE {
		public static final int NONE = 0;
		public static final int MAX = 1;
		public static final int CARRY_OVER = 2;
	};
}
