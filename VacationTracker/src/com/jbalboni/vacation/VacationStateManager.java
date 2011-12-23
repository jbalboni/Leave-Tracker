package com.jbalboni.vacation;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class VacationStateManager
{
    private static final DateTimeFormatter fmt = ISODateTimeFormat.localDateParser();
    public static VacationTracker createVacationTracker(SharedPreferences prefs)
    {
        String startDateStr = prefs.getString("startdate", "2011-05-02");
        LocalDate startDate = fmt.parseLocalDate(startDateStr);
        Float hoursUsed = Float.parseFloat(prefs.getString("hoursUsed", "0"));
        float hoursPerYear = Float.parseFloat(prefs.getString("hoursPerYear", "80"));
        float initialHours = Float.parseFloat(prefs.getString("initialHours", "0"));
        
        return new VacationTracker(startDate,hoursUsed,hoursPerYear,initialHours);
    }
    public static void saveVacationTracker(VacationTracker vacationTracker, SharedPreferences prefs)
    {
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString("hoursUsed", Float.toString(vacationTracker.getHoursUsed()));
        prefsEditor.putString("hoursPerYear", Float.toString(vacationTracker.getHoursPerYear()));
        prefsEditor.putString("intialHours", Float.toString(vacationTracker.getInitialHours()));
        prefsEditor.putString("startDate", String.format("%4d-%2d-%2d",vacationTracker.getStartDate().getYear(),vacationTracker.getStartDate().getMonthOfYear()
                ,vacationTracker.getStartDate().getDayOfMonth()));
        prefsEditor.commit();
    }
}
