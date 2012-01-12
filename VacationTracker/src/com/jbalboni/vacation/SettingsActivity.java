package com.jbalboni.vacation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity
{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        getPreferenceScreen().findPreference("catLeft_settings").setTitle(prefs.getString("catLeft_title", getString(R.string.default_left_name))+" category");
        getPreferenceScreen().findPreference("catCenter_settings").setTitle(prefs.getString("catCenter_title", getString(R.string.default_center_name))+" category");
        getPreferenceScreen().findPreference("catRight_settings").setTitle(prefs.getString("catRight_title", getString(R.string.default_right_name))+" category");
    }
}
