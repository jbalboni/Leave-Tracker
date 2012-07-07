package com.jbalboni.vacation;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
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
		menu.add(getString(R.string.menu_add)).setIcon(R.drawable.ic_menu_add)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Edit Category").setIcon(R.drawable.ic_menu_edit)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("Delete Category").setIcon(R.drawable.ic_menu_delete)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		String title = item.getTitle().toString();
		return true;
	}
	
}
