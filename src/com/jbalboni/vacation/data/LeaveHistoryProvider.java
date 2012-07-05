package com.jbalboni.vacation.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class LeaveHistoryProvider extends ContentProvider {
	private LeaveTrackerDatabase leaveDB;
	private static final String AUTHORITY = "com.jbalboni.vacation.data.LeaveHistoryProvider";
	public static final int LEAVE_HISTORY = 100;
	public static final int LEAVE_HISTORY_ID = 110;

	private static final String LEAVE_HISTORY_BASE_PATH = "leave-history";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + LEAVE_HISTORY_BASE_PATH);
	public static final Uri LIST_URI = Uri.parse("content://" + AUTHORITY + "/" + LEAVE_HISTORY_BASE_PATH + "/category");

	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/leave-history";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/leave-history";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, LEAVE_HISTORY_BASE_PATH + "/category/#", LEAVE_HISTORY);
		sURIMatcher.addURI(AUTHORITY, LEAVE_HISTORY_BASE_PATH + "/#", LEAVE_HISTORY_ID);
	}

	@Override
	public boolean onCreate() {
		leaveDB = new LeaveTrackerDatabase(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
	        String[] selectionArgs, String sortOrder) {
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	    queryBuilder.setTables(LeaveTrackerDatabase.LEAVE_HISTORY_TABLE);
	 
	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case LEAVE_HISTORY_ID:
	        queryBuilder.appendWhere(LeaveTrackerDatabase.ID + "="
	                + uri.getLastPathSegment());
	        break;
	    case LEAVE_HISTORY:
	    	queryBuilder.appendWhere(LeaveTrackerDatabase.CATEGORY_ID + "="
	                + uri.getLastPathSegment());
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
		// TODO Auto-generated method stub
		return 0;
	}
}