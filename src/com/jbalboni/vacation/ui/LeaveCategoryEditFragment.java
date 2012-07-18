package com.jbalboni.vacation.ui;

import org.joda.time.LocalDate;

import com.actionbarsherlock.app.SherlockFragment;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.data.LeaveCategoryProvider;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

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
import android.widget.Toast;

public class LeaveCategoryEditFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnDateChangedListener {
	private static final int LEAVE_CATEGORY_LOADER = 0x03;
	private int catID;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			catID = savedInstanceState.getInt("catID", 0);
		} else {
			catID = getActivity().getIntent().getIntExtra(getString(R.string.intent_catid),0);
		}
		
		if (catID == 0) {
			getSherlockActivity().getSupportActionBar().setTitle(R.string.menu_add);
		} else {
			getSherlockActivity().getSupportActionBar().setTitle(R.string.menu_edit);
			getLoaderManager().initLoader(LEAVE_CATEGORY_LOADER, null, this);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("catID", catID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View editLayout = inflater.inflate(R.layout.leave_category_edit_fragment, container, false);
		return editLayout;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		//Seems wrong to do output formatting here
		String[] projection = { LeaveTrackerDatabase.LEAVE_CATEGORY.ID, "number", "notes", "date" };
		Builder itemUri = LeaveCategoryProvider.CONTENT_URI.buildUpon().appendPath(Integer.toString(getActivity().getIntent().getIntExtra(getString(R.string.intent_catid),0)));
		CursorLoader cursorLoader = new CursorLoader(getActivity(), itemUri.build(), projection, null,
				null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		cursor.moveToFirst();
		
//		int notesCol = cursor.getColumnIndex("notes");
//		EditText editNotes = (EditText) getView().findViewById(R.id.notes);
//		editNotes.setText(cursor.getString(notesCol));
//		
//		int dateCol = cursor.getColumnIndex("date");
//		LocalDate date = new LocalDate(fmt.parseLocalDate(cursor.getString(dateCol)));
//		DatePicker editDate = (DatePicker) getView().findViewById(R.id.datePicker);
//		editDate.init(date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth(), this);
//		
//		int hoursCol = cursor.getColumnIndex("number");
//		EditText editHours = (EditText) getView().findViewById(R.id.hours);
//		editHours.setText(cursor.getString(hoursCol));
	}
	
	public void saveCategory() {
		ContentValues leaveItemValues = new ContentValues();

		EditText editNotes = (EditText) getView().findViewById(R.id.notes);
		leaveItemValues.put(LeaveTrackerDatabase.LEAVE_HISTORY.NOTES, editNotes.getText().toString());
		
		EditText editHours = (EditText) getView().findViewById(R.id.hours);
		leaveItemValues.put(LeaveTrackerDatabase.LEAVE_HISTORY.NUMBER, editHours.getText().toString());
		
		DatePicker editDate = (DatePicker) getView().findViewById(R.id.datePicker);
		leaveItemValues.put(LeaveTrackerDatabase.LEAVE_HISTORY.DATE, new LocalDate(editDate.getYear(),editDate.getMonth()+1,editDate.getDayOfMonth()).toString());
		
		leaveItemValues.put(LeaveTrackerDatabase.LEAVE_HISTORY.CATEGORY, getActivity().getIntent().getIntExtra(getString(R.string.intent_catid), 0));

		if (catID == 0) {
			getActivity().getContentResolver().insert(
					LeaveCategoryProvider.CONTENT_URI,
					leaveItemValues
			);
			Toast.makeText(getActivity(), R.string.added_msg, Toast.LENGTH_LONG).show();
		} else {
			String[] idArgs = {Integer.toString(catID)};
			int updatedRows = getActivity().getContentResolver().update(
					LeaveCategoryProvider.CONTENT_URI,
					leaveItemValues,
					LeaveTrackerDatabase.LEAVE_HISTORY.ID+"=?",
					idArgs
			);
			if (updatedRows > 0) {
				Toast.makeText(getActivity(), R.string.saved_msg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(), R.string.error_msg, Toast.LENGTH_LONG).show();
			}
			
		}
		getActivity().finish();
	}
	
	public void deleteCategory() {
		if (catID != 0) {
			String[] idArgs = {Integer.toString(catID)};
			int updatedRows = getActivity().getContentResolver().delete(
					LeaveCategoryProvider.CONTENT_URI,
					LeaveTrackerDatabase.LEAVE_CATEGORY.ID+"=?",
					idArgs
			);
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
		//adapter.swapCursor(null);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub
		
	}

}
