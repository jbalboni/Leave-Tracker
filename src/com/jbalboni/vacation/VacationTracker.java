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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accrualOn ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(hoursPerYear);
		result = prime * result + Float.floatToIntBits(hoursUsed);
		result = prime * result + Float.floatToIntBits(initialHours);
		result = prime * result + ((leaveInterval == null) ? 0 : leaveInterval.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	public String getLeaveInterval() {
		return leaveInterval;
	}

	public void setLeaveInterval(String leaveInterval) {
		this.leaveInterval = leaveInterval;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VacationTracker))
			return false;
		VacationTracker other = (VacationTracker) obj;
		if (accrualOn != other.accrualOn)
			return false;
		if (Float.floatToIntBits(hoursPerYear) != Float.floatToIntBits(other.hoursPerYear))
			return false;
		if (Float.floatToIntBits(hoursUsed) != Float.floatToIntBits(other.hoursUsed))
			return false;
		if (Float.floatToIntBits(initialHours) != Float.floatToIntBits(other.initialHours))
			return false;
		if (leaveInterval == null) {
			if (other.leaveInterval != null)
				return false;
		} else if (!leaveInterval.equals(other.leaveInterval))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}
