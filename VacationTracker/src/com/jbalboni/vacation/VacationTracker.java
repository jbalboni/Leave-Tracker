package com.jbalboni.vacation;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;

public class VacationTracker {
	private static final int WEEKS_IN_YEAR = 52;
	private static final int MONTHS_IN_YEAR = 12;
	private static final int DAYS_IN_WEEK = 7;
	private static final int DAYS_IN_YEAR = 365;

	private LocalDate startDate;
	private float hoursUsed;
	private float hoursPerYear;
	private float initialHours;
	private String leaveInterval;
	private boolean accrualOn;

	// private float maxLeave;
	// private float carryoverLimit;
	// private boolean useMaxLeave;
	// private boolean useCarryoverLimit;

	public VacationTracker() {
	}

	public VacationTracker(LocalDate startDate, float hoursUsed, float hoursPerYear, float initialHours,
			String leaveInterval, boolean accrualOn) {
		this.startDate = startDate;
		this.hoursUsed = hoursUsed;
		this.hoursPerYear = hoursPerYear;
		this.initialHours = initialHours;
		this.leaveInterval = leaveInterval;
		this.accrualOn = accrualOn;
	}

	public float calculateHours(LocalDate asOfDate) {
		int interval = 0;
		float vacationHours = 0;
		if (this.accrualOn == true) {
			// LocalDate endOfYear =
			// startDate.withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(31);
			// int yearEnds = 0;
			// while (endOfYear.isBefore(asOfDate)) {
			// yearEnds++;
			// endOfYear.plusYears(1);
			// }
			if (leaveInterval.equals("Daily")) {
				Days days = Days.daysBetween(startDate, asOfDate);
				interval = days.getDays();
				vacationHours = (interval * (hoursPerYear / DAYS_IN_YEAR) - hoursUsed);
			} else if (leaveInterval.equals("Weekly")) {
				Weeks weeks = Weeks.weeksBetween(startDate, asOfDate);
				interval = weeks.getWeeks();
				vacationHours = (interval * (hoursPerYear / WEEKS_IN_YEAR) - hoursUsed);
			} else if (leaveInterval.equals("Bi-Weekly")) {
				Weeks weeks = Weeks.weeksBetween(startDate, asOfDate);
				interval = weeks.getWeeks() / 2;
				vacationHours = (interval * (hoursPerYear / (WEEKS_IN_YEAR / 2)) - hoursUsed);
			} else if (leaveInterval.equals("Monthly")) {
				Months months = Months.monthsBetween(startDate, asOfDate);
				interval = months.getMonths();
				vacationHours = (interval * (hoursPerYear / MONTHS_IN_YEAR) - hoursUsed);
			}
		} else {
			vacationHours = hoursPerYear - hoursUsed;
		}
		return vacationHours + initialHours;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setHoursUsed(float hoursUsed) {
		this.hoursUsed = hoursUsed;
	}

	public void addHoursUsed(float hoursUsed) {
		this.hoursUsed += hoursUsed;
	}

	public float getHoursUsed() {
		return hoursUsed;
	}

	public float getHoursPerYear() {
		return hoursPerYear;
	}

	public void setHoursPerYear(float hoursPerYear) {
		this.hoursPerYear = hoursPerYear;
	}

	public float getInitialHours() {
		return initialHours;
	}

	public void setInitialHours(float initialHours) {
		this.initialHours = initialHours;
	}

	public boolean isAccrualOn() {
		return accrualOn;
	}

}
