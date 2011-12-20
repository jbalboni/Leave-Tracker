package com.jbalboni.vacation;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class VacationTrackerActivity extends Activity {
    private VacationTracker vacationTracker;
    private LocalDate asOfDate = new LocalDate();
    
    private Button asOfDatePicker;
    static final int DATE_DIALOG_ID = 0;
    DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        setContentView(R.layout.main);
        
        vacationTracker = VacationStateManager.createVacationTracker(prefs,this);
        
        TextView daysAvailable = (TextView)findViewById(R.id.hoursAvailable);
        daysAvailable.setText(String.format("%s %.2f",getString(R.string.hours_avail),vacationTracker.calculateHours(asOfDate)));
        
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        //actionBar.setHomeAction(new IntentAction(this, VacationTrackerActivity.createIntent(this), R.drawable.ic_title_home_default));
        actionBar.addAction(new ToastAction());
        final Action settingsAction = new IntentAction(this, new Intent(this, SettingsActivity.class), R.drawable.ic_action_settings);
        actionBar.addAction(settingsAction);
        
        asOfDatePicker = (Button) findViewById(R.id.changeAsOfDate);

        // add a click listener to the button
        asOfDatePicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                    mDateSetListener, asOfDate.getYear(), asOfDate.getMonthOfYear()-1, asOfDate.getDayOfMonth());
        }
        return null;
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                asOfDate = new LocalDate(year,monthOfYear+1,dayOfMonth);
                updateDisplay();
            }
        };
    
    @Override
    protected void onResume() {
        super.onResume();
        
        vacationTracker = VacationStateManager.createVacationTracker(PreferenceManager.getDefaultSharedPreferences(this),this);
        
        updateDisplay();
    }

    private void updateDisplay()
    {
        TextView daysAvailable = (TextView)findViewById(R.id.hoursAvailable);
        daysAvailable.setText(String.format("%.2f",vacationTracker.calculateHours(asOfDate)));
        TextView asOfDateTextView = (TextView) findViewById(R.id.asOfDateDesc);
        if (asOfDate.compareTo(new LocalDate()) != 0) {
            asOfDateTextView.setText(fmt.print(asOfDate));
        }
        else {
            asOfDateTextView.setText(getString(R.string.default_as_of_date));
        }
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