package com.jbalboni.vacation;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;

public class VacationTracker
{
    private static final int WEEKS_IN_YEAR = 52;
    private static final int MONTHS_IN_YEAR = 12;
    private static final int DAYS_IN_WEEK = 7;
    
    private LocalDate startDate;
    private float hoursUsed;
    private float hoursPerYear;
    private float initialHours;
    private String leaveInterval;
    
    public VacationTracker() {
    }
    public VacationTracker(LocalDate startDate, float hoursUsed, float hoursPerYear, float initialHours, String leaveInterval) {
        this.startDate = startDate;
        this.hoursUsed = hoursUsed;
        this.hoursPerYear = hoursPerYear;
        this.initialHours = initialHours;
        this.leaveInterval = leaveInterval;
     }
    public float calculateHours(LocalDate asOfDate) {
        int interval = 0;
        float vacationHours = 0;
        if (leaveInterval.equals("Day")) {
            Days days = Days.daysBetween(startDate, asOfDate);
            interval = days.getDays();
            vacationHours = (interval * ((hoursPerYear / WEEKS_IN_YEAR) / DAYS_IN_WEEK) - hoursUsed);
        } else if (leaveInterval.equals("Week")) {
            Weeks weeks = Weeks.weeksBetween(startDate, asOfDate);
            interval = weeks.getWeeks();
            vacationHours = (interval * (hoursPerYear / WEEKS_IN_YEAR) - hoursUsed);
        } else if (leaveInterval.equals("Month")) {
            Months months = Months.monthsBetween(startDate, asOfDate);
            interval = months.getMonths();
            vacationHours = (interval * (hoursPerYear / MONTHS_IN_YEAR) - hoursUsed);
        }
        return vacationHours;
    }
    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
    }
    public LocalDate getStartDate()
    {
        return startDate;
    }
    public void setHoursUsed(float hoursUsed)
    {
        this.hoursUsed = hoursUsed;
    }
    public void addHoursUsed(float hoursUsed)
    {
        this.hoursUsed += hoursUsed;
    }
    public float getHoursUsed()
    {
        return hoursUsed;
    }
    public float getHoursPerYear()
    {
        return hoursPerYear;
    }
    public void setHoursPerYear(float hoursPerYear)
    {
        this.hoursPerYear = hoursPerYear;
    }
    public float getInitialHours()
    {
        return initialHours;
    }
    public void setInitialHours(float initialHours)
    {
        this.initialHours = initialHours;
    }
    
}
