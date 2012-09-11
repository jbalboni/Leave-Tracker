package com.jbalboni.vacation.data;

public enum LeaveCategoryTable {
	ID("_id"), TITLE("title"), ACCRUAL("accrual"), HOURS_PER_YEAR("hours_per_year"), CAP_TYPE("cap_type"), CAP_VAL(
			"cap_val"), INITIAL_HOURS("initial_hours"), DISPLAY("display_pos");

	private static String createScript = "CREATE TABLE leave_categories ( _id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, display_pos INTEGER, hours_per_year REAL NOT NULL, initial_hours REAL NOT NULL, accrual INTEGER NOT NULL, cap_type INTEGER NOT NULL, cap_val REAL)";
	private static String tableName = "leave_categories";
	private String colName;

	LeaveCategoryTable(String colName) {
		this.colName = colName;
	}

	@Override
	public String toString() {
		return colName;
	}

	public static String getTableCreate() {
		return createScript;
	}
	
	public static String getName() {
		return tableName;
	}
}
