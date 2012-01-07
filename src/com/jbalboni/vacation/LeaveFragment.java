package com.jbalboni.vacation;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LeaveFragment extends Fragment
{
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
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.leave_fragment, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        
        vacationTracker = LeaveStateManager.createVacationTracker(prefs);
        
        TextView hoursAvailable = (TextView)getView().findViewById(R.id.hoursAvailable);
        hoursAvailable.setText(String.format("%s %.2f",getString(R.string.hours_avail),vacationTracker.calculateHours(asOfDate)));
        
        /*ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        //actionBar.setHomeAction(new IntentAction(this, VacationTrackerActivity.createIntent(this), R.drawable.ic_title_home_default));
        final Action settingsAction = new IntentAction(this, new Intent(this, SettingsActivity.class), R.drawable.ic_action_settings);
        actionBar.addAction(settingsAction);
        */
        asOfDatePicker = (Button) getActivity().findViewById(R.id.changeAsOfDate);
        asOfDatePicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().showDialog(DATE_DIALOG_ID);
            }
        });
        
        useHoursPicker = (Button) getActivity().findViewById(R.id.useHours);
        useHoursPicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().showDialog(HOURS_DIALOG_ID);
            }
        });
    }
    
    public void addHoursUsed(float hoursUsed) {
        vacationTracker.addHoursUsed(hoursUsed);
    }
    
    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        vacationTracker 
            = LeaveStateManager.createVacationTracker(PreferenceManager.getDefaultSharedPreferences(getActivity()));
        
        updateDisplay();
    }
      
    public void updateDisplay()
    {
        TextView hoursAvailable = (TextView)getView().findViewById(R.id.hoursAvailable);
        hoursAvailable.setText(String.format("%.2f",vacationTracker.calculateHours(asOfDate)));
        TextView asOfDateTextView = (TextView) getView().findViewById(R.id.asOfDateDesc);
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
