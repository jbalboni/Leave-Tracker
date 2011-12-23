package com.jbalboni.vacation;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class VacationTracker
{
    private static final int WEEKS_IN_YEAR = 52;
    private static final int DAYS_IN_WEEK = 7;
    
    private LocalDate startDate;
    private float hoursUsed;
    private float hoursPerYear;
    private float initialHours;
    
    public VacationTracker() {
    }
    public VacationTracker(LocalDate startDate, float hoursUsed, float hoursPerYear, float initialHours) {
        this.startDate = startDate;
        this.hoursUsed = hoursUsed;
        this.hoursPerYear = hoursPerYear;
        this.initialHours = initialHours;
     }
    public float calculateHours(LocalDate asOfDate) {
        Days days = Days.daysBetween(startDate, asOfDate);
        float vacationHours = (days.getDays() * ((hoursPerYear / WEEKS_IN_YEAR) / DAYS_IN_WEEK) - hoursUsed);
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
