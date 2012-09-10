package com.jbalboni.vacation.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jbalboni.vacation.R;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class LeaveEditActivity extends SherlockFragmentActivity implements ActionBar.TabListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.leave_edit);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab useTab = getSupportActionBar().newTab();
        useTab.setText("Use Hours");
        useTab.setTabListener(this);
        getSupportActionBar().addTab(useTab);
        ActionBar.Tab addTab = getSupportActionBar().newTab();
        addTab.setText("Add Hours");
        addTab.setTabListener(this);
        getSupportActionBar().addTab(addTab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.menu_save).setIcon(R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		int itemID = getIntent().getIntExtra(getString(R.string.intent_itemid), 0);
		if (itemID != 0) {
			menu.add(R.string.menu_delete).setIcon(R.drawable.ic_menu_delete)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
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
		} else if (title.equals(getString(R.string.menu_delete))) {
			FragmentManager fm = getSupportFragmentManager();
			LeaveEditFragment fragment = (LeaveEditFragment) fm.findFragmentById(R.id.leaveEditFragment);
			fragment.deleteLeaveItem();
		}
		return true;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		FragmentManager fm = getSupportFragmentManager();
		LeaveEditFragment fragment = (LeaveEditFragment) fm.findFragmentById(R.id.leaveEditFragment);
		fragment.changeAddOrUse(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
