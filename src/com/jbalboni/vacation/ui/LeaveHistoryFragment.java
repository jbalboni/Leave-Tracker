package com.jbalboni.vacation.ui;

import com.actionbarsherlock.app.SherlockListFragment;
import com.jbalboni.vacation.R;
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
import android.widget.ListView;
import android.widget.TextView;

public class LeaveHistoryFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int LEAVE_HISTORY_LOADER = 0x01;
	private int currentID;

	private SimpleCursorAdapter adapter;

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
		int leaveItemID = cursor.getInt(cursor.getColumnIndex(LeaveTrackerDatabase.LEAVE_HISTORY.ID));
		Intent intent = new Intent();
		intent.setClass(getActivity(), LeaveEditActivity.class);
		intent.putExtra(getString(R.string.intent_catid), getActivity().getIntent().getIntExtra(getString(R.string.intent_catid), 0));
		intent.putExtra(getString(R.string.intent_itemid), leaveItemID);
		startActivity(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String[] uiBindFrom = { LeaveTrackerDatabase.LEAVE_HISTORY.NUMBER, LeaveTrackerDatabase.LEAVE_HISTORY.DATE };
		int[] uiBindTo = { R.id.hours, R.id.date};

		getLoaderManager().initLoader(LEAVE_HISTORY_LOADER, null, this);

		adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.leave_history_row, null,
				uiBindFrom, uiBindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		setListAdapter(adapter);
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.leave_history_list, container, false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		//Seems wrong to do output formatting here
		String[] projection = { LeaveTrackerDatabase.LEAVE_HISTORY.ID, "cast(number as text)||\" hours\" as number", "date" };
		Builder listUri = LeaveHistoryProvider.LIST_URI.buildUpon().appendPath(Integer.toString(getActivity().getIntent().getIntExtra(getString(R.string.intent_catid), 2)));
		CursorLoader cursorLoader = new CursorLoader(getActivity(), listUri.build(), projection, null,
				null, LeaveTrackerDatabase.LEAVE_HISTORY.DATE+" DESC");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
		if (cursor.getCount() == 0) {
			TextView loadingView = (TextView) getView().findViewById(R.id.loading);
			loadingView.setText(R.string.no_records);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
	@Override
	public void onResume() {
		getLoaderManager().restartLoader(LEAVE_HISTORY_LOADER, null, this);
		super.onResume();
	}
}
