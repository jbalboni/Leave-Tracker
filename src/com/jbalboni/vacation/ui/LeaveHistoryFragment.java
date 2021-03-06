package com.jbalboni.vacation.ui;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.data.LeaveCategoryTable;
import com.jbalboni.vacation.data.LeaveHistoryProvider;
import com.jbalboni.vacation.data.LeaveHistoryTable;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LeaveHistoryFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int LEAVE_HISTORY_LOADER = 0x01;
	private int currentID;

	private SimpleCursorAdapter adapter;

	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
	private static DateTimeFormatter fmtNoYear = DateTimeFormat.forPattern("MMM dd");
	private static DateTimeFormatter fmtView = DateTimeFormat.forPattern("MMM dd, yyyy");

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			currentID = savedInstanceState.getInt("currentID", 0);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentID", currentID);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = (Cursor) adapter.getItem(position);
		int leaveItemID = cursor.getInt(cursor.getColumnIndex(LeaveHistoryTable.ID.toString()));
		Intent intent = new Intent();
		intent.setClass(getActivity(), LeaveEditActivity.class);
		intent.putExtra(getString(R.string.intent_catid),
				getActivity().getIntent().getIntExtra(getString(R.string.intent_catid), 0));
		intent.putExtra(getString(R.string.intent_itemid), leaveItemID);
		startActivity(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String[] uiBindFrom = { LeaveHistoryTable.NUMBER.toString(), LeaveHistoryTable.DATE.toString(),
				LeaveHistoryTable.NOTES.toString() };
		int[] uiBindTo = { R.id.hours, R.id.date, R.id.notes };

		getLoaderManager().initLoader(LEAVE_HISTORY_LOADER, null, this);

		adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.leave_history_row, null,
				uiBindFrom, uiBindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		//formatting for list view
		SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int index) {
				if (cursor.getColumnName(index).equals(LeaveHistoryTable.NOTES.toString())) {
					if (cursor.getString(index).length() == 0) {
						view.setVisibility(View.GONE);
					} else {
						view.setVisibility(View.VISIBLE);
						ImageView button = (ImageView) view;
						final String notes = cursor.getString(index);
						button.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								NotesDialogFragment notesDialog = new NotesDialogFragment();
								notesDialog.setNotes(notes);
								notesDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.VacationDialog);
								notesDialog.show(getFragmentManager(), "notes");
							}
						});
					}
					return true;
				} else if (cursor.getColumnName(index).equals(LeaveHistoryTable.DATE.toString())) {
					TextView dateView = (TextView) view;
					LocalDate date = fmt.parseLocalDate(cursor.getString(index));
					if (date.getYear() == (new LocalDate()).getYear()) {
						dateView.setText(fmtNoYear.print(date));
					} else {
						dateView.setText(fmtView.print(date));
					}
					return true;
				} else if (cursor.getColumnName(index).equals(LeaveHistoryTable.NUMBER.toString())) {
					TextView hoursView = (TextView) view;
					int addOrUse = cursor.getInt(cursor.getColumnIndex(LeaveHistoryTable.ADD_OR_USE.toString()));
					float hours = cursor.getFloat(index);
					if (addOrUse == 1) {
						hoursView.setText(String.format("%.2f hours added", hours));
					} else {
						hoursView.setText(String.format("%.2f hours used", hours));
					}
					return true;
				}
				return false;
			}
		};
		adapter.setViewBinder(binder);
		setListAdapter(adapter);

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.leave_history_list, container, false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Seems wrong to do output formatting here
		String[] projection = { LeaveHistoryTable.ID.toString(), LeaveHistoryTable.NUMBER.toString(),
				LeaveHistoryTable.DATE.toString(), LeaveHistoryTable.NOTES.toString(),
				LeaveHistoryTable.ADD_OR_USE.toString() };
		Builder listUri = LeaveHistoryProvider.LIST_URI.buildUpon().appendPath(
				Integer.toString(getActivity().getIntent().getIntExtra(getString(R.string.intent_catid), 2)));
		CursorLoader cursorLoader = new CursorLoader(getActivity(), listUri.build(), projection, null, null,
				LeaveHistoryTable.DATE + " DESC");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	@Override
	public void onResume() {
		Cursor cursor = ((SimpleCursorAdapter) getListAdapter()).getCursor();
		if (cursor != null) {
			cursor.requery();
		}
		setTitle();
		super.onResume();
	}

	private void setTitle() {
		SQLiteQueryBuilder titleQuery;
		LeaveTrackerDatabase leaveDB;
		String selTitle = LeaveCategoryTable.ID.toString() + "=?";
		String[] projection = { LeaveCategoryTable.TITLE.toString() };
		leaveDB = new LeaveTrackerDatabase(getActivity());
		titleQuery = new SQLiteQueryBuilder();
		titleQuery.setTables(LeaveCategoryTable.getName());
		Cursor cursor = titleQuery.query(
				leaveDB.getReadableDatabase(),
				projection,
				selTitle,
				new String[] { Integer.toString(getActivity().getIntent().getIntExtra(getString(R.string.intent_catid),
						2)) }, null, null, null);
		cursor.moveToFirst();
		getSherlockActivity().getSupportActionBar().setTitle(cursor.getString(0));
		cursor.close();
		leaveDB.close();
	}

	public static class NotesDialogFragment extends DialogFragment implements OnClickListener {

		private String notes;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			getDialog().setTitle(getString(R.string.notes));

			View view = inflater.inflate(R.layout.notes_fragment, container);
			TextView notesView = (TextView) view.findViewById(R.id.notes);
			notesView.setText(notes);

			Button button = (Button) view.findViewById(R.id.close);
			button.setOnClickListener(this);

			return view;
		}

		@Override
		public void onClick(View v) {
			Button clickedButton = (Button) v;
			if (clickedButton.getId() == R.id.close) {
				getDialog().dismiss();
			}
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}
	}
}
