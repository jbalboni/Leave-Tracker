package com.jbalboni.vacation;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;

public class VacationTracker {
	private static final int WEEKS_IN_YEAR = 52;
	private static final int MONTHS_IN_YEAR = 12;
	private static final int DAYS_IN_YEAR = 365;

	private LocalDate startDate;
	private float hoursUsed;
	private float hoursPerYear;
	private float initialHours;
	private String leaveInterval;
	private boolean accrualOn;
	private LeaveCapType leaveCapType;
	private float leaveCap;

	// private float maxLeave;
	// private float carryoverLimit;
	// private boolean useMaxLeave;
	// private boolean useCarryoverLimit;

	public VacationTracker() {
	}

	public VacationTracker(LocalDate startDate, float hoursUsed, float hoursPerYear, float initialHours,
			String leaveInterval, boolean accrualOn, LeaveCapType leaveCapType, float leaveCap) {
		this.startDate = startDate;
		this.hoursUsed = hoursUsed;
		this.hoursPerYear = hoursPerYear;
		this.initialHours = initialHours;
		this.leaveInterval = leaveInterval;
		this.accrualOn = accrualOn;
		this.leaveCap = leaveCap;
		this.leaveCapType = leaveCapType;
	}

	public float calculateHours(LocalDate asOfDate) {
		int interval = 0;
		float vacationHours = initialHours - hoursUsed;
		if (this.accrualOn == true) {
			 
			if (leaveInterval.equals("Daily")) {
				LocalDate currentDate = startDate;
				while (currentDate.isBefore(asOfDate)) {
					float tempVacationHours;
					LocalDate endOfYear =
						 currentDate.plusDays(1).withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(31);
					if (endOfYear.isBefore(asOfDate)) {
						Days days = Days.daysBetween(currentDate, endOfYear);
						interval = days.getDays();
						tempVacationHours = interval * (hoursPerYear / DAYS_IN_YEAR);
						if (leaveCapType == LeaveCapType.CARRYOVER && tempVacationHours + vacationHours > leaveCap) {
							vacationHours = leaveCap;
						}
						else {
							vacationHours += tempVacationHours;
						}
					} else {
						Days days = Days.daysBetween(currentDate, asOfDate);
						interval = days.getDays();
						vacationHours += interval * (hoursPerYear / DAYS_IN_YEAR);
					}
					currentDate = endOfYear;
				}
			} else if (leaveInterval.equals("Weekly")) {
				LocalDate currentDate = startDate;
				while (currentDate.isBefore(asOfDate)) {
					float tempVacationHours;
					LocalDate endOfYear =
						currentDate.plusDays(1).withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(31);
					LocalDate newEndOfYear = endOfYear.withDayOfWeek(startDate.getDayOfWeek());
					if (endOfYear.isAfter(newEndOfYear))
						endOfYear = newEndOfYear.plusWeeks(1);
					else
						endOfYear = newEndOfYear;
					if (endOfYear.isBefore(asOfDate)) {
						Weeks weeks = Weeks.weeksBetween(currentDate, endOfYear);
						interval = weeks.getWeeks();
						tempVacationHours = interval * (hoursPerYear / WEEKS_IN_YEAR);
						if (leaveCapType == LeaveCapType.CARRYOVER && tempVacationHours + vacationHours > leaveCap) {
							vacationHours = leaveCap;
						}
						else {
							vacationHours += tempVacationHours;
						}
					} else {
						Weeks weeks = Weeks.weeksBetween(currentDate, asOfDate);
						interval = weeks.getWeeks();
						vacationHours += interval * (hoursPerYear / WEEKS_IN_YEAR);
					}
					currentDate = endOfYear;
				}
			} else if (leaveInterval.equals("Bi-Weekly")) {
				LocalDate currentDate = startDate;
				while (currentDate.isBefore(asOfDate)) {
					float tempVacationHours;
					LocalDate endOfYear =
						currentDate.plusDays(1).withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(31);
					LocalDate newEndOfYear = endOfYear.withDayOfWeek(startDate.getDayOfWeek());
					if (endOfYear.isAfter(newEndOfYear))
						endOfYear = newEndOfYear.plusWeeks(1);
					else
						endOfYear = newEndOfYear;
					if (Weeks.weeksBetween(startDate, endOfYear).getWeeks() % 2 != 0)
						endOfYear = endOfYear.plusWeeks(1);
					if (endOfYear.isBefore(asOfDate)) {
						Weeks weeks = Weeks.weeksBetween(currentDate, endOfYear);
						interval = weeks.getWeeks();
						tempVacationHours = interval * (hoursPerYear / WEEKS_IN_YEAR);
						if (leaveCapType == LeaveCapType.CARRYOVER && tempVacationHours + vacationHours > leaveCap) {
							vacationHours = leaveCap;
						}
						else {
							vacationHours += tempVacationHours;
						}
					} else {
						Weeks weeks = Weeks.weeksBetween(currentDate, asOfDate);
						interval = weeks.getWeeks() / 2;
						vacationHours += interval * (hoursPerYear / (WEEKS_IN_YEAR / 2));
					}
					currentDate = endOfYear;
				}
			} else if (leaveInterval.equals("Monthly")) {
				LocalDate currentDate = startDate;
				while (currentDate.isBefore(asOfDate)) {
					float tempVacationHours;
					LocalDate endOfYear =
						currentDate.plusDays(1).withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(31);
					LocalDate newEndOfYear = endOfYear.withDayOfMonth(startDate.getDayOfMonth());
					if (endOfYear.isAfter(newEndOfYear))
						endOfYear = newEndOfYear.plusMonths(1);
					else
						endOfYear = newEndOfYear;
					if (endOfYear.isBefore(asOfDate)) {
						Months months = Months.monthsBetween(currentDate, endOfYear);
						interval = months.getMonths();
						tempVacationHours = interval * (hoursPerYear / MONTHS_IN_YEAR);
						if (leaveCapType == LeaveCapType.CARRYOVER && tempVacationHours + vacationHours > leaveCap) {
							vacationHours = leaveCap;
						}
						else {
							vacationHours += tempVacationHours;
						}
					} else {
						Months months = Months.monthsBetween(currentDate, asOfDate);
						interval = months.getMonths();
						vacationHours += interval * (hoursPerYear / MONTHS_IN_YEAR);
					}
					currentDate = endOfYear;
				}
			}
		} else {
			vacationHours += hoursPerYear;
		}
		if (leaveCapType == LeaveCapType.MAX && vacationHours + initialHours > leaveCap) 
			return leaveCap;
		else
			return vacationHours;
	}

	public LeaveCapType getLeaveCapType() {
		return leaveCapType;
	}

	public void setLeaveCapType(LeaveCapType leaveCapType) {
		this.leaveCapType = leaveCapType;
	}

	public float getLeaveCap() {
		return leaveCap;
	}

	public void setLeaveCap(float leaveCap) {
		this.leaveCap = leaveCap;
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
