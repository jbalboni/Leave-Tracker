package com.jbalboni.vacation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity
{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        getPreferenceScreen().findPreference("catLeft_settings").setTitle(prefs.getString("catLeft_title", getString(R.string.default_left_name))+" category");
        getPreferenceScreen().findPreference("catCenter_settings").setTitle(prefs.getString("catCenter_title", getString(R.string.default_center_name))+" category");
        getPreferenceScreen().findPreference("catRight_settings").setTitle(prefs.getString("catRight_title", getString(R.string.default_right_name))+" category");
        setTitleByAccrual(getPreferenceScreen(),findPreference("catLeft_accrualOn"));
        setTitleByAccrual(getPreferenceScreen(),findPreference("catCenter_accrualOn"));
        setTitleByAccrual(getPreferenceScreen(),findPreference("catRight_accrualOn"));
    }
    @Override
    public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().endsWith("accrualOn")) {
            setTitleByAccrual(preferenceScreen, preference);
        }
        return super.onPreferenceTreeClick (preferenceScreen, preference);
    }
    public void setTitleByAccrual(PreferenceScreen preferenceScreen, Preference preference) {
        CheckBoxPreference accrualOn = (CheckBoxPreference)preference;
        String prefix = preference.getKey().substring(0,preference.getKey().indexOf('_')+1);
        EditTextPreference hoursPerYear = (EditTextPreference)preferenceScreen.findPreference(prefix+"hoursPerYear");
        if (accrualOn.isChecked()) {
            hoursPerYear.setTitle(getString(R.string.leave_pref_perYear));
            hoursPerYear.setDialogTitle(getString(R.string.leave_pref_perYear));
        } else {
            hoursPerYear.setTitle(getString(R.string.leave_pref_available));
            hoursPerYear.setDialogTitle(getString(R.string.leave_pref_available));
        }
    }
}
