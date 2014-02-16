package com.jbalboni.vacation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import com.jbalboni.vacation.data.LeaveCategoryTable;
import com.jbalboni.vacation.data.LeaveHistoryTable;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class LeaveTrackerBackupAgent extends BackupAgent {

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState)
			throws IOException {
		backupCategories(data);
		backupEntries(data);
		backupPrefs(data);
	}

	private void backupCategories(BackupDataOutput data) throws IOException {
		// Create buffer stream and data output stream for our data
		ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
		DataOutputStream outWriter = new DataOutputStream(bufStream);
		// Write structured data
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter);
		
		SQLiteDatabase db = null;
		try {
			db = new LeaveTrackerDatabase(this).getReadableDatabase();
			Cursor c = db.query(LeaveCategoryTable.getName(), null, null, null, null, null, null);
			
			String[] category = new String[8];
			c.moveToFirst();
			while (!c.isAfterLast()) {
				category[0] = Integer.toString(c.getInt(0));
				category[1] = c.getString(1);
				category[2] = Integer.toString(c.getInt(2));
				category[3] = Float.toString(c.getFloat(3));
				category[4] = Float.toString(c.getFloat(4));
				category[5] = Integer.toString(c.getInt(5));
				category[6] = Integer.toString(c.getInt(6));
				category[7] = Float.toString(c.getFloat(7));
				writer.writeNext(category);
				c.moveToNext();
			}
			writer.close();
			outWriter.writeUTF(stringWriter.toString());
	
			// Send the data to the Backup Manager via the BackupDataOutput
			byte[] buffer = bufStream.toByteArray();
			int len = buffer.length;
			data.writeEntityHeader("Categories", len);
			data.writeEntityData(buffer, len);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void backupEntries(BackupDataOutput data) throws IOException {
		// Create buffer stream and data output stream for our data
		ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
		DataOutputStream outWriter = new DataOutputStream(bufStream);
		// Write structured data
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter);
		SQLiteDatabase db = null;
		try {
			db = new LeaveTrackerDatabase(this).getReadableDatabase();
			String[] selection = { LeaveHistoryTable.ID.toString(), LeaveHistoryTable.NOTES.toString(),
					LeaveHistoryTable.NUMBER.toString(), LeaveHistoryTable.DATE.toString(),
					LeaveHistoryTable.ADD_OR_USE.toString(), LeaveHistoryTable.CATEGORY.toString() };
			Cursor c = db.query(LeaveHistoryTable.getName(), selection, null, null, null, null, null);

			String[] entry = new String[6];
			c.moveToFirst();
			while (!c.isAfterLast()) {
				entry[0] = Integer.toString(c.getInt(0));
				entry[1] = c.getString(1);
				entry[2] = Float.toString(c.getFloat(2));
				entry[3] = c.getString(3);
				entry[4] = Integer.toString(c.getInt(4));
				entry[5] = Integer.toString(c.getInt(5));
				writer.writeNext(entry);
				c.moveToNext();
			}
			writer.close();

			if (c.getCount() > 0) {
				outWriter.writeUTF(stringWriter.toString());
				// Send the data to the Backup Manager via the BackupDataOutput
				byte[] buffer = bufStream.toByteArray();
				int len = buffer.length;
				data.writeEntityHeader("Entries", len);
				data.writeEntityData(buffer, len);
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void backupPrefs(BackupDataOutput data) throws IOException {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Create buffer stream and data output stream for our data
		ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
		DataOutputStream outWriter = new DataOutputStream(bufStream);
		// Write structured data
		outWriter.writeUTF(prefs.getString("startDate", null));

		// Send the data to the Backup Manager via the BackupDataOutput
		byte[] buffer = bufStream.toByteArray();
		int len = buffer.length;
		data.writeEntityHeader("startDate", len);
		data.writeEntityData(buffer, len);

		bufStream = new ByteArrayOutputStream();
		outWriter = new DataOutputStream(bufStream);
		// Write structured data
		outWriter.writeUTF(prefs.getString("leaveInterval", "Weekly"));

		// Send the data to the Backup Manager via the BackupDataOutput
		buffer = bufStream.toByteArray();
		len = buffer.length;
		data.writeEntityHeader("leaveInterval", len);
		data.writeEntityData(buffer, len);
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
		while (data.readNextHeader()) {
			String key = data.getKey();
			int dataSize = data.getDataSize();
			if (key.equals("Categories")) {
				restoreCategories(data, dataSize);
			} else if (key.equals("Entries")) {
				restoreEntries(data, dataSize);
			} else if (key.equals("leaveInterval") || key.equals("startDate")) {
				restorePref(key, data, dataSize);
			} else {
				data.skipEntityData();
			}
		}
	}

	private void restorePref(String key, BackupDataInput data, int dataSize) throws IOException {
		byte[] dataBuf = new byte[dataSize];
		data.readEntityData(dataBuf, 0, dataSize);
		ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
		DataInputStream in = new DataInputStream(baStream);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, in.readUTF());

		in.close();
		editor.commit();
	}

	private void restoreEntries(BackupDataInput data, int dataSize) throws IOException {
		byte[] dataBuf = new byte[dataSize];
		data.readEntityData(dataBuf, 0, dataSize);
		ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
		DataInputStream in = new DataInputStream(baStream);

		// Read the player name and score from the backup data
		String categoriesList = in.readUTF();
		CSVReader reader = new CSVReader(new StringReader(categoriesList));
		List<String[]> rows = reader.readAll();
		reader.close();
		SQLiteDatabase db = new LeaveTrackerDatabase(this).getReadableDatabase();
		for (String[] row : rows) {
			ContentValues categoryValues = new ContentValues();
			categoryValues.put(LeaveHistoryTable.NOTES.toString(), row[1]);
			categoryValues.put(LeaveHistoryTable.NUMBER.toString(), Float.parseFloat(row[2]));
			categoryValues.put(LeaveHistoryTable.DATE.toString(), row[3]);
			categoryValues.put(LeaveHistoryTable.ADD_OR_USE.toString(), Integer.parseInt(row[4]));
			categoryValues.put(LeaveHistoryTable.CATEGORY.toString(), Integer.parseInt(row[5]));
			db.insert(LeaveHistoryTable.getName(), null, categoryValues);
		}
		db.close();
		in.close();
	}

	private void restoreCategories(BackupDataInput data, int dataSize) throws IOException {
		byte[] dataBuf = new byte[dataSize];
		data.readEntityData(dataBuf, 0, dataSize);
		ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
		DataInputStream in = new DataInputStream(baStream);

		// Read the player name and score from the backup data
		String categoriesList = in.readUTF();
		CSVReader reader = new CSVReader(new StringReader(categoriesList));
		List<String[]> rows = reader.readAll();
		reader.close();
		SQLiteDatabase db = new LeaveTrackerDatabase(this).getReadableDatabase();
		for (String[] row : rows) {
			ContentValues categoryValues = new ContentValues();
			categoryValues.put(LeaveCategoryTable.TITLE.toString(), row[1]);
			categoryValues.put(LeaveCategoryTable.DISPLAY.toString(), Integer.parseInt(row[2]));
			categoryValues.put(LeaveCategoryTable.HOURS_PER_YEAR.toString(), Float.parseFloat(row[3]));
			categoryValues.put(LeaveCategoryTable.INITIAL_HOURS.toString(), Float.parseFloat(row[4]));
			categoryValues.put(LeaveCategoryTable.ACCRUAL.toString(), Integer.parseInt(row[5]));
			categoryValues.put(LeaveCategoryTable.CAP_TYPE.toString(), Integer.parseInt(row[6]));
			categoryValues.put(LeaveCategoryTable.CAP_VAL.toString(), Float.parseFloat(row[7]));
			String[] idArgs = { row[0] };
			db.update(LeaveCategoryTable.getName(), categoryValues, LeaveCategoryTable.ID + "=?", idArgs);
		}
		db.close();
		in.close();
	}

}
