package com.jbalboni.vacation.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jbalboni.vacation.R;

import android.content.Intent;
import android.os.Bundle;

public class LeaveHistoryActivity extends SherlockFragmentActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leave_history);
        getSupportActionBar().setTitle(getIntent().getStringExtra(getString(R.string.intent_catname)));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.menu_add).setIcon(R.drawable.ic_menu_add)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(R.string.menu_edit_cat).setIcon(R.drawable.ic_menu_edit)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(R.string.menu_delete_cat).setIcon(R.drawable.ic_menu_delete)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		String title = item.getTitle().toString();
		if (title.equals(getString(R.string.menu_add))) {
			Intent intent = new Intent();
			intent.setClass(this, LeaveEditActivity.class);
			intent.putExtra(getString(R.string.intent_catid), getIntent().getIntExtra(getString(R.string.intent_catid), 0));
			startActivity(intent);
		} else if (title.equals(getString(R.string.menu_edit_cat))) {
			Intent intent = new Intent();
			intent.setClass(this, LeaveCategoryEditActivity.class);
			intent.putExtra(getString(R.string.intent_catid), getIntent().getIntExtra(getString(R.string.intent_catid), 0));
			startActivity(intent);
		}
		return true;
	}
	
}
