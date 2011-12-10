package com.jbalboni.vacation;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class VacationStateManager
{
    private static final DateTimeFormatter fmt = ISODateTimeFormat.localDateParser();
    public static VacationTracker createVacationTracker(SharedPreferences prefs, VacationTrackerActivity trackerActivity)
    {
        String startDateStr = prefs.getString("startdate", "2011-05-02");
        LocalDate startDate = fmt.parseLocalDate(startDateStr);
        Float hoursUsed = Float.parseFloat(prefs.getString("hoursUsed", "0"));
        int daysPerYear = Integer.parseInt(prefs.getString("daysPerYear", "10"));
        
        return new VacationTracker(startDate,hoursUsed,daysPerYear);
    }
    public static void saveVacationTracker(VacationTracker vacationTracker, SharedPreferences prefs)
    {
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString("hoursUsed", Float.toString(vacationTracker.getHoursUsed()));
        prefsEditor.putString("daysPerYear", Integer.toString(vacationTracker.getDaysPerYear()));
        prefsEditor.putString("startDate", String.format("%4d-%2d-%2d",vacationTracker.getStartDate().getYear(),vacationTracker.getStartDate().getMonthOfYear()
                ,vacationTracker.getStartDate().getDayOfMonth()));
        prefsEditor.commit();
    }
}
