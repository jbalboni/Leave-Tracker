package com.jbalboni.vacation.ui;

import org.joda.time.LocalDate;

import com.jbalboni.vacation.VacationTracker;

/*
 * Dumb container class for the async update
 */
public class LeaveTrackerUpdate {
	protected VacationTracker tracker;
	protected LocalDate asOfDate;
	protected float hours;
}
