package com.jbalboni.vacation;

import org.joda.time.LocalDate;

/*
 * Used in the leave history list in VacationTracker and LeaveStateManager
 */
public class LeaveItem {
	private LocalDate date;
	private float hours;
	private LeaveRecType addOrUse;
	
	public LeaveItem(LocalDate date, float hours, LeaveRecType leaveRecType) {
		this.date = date;
		this.hours = hours;
		this.addOrUse = leaveRecType;
	}

	public float getHours() {
		return hours;
	}

	public LocalDate getDate() {
		return date;
	}

	public LeaveRecType getAddOrUse() {
		return addOrUse;
	}

}
