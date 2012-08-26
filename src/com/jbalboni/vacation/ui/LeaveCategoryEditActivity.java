package com.jbalboni.vacation.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jbalboni.vacation.R;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CheckBox;

public class LeaveCategoryEditActivity extends SherlockFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.leave_category_edit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.menu_save).setIcon(R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		int catID = getIntent().getIntExtra(getString(R.string.intent_catid), 0);
		if (catID > 3) {
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
			LeaveCategoryEditFragment fragment = (LeaveCategoryEditFragment) fm
					.findFragmentById(R.id.leaveCategoryEditFragment);
			fragment.saveCategory();
		} else if (title.equals(getString(R.string.menu_delete))) {
			FragmentManager fm = getSupportFragmentManager();
			LeaveCategoryEditFragment fragment = (LeaveCategoryEditFragment) fm
					.findFragmentById(R.id.leaveCategoryEditFragment);
			fragment.deleteCategory();
		}
		return true;
	}

	public void onAccrualChecked(View view) {
		boolean checked = ((CheckBox) view).isChecked();
		FragmentManager fm = getSupportFragmentManager();
		LeaveCategoryEditFragment fragment = (LeaveCategoryEditFragment) fm
				.findFragmentById(R.id.leaveCategoryEditFragment);
		fragment.toggleAccrual(checked);
	}
}
