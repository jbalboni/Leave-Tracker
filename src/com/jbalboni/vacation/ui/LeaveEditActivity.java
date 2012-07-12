package com.jbalboni.vacation.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.R.drawable;
import com.jbalboni.vacation.R.id;
import com.jbalboni.vacation.R.layout;
import com.jbalboni.vacation.R.string;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class LeaveEditActivity extends SherlockFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.leave_edit_activity);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.menu_save).setIcon(R.drawable.ic_menu_save)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(R.string.menu_delete).setIcon(R.drawable.ic_menu_delete)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		String title = item.getTitle().toString();
		if (title.equals(getString(R.string.menu_save))) {
			FragmentManager fm = getSupportFragmentManager();
			LeaveEditFragment fragment = (LeaveEditFragment) fm.findFragmentById(R.id.leaveEditFragment);
			fragment.saveLeaveItem();
		}
		return true;
	}
}
