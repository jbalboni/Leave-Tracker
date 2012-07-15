package com.jbalboni.vacation.data;

import com.jbalboni.vacation.LeaveCategory;
import com.jbalboni.vacation.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

public class LeaveTrackerDatabase extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String LEAVE_HISTORY_CREATE = "CREATE TABLE leave_history ( _id INTEGER PRIMARY KEY AUTOINCREMENT, notes TEXT, number REAL, date DATE, leave_category_id INTEGER NOT NULL, FOREIGN KEY(leave_category_id) REFERENCES leave_categories(_id))";
	private static final String LEAVE_CATEGORY_CREATE = "CREATE TABLE leave_categories ( _id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, display_pos INTEGER, initial_hours REAL NOT NULL, accrual INTEGER NOT NULL, cap_type INTEGER NOT NULL, cap_val REAL)";
	private SharedPreferences prefs;
	private Context context;
	public static String LEAVE_HISTORY_TABLE = "leave_history";
	public static String LEAVE_CATEGORY_TABLE = "leave_categories";
	public static String ID = "_id";
	public static String TITLE = "title";
	public static String CATEGORY_ID = "leave_category_id";

	public LeaveTrackerDatabase(Context context) {
		super(context, context.getString(R.string.database_name), null, DATABASE_VERSION);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.context = context;
	}

	@Override
	//TODO fix gross string sql statements
	public void onCreate(SQLiteDatabase db) {
		LeaveCategory.LEFT.setTitle(context.getString(R.string.default_left_name));
		LeaveCategory.CENTER.setTitle(context.getString(R.string.default_center_name));
		LeaveCategory.RIGHT.setTitle(context.getString(R.string.default_right_name));
		db.execSQL(LEAVE_CATEGORY_CREATE);

		db.execSQL(String.format("insert into %s (_id,title,display_pos,initial_hours,accrual,cap_type) VALUES (1,'%s','%d','%d','%d','%d')",
				LEAVE_CATEGORY_TABLE, LeaveCategory.LEFT.getTitle(), 0, 0, 1, LEAVE_CAP_TYPE.NONE));
		db.execSQL(String.format("insert into %s (_id,title,display_pos,initial_hours,accrual,cap_type) VALUES (2,'%s','%d','%d','%d','%d')",
				LEAVE_CATEGORY_TABLE, LeaveCategory.CENTER.getTitle(), 1, 0, 1, LEAVE_CAP_TYPE.NONE));
		db.execSQL(String.format("insert into %s (_id,title,display_pos,initial_hours,accrual,cap_type) VALUES (3,'%s','%d','%d','%d','%d')",
				LEAVE_CATEGORY_TABLE, LeaveCategory.RIGHT.getTitle(), 2, 0, 0, LEAVE_CAP_TYPE.NONE));

		db.execSQL(LEAVE_HISTORY_CREATE);
		if (!prefs.getString(LeaveCategory.LEFT.getPrefix() + "initialHours", "0").equals("0")) {
			db.execSQL(String
					.format("insert into leave_history (notes,number,date,leave_category_id) VALUES ('%s','%s',date('now'),'%f')",
							"Leave from Initial Hours preference",
							Float.parseFloat(prefs.getString(LeaveCategory.LEFT.getPrefix() + "initialHours", "0")), 1));
		}
		if (!prefs.getString(LeaveCategory.LEFT.getPrefix() + "hoursUsed", "0").equals("0")) {
			db.execSQL(String
					.format("insert into leave_history (notes,number,date,leave_category_id) VALUES ('%s','%s',date('now'),'%f')",
							"Leave from Hours Used preference",
							Float.parseFloat(prefs.getString(LeaveCategory.LEFT.getPrefix() + "hoursUsed", "0")), 1));
		}
		if (!prefs.getString(LeaveCategory.CENTER.getPrefix() + "initialHours", "0").equals("0")) {
			db.execSQL(String
					.format("insert into leave_history (notes,number,date,leave_category_id) VALUES ('%s','%s',date('now'),'%f')",
							"Leave from Initial Hours preference",
							Float.parseFloat(prefs.getString(LeaveCategory.CENTER.getPrefix() + "initialHours", "0")),
							1));
		}
		if (!prefs.getString(LeaveCategory.CENTER.getPrefix() + "hoursUsed", "0").equals("0")) {
			db.execSQL(String
					.format("insert into leave_history (notes,number,date,leave_category_id) VALUES ('%s','%s',date('now'),'%f')",
							"Leave from Hours Used preference",
							Float.parseFloat(prefs.getString(LeaveCategory.CENTER.getPrefix() + "hoursUsed", "0")), 2));
		}
		if (!prefs.getString(LeaveCategory.RIGHT.getPrefix() + "initialHours", "0").equals("0")) {
			db.execSQL(String
					.format("insert into leave_history (notes,number,date,leave_category_id) VALUES ('%s','%s',date('now'),'%f')",
							"Leave from Initial Hours preference",
							Float.parseFloat(prefs.getString(LeaveCategory.RIGHT.getPrefix() + "initialHours", "0")), 1));
		}
		if (!prefs.getString(LeaveCategory.RIGHT.getPrefix() + "hoursUsed", "0").equals("0")) {
			db.execSQL(String
					.format("insert into leave_history (notes,number,date,leave_category_id) VALUES ('%s','%s',date('now'),'%f')",
							"Leave from Hours Used preference",
							Float.parseFloat(prefs.getString(LeaveCategory.RIGHT.getPrefix() + "hoursUsed", "0")), 3));
		}
		db.execSQL(String.format("insert into %s (_id,notes,number,date,leave_category_id) VALUES (1,'%s',%d,%s,%d)",
				LEAVE_HISTORY_TABLE, "Test note 1", 8,"date('2012-02-03')",1));
		db.execSQL(String.format("insert into %s (_id,notes,number,date,leave_category_id) VALUES (2,'%s',%d,%s,%d)",
				LEAVE_HISTORY_TABLE, "Testing notes 2", 5,"date('2012-02-02')",2));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + LEAVE_HISTORY_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + LEAVE_CATEGORY_TABLE);
		onCreate(db);

	}
	
	//I'm not happy with this method, but at least my columns are defined somewhere consistently
	public static class LEAVE_HISTORY {
		public static final String ID = "_id";
		public static final String TITLE = "title";
		public static final String NUMBER = "number";
		public static final String DATE = "date";
		public static final String NOTES = "notes";
		public static final String CATEGORY = "leave_category_id";
	};
	public static class LEAVE_CATEGORY {
		public static final String ID = "_id";
		public static final String TITLE = "title";
		public static final String ACCRUAL_ON = "accrual_on";
		public static final String HOURS_PER_YEAR = "hours_per_year";
		public static final String CAP_TYPE = "leave_cap_type";
		public static final String CAP_VAL = "leave_cap_val";
		public static final String CATEGORY = "leave_category_id";
	};
	public static class LEAVE_CAP_TYPE {
		public static final int NONE = 0;
		public static final int MAX = 1;
		public static final int CARRY_OVER = 2;
	};
}
