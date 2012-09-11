package com.jbalboni.vacation.ui;

import com.actionbarsherlock.app.SherlockFragment;
import com.jbalboni.vacation.LeaveCapType;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.data.LeaveCategoryProvider;
import com.jbalboni.vacation.data.LeaveCategoryTable;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LeaveCategoryEditFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>,
		OnDateChangedListener, OnItemSelectedListener {
	private static final int LEAVE_CATEGORY_LOADER = 0x03;
	private int catID;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			catID = savedInstanceState.getInt("catID", 0);
		} else {
			catID = getActivity().getIntent().getIntExtra(getString(R.string.intent_catid), 0);
		}

		if (catID == 0) {
			getSherlockActivity().getSupportActionBar().setTitle(R.string.menu_add_cat);
		} else {
			getSherlockActivity().getSupportActionBar().setTitle(R.string.menu_edit_cat);
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
		
		//Set up limit list
		Spinner spinner = (Spinner) editLayout.findViewById(R.id.leaveCapType);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.leave_cap_types,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(this);
		
		return editLayout;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Seems wrong to do output formatting here
		String[] projection = { LeaveCategoryTable.ID.toString(), LeaveCategoryTable.ACCRUAL.toString(),
				LeaveCategoryTable.HOURS_PER_YEAR.toString(), LeaveCategoryTable.TITLE.toString(),
				LeaveCategoryTable.CAP_TYPE.toString(), LeaveCategoryTable.CAP_VAL.toString(),
				LeaveCategoryTable.INITIAL_HOURS.toString() };
		Builder itemUri = LeaveCategoryProvider.CONTENT_URI.buildUpon().appendPath(
				Integer.toString(getActivity().getIntent().getIntExtra(getString(R.string.intent_catid), 0)));
		CursorLoader cursorLoader = new CursorLoader(getActivity(), itemUri.build(), projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		cursor.moveToFirst();

		int colNum = 0;
		EditText editView;

		colNum = cursor.getColumnIndex(LeaveCategoryTable.TITLE.toString());
		editView = (EditText) getView().findViewById(R.id.categoryTitle);
		editView.setText(cursor.getString(colNum));

		colNum = cursor.getColumnIndex(LeaveCategoryTable.CAP_TYPE.toString());
		Spinner spinner = (Spinner) getView().findViewById(R.id.leaveCapType);
		spinner.setSelection(cursor.getInt(colNum));
		setLeaveCapVal(cursor.getInt(colNum));
		
		colNum = cursor.getColumnIndex(LeaveCategoryTable.CAP_VAL.toString());
		editView = (EditText) getView().findViewById(R.id.leaveCapVal);
		editView.setText(Float.toString(cursor.getFloat(colNum)));	

		colNum = cursor.getColumnIndex(LeaveCategoryTable.HOURS_PER_YEAR.toString());
		editView = (EditText) getView().findViewById(R.id.hoursPerYear);
		editView.setText(Float.toString(cursor.getFloat(colNum)));

		colNum = cursor.getColumnIndex(LeaveCategoryTable.INITIAL_HOURS.toString());
		editView = (EditText) getView().findViewById(R.id.initialHours);
		editView.setText(Float.toString(cursor.getFloat(colNum)));

		colNum = cursor.getColumnIndex(LeaveCategoryTable.ACCRUAL.toString());
		CheckBox editBox = (CheckBox) getView().findViewById(R.id.accrual);
		editBox.setChecked(cursor.getInt(colNum) == 1);
		toggleAccrual(editBox.isChecked());
	}

	public void saveCategory() {
		ContentValues categoryValues = new ContentValues();
		
		EditText editView;
		
		editView = (EditText) getView().findViewById(R.id.categoryTitle);
		categoryValues.put(LeaveCategoryTable.TITLE.toString(), editView.getText().toString());
		
		editView = (EditText) getView().findViewById(R.id.hoursPerYear);
		categoryValues.put(LeaveCategoryTable.HOURS_PER_YEAR.toString(), LeaveTrackerDatabase.getFloat(editView.getText().toString()));
		
		editView = (EditText) getView().findViewById(R.id.initialHours);
		categoryValues.put(LeaveCategoryTable.INITIAL_HOURS.toString(), LeaveTrackerDatabase.getFloat(editView.getText().toString()));
		
		editView = (EditText) getView().findViewById(R.id.leaveCapVal);
		categoryValues.put(LeaveCategoryTable.CAP_VAL.toString(), LeaveTrackerDatabase.getFloat(editView.getText().toString()));
		
		CheckBox accrualOn = (CheckBox) getView().findViewById(R.id.accrual);
		categoryValues.put(LeaveCategoryTable.ACCRUAL.toString(), accrualOn.isChecked() ? 1 : 0);
		
		Spinner leaveCapType = (Spinner) getView().findViewById(R.id.leaveCapType);
		categoryValues.put(LeaveCategoryTable.CAP_TYPE.toString(), leaveCapType.getSelectedItemPosition());

		if (catID == 0) {
			getActivity().getContentResolver().insert(LeaveCategoryProvider.CONTENT_URI, categoryValues);
			Toast.makeText(getActivity(), R.string.added_msg, Toast.LENGTH_LONG).show();
		} else {
			String[] idArgs = { Integer.toString(catID) };
			int updatedRows = getActivity().getContentResolver().update(LeaveCategoryProvider.CONTENT_URI,
					categoryValues, LeaveCategoryTable.ID + "=?", idArgs);
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
			String[] idArgs = { Integer.toString(catID) };
			int updatedRows = getActivity().getContentResolver().delete(LeaveCategoryProvider.CONTENT_URI,
					LeaveCategoryTable.ID + "=?", idArgs);
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
	
	private void setLeaveCapVal(int position) {
		EditText editView = (EditText) getView().findViewById(R.id.leaveCapVal);
		TextView view = (TextView) getView().findViewById(R.id.leaveCapValLabel);
		//Need to hide the leave cap value field if there is no cap
		LeaveCapType capType = LeaveCapType.getLeaveCapType(position);
		if (capType == LeaveCapType.NONE) {
			editView.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
		} else if (capType == LeaveCapType.MAX) {
			view.setText(R.string.leave_pref_cap_val_max);
			editView.setVisibility(View.VISIBLE);
			view.setVisibility(View.VISIBLE);
		} else if (capType == LeaveCapType.CARRYOVER) {
			view.setText(R.string.leave_pref_cap_val_carry);
			editView.setVisibility(View.VISIBLE);
			view.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		setLeaveCapVal(pos);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	public void toggleAccrual(boolean accrualOn) {
		View perYearView = getView().findViewById(R.id.hoursPerYear);
		View perYearLabelView = getView().findViewById(R.id.hoursPerYearLabel);
		Spinner leaveCapType = (Spinner) getView().findViewById(R.id.leaveCapType);
		View leaveCapLabelView = getView().findViewById(R.id.leaveCapTypeLabel);
		View leaveCapValView = getView().findViewById(R.id.leaveCapVal);
		View leaveCapValLabelView = getView().findViewById(R.id.leaveCapValLabel);
		if (accrualOn) {
			perYearView.setVisibility(View.VISIBLE);
			perYearLabelView.setVisibility(View.VISIBLE);
			leaveCapType.setVisibility(View.VISIBLE);
			leaveCapLabelView.setVisibility(View.VISIBLE);
			setLeaveCapVal(leaveCapType.getSelectedItemPosition());
		} else {
			perYearView.setVisibility(View.GONE);
			perYearLabelView.setVisibility(View.GONE);
			leaveCapType.setVisibility(View.GONE);
			leaveCapLabelView.setVisibility(View.GONE);
			leaveCapValView.setVisibility(View.GONE);
			leaveCapValLabelView.setVisibility(View.GONE);
		}
		
	}

}
