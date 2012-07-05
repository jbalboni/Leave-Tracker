package com.jbalboni.vacation;

import com.jbalboni.vacation.data.LeaveHistoryProvider;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

public class LeaveHistoryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
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

		String[] uiBindFrom = { "number", "date" };
		int[] uiBindTo = { R.id.hours, R.id.date};

		getLoaderManager().initLoader(LEAVE_HISTORY_LOADER, null, this);

		adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.leave_history_row, null,
				uiBindFrom, uiBindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		setListAdapter(adapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentID", currentID);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = (Cursor) adapter.getItem(position);
		int leaveItemID = cursor.getInt(cursor.getColumnIndex(LeaveTrackerDatabase.ID));
		Intent intent = new Intent();
		intent.setClass(getActivity(), LeaveItemActivity.class);
		intent.putExtra("itemID", leaveItemID);
		startActivity(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.leave_history_list, container, false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		//Seems wrong to do output formatting here
		String[] projection = { LeaveTrackerDatabase.ID, "cast(number as text)||\" hours\" as number", "strftime(\"%m/%d\",date) as date" };
		Builder listUri = LeaveHistoryProvider.LIST_URI.buildUpon().appendPath(Integer.toString(getActivity().getIntent().getIntExtra("com.jbalboni.vacation.catID", 2)));
		CursorLoader cursorLoader = new CursorLoader(getActivity(), listUri.build(), projection, null,
				null, null);
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
}