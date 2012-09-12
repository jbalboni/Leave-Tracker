package com.jbalboni.vacation;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;


/*
 * Main logic class. Does the leave calculation based on used hours, accrued leave, and limit
 */
public class VacationTracker {
	private static final int WEEKS_IN_YEAR = 52;
	private static final int MONTHS_IN_YEAR = 12;
	private static final int DAYS_IN_YEAR = 365;

	private LocalDate startDate;
	private List<LeaveItem> history;
	private float hoursPerYear;
	private float initialHours;
	private String leaveInterval;
	private boolean accrualOn;
	private LeaveCapType leaveCapType;
	private float leaveCap;

	public VacationTracker() {
	}

	public VacationTracker(LocalDate startDate, List<LeaveItem> history, float hoursPerYear, float initialHours,
			String leaveInterval, boolean accrualOn, LeaveCapType leaveCapType, float leaveCap) {
		this.startDate = startDate;
		this.history = history;
		this.hoursPerYear = hoursPerYear;
		this.initialHours = initialHours;
		this.leaveInterval = leaveInterval;
		this.accrualOn = accrualOn;
		this.leaveCap = leaveCap;
		this.leaveCapType = leaveCapType;
	}

	public float calculateHours(LocalDate asOfDate) {
		int historyIndex = 0;
		// remove before and after leave items
		List<LeaveItem> trimmedHist = trimLeave(asOfDate);

		float vacationHours = 0;
		vacationHours = initialHours;

		if (this.accrualOn == true) {
			LocalDate currentDate = addInterval(startDate);
			LocalDate previousDate = startDate;
			float hoursPerPeriod = getIntervalHours();
			//Go period by period and add accrued leave
			while (currentDate.compareTo(asOfDate) <= 0) {
				if (previousDate.getYear() < currentDate.getYear() && leaveCapType == LeaveCapType.CARRYOVER && vacationHours > leaveCap) {
					vacationHours = leaveCap;
				} 
				vacationHours += hoursPerPeriod;
				SumAndPos currentUsed = sumLeave(trimmedHist, historyIndex, currentDate);
				historyIndex = currentUsed.pos;
				vacationHours -= currentUsed.sum;
				previousDate = currentDate;
				currentDate = addInterval(currentDate);
			}
			//Check to see if a year was passed after period but before as of date
			if (previousDate.getYear() < asOfDate.getYear() && leaveCapType == LeaveCapType.CARRYOVER && vacationHours > leaveCap) {
				vacationHours = leaveCap;
			} 
		} else {
			vacationHours += hoursPerYear;
		}
		
		//this will catch the used leave on the as of date
		SumAndPos currentUsed = sumLeave(trimmedHist, historyIndex, asOfDate);
		historyIndex = currentUsed.pos;
		vacationHours -= currentUsed.sum;
		
		if (leaveCapType == LeaveCapType.MAX && vacationHours > leaveCap)
			return leaveCap;
		else
			return vacationHours;
	}
	
	private LocalDate addInterval(LocalDate date) {
		if (leaveInterval.equals("Yearly")) {
			return date.plusYears(1);
		} else if (leaveInterval.equals("Monthly")) {
			return date.plusMonths(1);
		} else if (leaveInterval.equals("Weekly")) {
			return date.plusWeeks(1);
		} else if (leaveInterval.equals("Bi-Weekly")) {
			return date.plusWeeks(2);
		} else if (leaveInterval.equals("Daily")) {
			return date.plusDays(1);
		} else if (leaveInterval.equals("Twice Monthly")) {
			if (date.getDayOfMonth() == 1) {
				return date.withDayOfMonth(15);
			} else {
				return date.withDayOfMonth(1).plusMonths(1);
			}
		} 
		return date;
	}
	
	private float getIntervalHours() {
		if (leaveInterval.equals("Yearly")) {
			return hoursPerYear;
		} else if (leaveInterval.equals("Monthly")) {
			return hoursPerYear / MONTHS_IN_YEAR;
		} else if (leaveInterval.equals("Weekly")) {
			return hoursPerYear / WEEKS_IN_YEAR;
		} else if (leaveInterval.equals("Bi-Weekly")) {
			return hoursPerYear / (WEEKS_IN_YEAR / 2);
		} else if (leaveInterval.equals("Daily")) {
			return hoursPerYear / DAYS_IN_YEAR;
		} else if (leaveInterval.equals("Twice Monthly")) {
			return hoursPerYear / (MONTHS_IN_YEAR * 2);
		}
		return 0;
	}

	/*
	 * Stupid Java
	 */
	private static class SumAndPos {
		public int pos;
		public float sum;

		public SumAndPos(int pos, float sum) {
			this.pos = pos;
			this.sum = sum;
		}
	}

	private SumAndPos sumLeave(List<LeaveItem> list, int start, LocalDate endDate) {
		float leaveSum = 0;
		int i = start;
		while (i < list.size() && list.get(i).getDate().compareTo(endDate) <= 0) {
			if (list.get(i).getAddOrUse() == LeaveRecType.ADD) {
				leaveSum -= list.get(i).getHours();
			} else {
				leaveSum += list.get(i).getHours();
			}
			
			i++;
		}
		return new SumAndPos(i, leaveSum);
	}

	/*
	 * We don't need to count leave outside of the start date and as of date.
	 */
	private List<LeaveItem> trimLeave(LocalDate asOfDate) {
		List<LeaveItem> hist = new ArrayList<LeaveItem>();
		for (LeaveItem item : history) {
			if (!(item.getDate().isBefore(startDate) || item.getDate().isAfter(asOfDate))) {
				hist.add(item);
			}
		}
		return hist;
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

	public String getLeaveInterval() {
		return leaveInterval;
	}

	public void setLeaveInterval(String leaveInterval) {
		this.leaveInterval = leaveInterval;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accrualOn ? 1231 : 1237);
		result = prime * result + ((history == null) ? 0 : history.hashCode());
		result = prime * result + Float.floatToIntBits(hoursPerYear);
		result = prime * result + Float.floatToIntBits(initialHours);
		result = prime * result + Float.floatToIntBits(leaveCap);
		result = prime * result + ((leaveCapType == null) ? 0 : leaveCapType.hashCode());
		result = prime * result + ((leaveInterval == null) ? 0 : leaveInterval.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
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
		if (history == null) {
			if (other.history != null)
				return false;
		} else if (!history.equals(other.history))
			return false;
		if (Float.floatToIntBits(hoursPerYear) != Float.floatToIntBits(other.hoursPerYear))
			return false;
		if (Float.floatToIntBits(initialHours) != Float.floatToIntBits(other.initialHours))
			return false;
		if (Float.floatToIntBits(leaveCap) != Float.floatToIntBits(other.leaveCap))
			return false;
		if (leaveCapType != other.leaveCapType)
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

	public void addHoursUsed(Float hoursUsed) {
		// TODO Auto-generated method stub

	}

}
