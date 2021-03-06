package com.jbalboni.vacation.ui;

import com.actionbarsherlock.app.SherlockListFragment;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.data.LeaveCategoryProvider;
import com.jbalboni.vacation.data.LeaveCategoryTable;

import android.content.Intent;
import android.database.Cursor;
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

public class LeaveCategoryFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int LEAVE_CATEGORY_LOADER = 0x02;
	private int currentID;

	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			currentID = savedInstanceState.getInt("currentID", 0);
		}

		String[] uiBindFrom = { "title" };
		int[] uiBindTo = { R.id.categoryTitle };

		getLoaderManager().initLoader(LEAVE_CATEGORY_LOADER, null, this);

		adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.leave_category_row, null,
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
		int categoryID = cursor.getInt(cursor.getColumnIndex(LeaveCategoryTable.ID.toString()));
		Intent intent = new Intent();
		intent.setClass(getActivity(), LeaveHistoryActivity.class);
		intent.putExtra(getString(R.string.intent_catid), categoryID);
		startActivity(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.leave_category_list, container, false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { LeaveCategoryTable.ID.toString(), "title" };
		CursorLoader cursorLoader = new CursorLoader(getActivity(), LeaveCategoryProvider.CONTENT_URI, projection,
				null, null, null);
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
		((SimpleCursorAdapter) getListAdapter()).notifyDataSetChanged();
		super.onResume();
	}
}
