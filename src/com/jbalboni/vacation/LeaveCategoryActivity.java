package com.jbalboni.vacation;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LeaveCategoryActivity extends SherlockFragmentActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.leave_category);
		getSupportActionBar().setTitle(R.string.cat_title);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(getString(R.string.menu_add)).setIcon(R.drawable.ic_menu_add)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		String title = item.getTitle().toString();
		return true;
	}
}
