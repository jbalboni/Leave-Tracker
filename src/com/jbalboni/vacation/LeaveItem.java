package com.jbalboni.vacation;

import org.joda.time.LocalDate;

public class LeaveItem {
	private LocalDate date;
	private float hours;
	public LeaveItem(LocalDate date, float hours) {
		this.date = date;
		this.hours = hours;
	}
	public float getHours() {
		return hours;
	}
	public LocalDate getDate() {
		return date;
	}
}
