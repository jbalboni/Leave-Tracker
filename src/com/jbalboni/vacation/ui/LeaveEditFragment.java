package com.jbalboni.vacation.ui;

import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.data.LeaveHistoryProvider;
import com.jbalboni.vacation.data.LeaveHistoryTable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LeaveEditFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>,
		OnDateChangedListener {
	private static final int LEAVE_HISTORY_LOADER = 0x01;
	private static final DateTimeFormatter fmt = ISODateTimeFormat.localDateParser();
	private int itemID;
	private int addOrUse;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			itemID = savedInstanceState.getInt("itemID", 0);
		} else {
			itemID = getActivity().getIntent().getIntExtra(getString(R.string.intent_itemid), 0);
		}

		if (itemID == 0) {
			getSherlockActivity().getSupportActionBar().setTitle(R.string.menu_add);
			changeAddOrUse(0);
		} else {
			getSherlockActivity().getSupportActionBar().setTitle(R.string.menu_edit);
			getLoaderManager().initLoader(LEAVE_HISTORY_LOADER, null, this);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("itemID", itemID);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View editLayout = inflater.inflate(R.layout.leave_edit_fragment, container, false);
		DatePicker datePicker = (DatePicker) editLayout.findViewById(R.id.datePicker);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			datePicker.setCalendarViewShown(false);
		}
		return editLayout;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Seems wrong to do output formatting here
		String[] projection = { LeaveHistoryTable.ID.toString(), LeaveHistoryTable.NUMBER.toString(),
				LeaveHistoryTable.NOTES.toString(), LeaveHistoryTable.DATE.toString(),
				LeaveHistoryTable.ADD_OR_USE.toString() };
		Builder itemUri = LeaveHistoryProvider.CONTENT_URI.buildUpon().appendPath(
				Integer.toString(getActivity().getIntent().getIntExtra(getString(R.string.intent_itemid), 0)));
		CursorLoader cursorLoader = new CursorLoader(getActivity(), itemUri.build(), projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		cursor.moveToFirst();

		int notesCol = cursor.getColumnIndex(LeaveHistoryTable.NOTES.toString());
		EditText editNotes = (EditText) getView().findViewById(R.id.notes);
		editNotes.setText(cursor.getString(notesCol));

		int dateCol = cursor.getColumnIndex(LeaveHistoryTable.DATE.toString());
		LocalDate date = new LocalDate(fmt.parseLocalDate(cursor.getString(dateCol)));
		DatePicker editDate = (DatePicker) getView().findViewById(R.id.datePicker);
		editDate.init(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth(), this);

		int hoursCol = cursor.getColumnIndex(LeaveHistoryTable.NUMBER.toString());
		EditText editHours = (EditText) getView().findViewById(R.id.hours);
		editHours.setText(cursor.getString(hoursCol));
		
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		int addOrUseCol = cursor.getColumnIndex(LeaveHistoryTable.ADD_OR_USE.toString());
		if (cursor.getInt(addOrUseCol) == 1) {
			actionBar.getTabAt(1).select();
			changeAddOrUse(1);
		} else {
			actionBar.getTabAt(0).select();
			changeAddOrUse(0);
		}
	}

	public void saveLeaveItem() {
		try {
			ContentValues leaveItemValues = new ContentValues();
	
			EditText editNotes = (EditText) getView().findViewById(R.id.notes);
			leaveItemValues.put(LeaveHistoryTable.NOTES.toString(), editNotes.getText().toString());
	
			EditText editHours = (EditText) getView().findViewById(R.id.hours);
			leaveItemValues.put(LeaveHistoryTable.NUMBER.toString(), editHours.getText().toString());
			
			leaveItemValues.put(LeaveHistoryTable.ADD_OR_USE.toString(), addOrUse);
	
			DatePicker editDate = (DatePicker) getView().findViewById(R.id.datePicker);
			leaveItemValues.put(LeaveHistoryTable.DATE.toString(),
					new LocalDate(editDate.getYear(), editDate.getMonth() + 1, editDate.getDayOfMonth()).toString());
	
			leaveItemValues.put(LeaveHistoryTable.CATEGORY.toString(),
					getActivity().getIntent().getIntExtra(getString(R.string.intent_catid), 0));
	
			if (itemID == 0) {
				getActivity().getContentResolver().insert(LeaveHistoryProvider.CONTENT_URI, leaveItemValues);
				Toast.makeText(getActivity(), R.string.added_msg, Toast.LENGTH_LONG).show();
			} else {
				String[] idArgs = { Integer.toString(itemID) };
				int updatedRows = getActivity().getContentResolver().update(LeaveHistoryProvider.CONTENT_URI,
						leaveItemValues, LeaveHistoryTable.ID + "=?", idArgs);
				if (updatedRows > 0) {
					Toast.makeText(getActivity(), R.string.saved_msg, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(), R.string.error_msg, Toast.LENGTH_LONG).show();
				}
	
			}
			getActivity().finish();
		} catch (IllegalFieldValueException e) {
			Toast.makeText(getActivity(), R.string.invalid_date, Toast.LENGTH_SHORT).show();
		} 
	}

	public void deleteLeaveItem() {
		if (itemID != 0) {
			String[] idArgs = { Integer.toString(itemID) };
			int updatedRows = getActivity().getContentResolver().delete(LeaveHistoryProvider.CONTENT_URI,
					LeaveHistoryTable.ID + "=?", idArgs);
			if (updatedRows > 0) {
				Toast.makeText(getActivity(), R.string.deleted_msg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(), R.string.error_msg, Toast.LENGTH_LONG).show();
			}
			getActivity().finish();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// adapter.swapCursor(null);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub

	}

	public void changeAddOrUse(int addOrUse) {
		this.addOrUse = addOrUse;
		TextView hoursLabel = (TextView) getView().findViewById(R.id.hoursLabel);
		if (addOrUse == 1) {
			hoursLabel.setText(R.string.ItemLabelHoursAdd);
		} else {
			hoursLabel.setText(R.string.ItemLabelHours);
		}
	}

}
