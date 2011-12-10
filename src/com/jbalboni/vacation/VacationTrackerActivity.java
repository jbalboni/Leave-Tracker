package com.jbalboni.vacation;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class VacationTrackerActivity extends Activity {
    private VacationTracker vacationTracker;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        setContentView(R.layout.main);
        
        vacationTracker = VacationStateManager.createVacationTracker(prefs,this);
        
        TextView daysAvailable = (TextView)findViewById(R.id.daysAvailable);
        daysAvailable.setText(String.format("%s %.2f",getString(R.string.days_avail),vacationTracker.calculateHours()));
        
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        //actionBar.setHomeAction(new IntentAction(this, VacationTrackerActivity.createIntent(this), R.drawable.ic_title_home_default));
        actionBar.addAction(new ToastAction());
        final Action settingsAction = new IntentAction(this, new Intent(this, SettingsActivity.class), R.drawable.ic_action_settings);
        actionBar.addAction(settingsAction);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        vacationTracker = VacationStateManager.createVacationTracker(PreferenceManager.getDefaultSharedPreferences(this),this);
        
        TextView daysAvailable = (TextView)findViewById(R.id.daysAvailable);
        daysAvailable.setText(String.format("%.2f",vacationTracker.calculateHours()));
    }
    
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, VacationTrackerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    
    private class ToastAction implements Action
    {

        @Override
        public int getDrawable() {
            return R.drawable.ic_action_add;
        }

        @Override
        public void performAction(View view) {
            Toast.makeText(VacationTrackerActivity.this,
                    "Example action", Toast.LENGTH_SHORT).show();
        }

    }
    
}