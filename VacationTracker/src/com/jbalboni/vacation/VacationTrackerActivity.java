package com.jbalboni.vacation;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.text.InputType;

public class VacationTrackerActivity extends Activity {
    private VacationTracker vacationTracker;
    private LocalDate asOfDate = new LocalDate();
    
    private Button asOfDatePicker;
    private Button useHoursPicker;
    static final int DATE_DIALOG_ID = 0;
    static final int HOURS_DIALOG_ID = 1;
    static final String HOURS_IN_DAY = "8";
    DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
    
    private SharedPreferences prefs;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        setContentView(R.layout.main);
        
        vacationTracker = VacationStateManager.createVacationTracker(prefs,this);
        
        TextView daysAvailable = (TextView)findViewById(R.id.hoursAvailable);
        daysAvailable.setText(String.format("%s %.2f",getString(R.string.hours_avail),vacationTracker.calculateHours(asOfDate)));
        
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        //actionBar.setHomeAction(new IntentAction(this, VacationTrackerActivity.createIntent(this), R.drawable.ic_title_home_default));
        final Action settingsAction = new IntentAction(this, new Intent(this, SettingsActivity.class), R.drawable.ic_action_settings);
        actionBar.addAction(settingsAction);
        
        asOfDatePicker = (Button) findViewById(R.id.changeAsOfDate);
        asOfDatePicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        useHoursPicker = (Button) findViewById(R.id.useHours);
        useHoursPicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(HOURS_DIALOG_ID);
            }
        });
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                    mDateSetListener, asOfDate.getYear(), asOfDate.getMonthOfYear()-1, asOfDate.getDayOfMonth());
        case HOURS_DIALOG_ID:
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Use hours");
            alert.setMessage("Add to your used leave");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            input.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setText(HOURS_IN_DAY);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              Float hoursUsed = Float.parseFloat(input.getText().toString());
              vacationTracker.addHoursUsed(hoursUsed);
              updateDisplay();
              VacationStateManager.saveVacationTracker(vacationTracker, prefs);
              }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
              }
            });

            return alert.create();
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
}