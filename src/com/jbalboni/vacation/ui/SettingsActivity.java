package com.jbalboni.vacation.ui;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.data.LeaveCategoryTable;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class SettingsActivity extends SherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		getSupportActionBar().setTitle(R.string.menu_settings);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		LeaveTrackerDatabase leaveDB = new LeaveTrackerDatabase(getApplicationContext());
		SQLiteDatabase db = leaveDB.getReadableDatabase();
		SQLiteQueryBuilder titlesQuery = new SQLiteQueryBuilder();
		titlesQuery.setTables(LeaveCategoryTable.getName());
		Cursor cursor = titlesQuery.query(db,
				new String[] { LeaveCategoryTable.TITLE.toString(), LeaveCategoryTable.ID.toString() }, null,
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Preference linkPref = getPreferenceScreen().findPreference(String.format("editCat%d",cursor.getInt(1)));
			linkPref.setTitle(String.format("%s Settings",cursor.getString(0)));
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, LeaveCategoryEditActivity.class);
			int catID = cursor.getInt(1);
			intent.putExtra(getString(R.string.intent_catid), catID);
			linkPref.setIntent(intent);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent();
			intent.setClass(this, LeaveTrackerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		return true;
	}
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
		(new BackupManager(this)).dataChanged();

    }
}
