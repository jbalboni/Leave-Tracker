package com.jbalboni.vacation;

import org.joda.time.LocalDate;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.jbalboni.vacation.data.LeaveHistoryProvider;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class LeaveEditFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnDateChangedListener {
	private static final int LEAVE_HISTORY_LOADER = 0x01;
	private int currentID;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			currentID = savedInstanceState.getInt("currentID", 0);
		}

		getLoaderManager().initLoader(LEAVE_HISTORY_LOADER, null, this);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentID", currentID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View editLayout = inflater.inflate(R.layout.leave_item, container, false);
		DatePicker datePicker = (DatePicker) editLayout.findViewById(R.id.datePicker);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			datePicker.setCalendarViewShown(false);
		}
		return editLayout;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		//Seems wrong to do output formatting here
		String[] projection = { LeaveTrackerDatabase.ID, "number", "notes", "date" };
		Builder itemUri = LeaveHistoryProvider.CONTENT_URI.buildUpon().appendPath(Integer.toString(getActivity().getIntent().getIntExtra(getString(R.string.intent_itemid),0)));
		CursorLoader cursorLoader = new CursorLoader(getActivity(), itemUri.build(), projection, null,
				null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		cursor.moveToFirst();
		
		int notesCol = cursor.getColumnIndex("notes");
		EditText editNotes = (EditText) getView().findViewById(R.id.notes);
		editNotes.setText(cursor.getString(notesCol));
		
		int dateCol = cursor.getColumnIndex("date");
		LocalDate date = new LocalDate(cursor.getLong(dateCol));
		DatePicker editDate = (DatePicker) getView().findViewById(R.id.datePicker);
		editDate.init(date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth(), this);
		
		int hoursCol = cursor.getColumnIndex("number");
		EditText editHours = (EditText) getView().findViewById(R.id.hours);
		editHours.setText(cursor.getString(hoursCol));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		//adapter.swapCursor(null);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub
		
	}
}
