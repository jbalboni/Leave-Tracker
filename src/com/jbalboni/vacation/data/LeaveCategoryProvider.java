package com.jbalboni.vacation.data;

import android.app.backup.BackupManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class LeaveCategoryProvider extends ContentProvider {
	private LeaveTrackerDatabase leaveDB;
	private BackupManager backupManager;
	private static final String AUTHORITY = "com.jbalboni.vacation.data.LeaveCategoryProvider";
	public static final int CATEGORY_LIST = 100;
	public static final int CATEGORY_ID = 110;

	private static final String LEAVE_CATEGORY_BASE_PATH = "leave-category";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + LEAVE_CATEGORY_BASE_PATH);

	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/leave-category";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/leave-category";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, LEAVE_CATEGORY_BASE_PATH, CATEGORY_LIST);
		sURIMatcher.addURI(AUTHORITY, LEAVE_CATEGORY_BASE_PATH + "/#", CATEGORY_ID);
	}

	@Override
	public boolean onCreate() {
		leaveDB = new LeaveTrackerDatabase(getContext());
		backupManager = new BackupManager(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = leaveDB.getReadableDatabase();
		int rows = db.delete(LeaveCategoryTable.getName(), selection, selectionArgs);
		backupManager.dataChanged();
		return rows;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = leaveDB.getReadableDatabase();
		long newID = db.insert(LeaveCategoryTable.getName(), null, values);
		backupManager.dataChanged();
		return uri.buildUpon().appendPath(Long.toString(newID)).build();
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
	        String[] selectionArgs, String sortOrder) {
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	    queryBuilder.setTables(LeaveCategoryTable.getName());
	 
	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case CATEGORY_ID:
	        queryBuilder.appendWhere(LeaveCategoryTable.ID + "="
	                + uri.getLastPathSegment());
	        break;
	    case CATEGORY_LIST:
	    	//no filter
	        break;
	    default:
	        throw new IllegalArgumentException("Unknown URI");
	    }
	 
	    Cursor cursor = queryBuilder.query(leaveDB.getReadableDatabase(),
	            projection, selection, selectionArgs, null, null, sortOrder);
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = leaveDB.getReadableDatabase();
		int rows = db.update(LeaveCategoryTable.getName(), values, selection, selectionArgs);
		backupManager.dataChanged();
		return rows;
	}
}