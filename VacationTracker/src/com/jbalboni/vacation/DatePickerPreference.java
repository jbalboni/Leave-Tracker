package com.jbalboni.vacation;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerPreference extends DialogPreference implements  
OnDateChangedListener, OnDateSetListener {

    @Override
    protected View onCreateDialogView() {
        DatePicker picker = new DatePicker(getContext());
        strDate = getPersistedString(null);
        LocalDate startDate = strDate == null ? new LocalDate() : fmt.parseLocalDate(strDate);
        
        picker.setCalendarViewShown(false);
        picker.init(startDate.getYear(),startDate.getMonthOfYear()-1,startDate.getDayOfMonth(), this);
        return picker;
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,  
            int dayOfMonth) {
        strDate = String.format("%4d-%02d-%02d",year,monthOfYear+1,dayOfMonth);
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear,  
            int dayOfMonth) {
        onDateChanged(view, year, monthOfYear, dayOfMonth);
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        super.setDefaultValue(null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if(positiveResult) {
            if(isPersistent()) {
                persistString(strDate);
            }
            callChangeListener(strDate);
        }
    }

    public DatePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DatePickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() { setPersistent(true); }

    private String strDate;
    private static final DateTimeFormatter fmt = ISODateTimeFormat.localDateParser();
}
