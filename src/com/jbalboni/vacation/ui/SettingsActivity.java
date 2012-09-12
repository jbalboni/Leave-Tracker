package com.jbalboni.vacation.ui;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.data.LeaveCategoryTable;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.preference.Preference;

public class SettingsActivity extends SherlockPreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getSupportActionBar().setTitle(R.string.menu_settings);
		this.setTheme(R.style.VacationTheme);
		LeaveTrackerDatabase leaveDB = new LeaveTrackerDatabase(getApplicationContext());
		SQLiteDatabase db = leaveDB.getReadableDatabase();
		SQLiteQueryBuilder titlesQuery = new SQLiteQueryBuilder();
		titlesQuery.setTables(LeaveCategoryTable.getName());
		Cursor cursor = titlesQuery.query(db,
				new String[] { LeaveCategoryTable.TITLE.toString(), LeaveCategoryTable.ID.toString() }, null,
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Preference linkPref = new Preference(getApplicationContext());
			linkPref.setKey(String.format("editCategory%d",cursor.getInt(1)));
			linkPref.setTitle(String.format("Edit %s category",cursor.getString(0)));
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, LeaveCategoryEditActivity.class);
			int catID = cursor.getInt(1);
			intent.putExtra(getString(R.string.intent_catid), catID);
			linkPref.setIntent(intent);
			getPreferenceScreen().addPreference(linkPref);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
	}
}
