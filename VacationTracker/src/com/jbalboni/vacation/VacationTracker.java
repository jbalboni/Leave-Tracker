package com.jbalboni.vacation;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class VacationTracker
{
    private static final int WEEKS_IN_YEAR = 52;
    private static final int HOURS_IN_DAY = 8;
    private static final int DAYS_IN_WEEK = 7;
    
    private LocalDate startDate;
    private float hoursUsed;
    private int daysPerYear;
    
    public VacationTracker() {
    }
    public VacationTracker(LocalDate startDate, float hoursUsed, int daysPerYear) {
        this.startDate = startDate;
        this.hoursUsed = hoursUsed;
        this.daysPerYear = daysPerYear;
     }
    public float calculateHours(LocalDate asOfDate) {
        Days days = Days.daysBetween(startDate, asOfDate);
        float hoursPerYear = daysPerYear * HOURS_IN_DAY;
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
    public void setDaysPerYear(int daysPerYear)
    {
        this.daysPerYear = daysPerYear;
    }
    public int getDaysPerYear()
    {
        return daysPerYear;
    }
    
}
