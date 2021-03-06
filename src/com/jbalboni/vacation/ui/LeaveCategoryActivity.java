package com.jbalboni.vacation.ui;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jbalboni.vacation.R;

public class LeaveCategoryActivity extends SherlockFragmentActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.leave_category);
		getSupportActionBar().setTitle(R.string.cat_title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(getString(R.string.menu_add)).setIcon(R.drawable.ic_menu_add)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.menu_add))) {
			Intent intent = new Intent();
			intent.setClass(this, LeaveCategoryEditActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent();
			intent.setClass(this, LeaveTrackerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		return true;
	}
}
