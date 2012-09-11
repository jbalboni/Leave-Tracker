package com.jbalboni.vacation.data;

public enum LeaveHistoryTable {
	ID("_id"), TITLE("title"), NUMBER("number"), DATE("date"), NOTES("notes"), CATEGORY("leave_category_id"), ADD_OR_USE(
			"add_or_use");

	private static String createScript = "CREATE TABLE leave_history ( _id INTEGER PRIMARY KEY AUTOINCREMENT, notes TEXT, number REAL, date DATE, leave_category_id INTEGER NOT NULL, FOREIGN KEY(leave_category_id) REFERENCES leave_categories(_id), add_or_use INTEGER)";
	private static String tableName;
	private String colName;

	LeaveHistoryTable(String colName) {
		this.colName = colName;
	}

	@Override
	public String toString() {
		return colName;
	}
	
	public static String getName() {
		return tableName;
	}

	public static String getTableCreate() {
		return createScript;
	}
}
