package com.jbalboni.vacation.ui;

import com.jbalboni.vacation.LeaveCapType;
import com.jbalboni.vacation.LeaveCategory;
import com.jbalboni.vacation.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		getPreferenceScreen().findPreference("catLeft_settings").setTitle(
				prefs.getString("catLeft_title", getString(R.string.default_left_name)) + " category");
		getPreferenceScreen().findPreference("catCenter_settings").setTitle(
				prefs.getString("catCenter_title", getString(R.string.default_center_name)) + " category");
		getPreferenceScreen().findPreference("catRight_settings").setTitle(
				prefs.getString("catRight_title", getString(R.string.default_right_name)) + " category");
		setTitleByAccrual(getPreferenceScreen(), findPreference("catLeft_accrualOn"));
		setTitleByAccrual(getPreferenceScreen(), findPreference("catCenter_accrualOn"));
		setTitleByAccrual(getPreferenceScreen(), findPreference("catRight_accrualOn"));
		
		ListPreference leaveCapType = (ListPreference)findPreference(LeaveCategory.LEFT.getPrefix()+"leaveCapType");
		leaveCapType.setOnPreferenceChangeListener(this);
		leaveCapType = (ListPreference)findPreference(LeaveCategory.CENTER.getPrefix()+"leaveCapType");
		leaveCapType.setOnPreferenceChangeListener(this);
		leaveCapType = (ListPreference)findPreference(LeaveCategory.RIGHT.getPrefix()+"leaveCapType");
		leaveCapType.setOnPreferenceChangeListener(this);
		
		setAccrualLimitText(getPreferenceScreen(),LeaveCategory.LEFT.getPrefix());
		setAccrualLimitText(getPreferenceScreen(),LeaveCategory.CENTER.getPrefix());
		setAccrualLimitText(getPreferenceScreen(),LeaveCategory.RIGHT.getPrefix());
	}

	private void setAccrualLimitText(PreferenceScreen preferenceScreen, String prefix) {
		ListPreference leaveCapType = (ListPreference)findPreference(prefix+"leaveCapType");
		EditTextPreference leaveCapVal = (EditTextPreference)findPreference(prefix+"leaveCapVal");
		setAccrualLimit(leaveCapVal, leaveCapType.getValue());
		
	}
	
	public void setAccrualLimit(EditTextPreference leaveCapVal, String leaveCapType ) {
		if (leaveCapType.equals(LeaveCapType.CARRYOVER.toString())) {
			leaveCapVal.setEnabled(true);
			leaveCapVal.setTitle(getString(R.string.leave_pref_cap_val_carry));
			leaveCapVal.setDialogTitle(getString(R.string.leave_pref_cap_val_carry));
			leaveCapVal.setSummary(getString(R.string.leave_pref_cap_val_summary_carry));
		} else if (leaveCapType.equals(LeaveCapType.MAX.toString())) {
			leaveCapVal.setEnabled(true);
			leaveCapVal.setTitle(getString(R.string.leave_pref_cap_val_max));
			leaveCapVal.setDialogTitle(getString(R.string.leave_pref_cap_val_max));
			leaveCapVal.setSummary(getString(R.string.leave_pref_cap_val_summary_max));
		} else {
			leaveCapVal.setEnabled(false);
			leaveCapVal.setSummary("");
			leaveCapVal.setTitle("Leave Accrual Limit");
		}
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference.getKey().endsWith("accrualOn")) {
			setTitleByAccrual(preferenceScreen, preference);
		} 
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public void setTitleByAccrual(PreferenceScreen preferenceScreen, Preference preference) {
		CheckBoxPreference accrualOn = (CheckBoxPreference) preference;
		String prefix = preference.getKey().substring(0, preference.getKey().indexOf('_') + 1);
		EditTextPreference hoursPerYear = (EditTextPreference) preferenceScreen.findPreference(prefix + "hoursPerYear");
		if (accrualOn.isChecked()) {
			hoursPerYear.setTitle(getString(R.string.leave_pref_perYear));
			hoursPerYear.setDialogTitle(getString(R.string.leave_pref_perYear));
		} else {
			hoursPerYear.setTitle(getString(R.string.leave_pref_available));
			hoursPerYear.setDialogTitle(getString(R.string.leave_pref_available));
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		EditTextPreference leaveCapVal = (EditTextPreference)findPreference(preference.getKey().substring(0, preference.getKey().indexOf('_') + 1)+"leaveCapVal");
		setAccrualLimit(leaveCapVal, newValue.toString());
		return true;
	}
}
