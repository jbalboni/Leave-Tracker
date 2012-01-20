package com.jbalboni.vacation.test;

import org.joda.time.LocalDate;

import android.test.AndroidTestCase;

import com.jbalboni.vacation.VacationTracker;

public class VacationTrackerTest extends AndroidTestCase {
	public VacationTrackerTest() {
		super();
	}

	public void testInitial() {
		float initialHours = 6;
		VacationTracker tracker = new VacationTracker(new LocalDate(), 0.0f, 80.0f, initialHours, "Daily", true);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);
	}

	public void testAccrualOff() {
		float initialHours = 0;
		float hoursUsed = 0;
		float hoursPerYear = 80;
		String leaveInterval = "Daily";
		boolean accrualOn = false;
		LocalDate startDate = new LocalDate();

		startDate = startDate.minusMonths(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn);
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear);
	}

	public void testAccrualOn() {
		float initialHours = 0;
		float hoursUsed = 0;
		float hoursPerYear = 120;
		String leaveInterval = "Daily";
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();

		startDate = startDate.minusYears(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn);
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear);
	}
	
	public void testHoursUsed() {
		float initialHours = 0;
		float hoursUsed = 17;
		float hoursPerYear = 120;
		String leaveInterval = "Daily";
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();

		startDate = startDate.minusYears(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn);
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear-hoursUsed);
	}
	public void testWeekly() {
		float initialHours = 0;
		float hoursUsed = 0;
		float hoursPerYear = 52;
		String leaveInterval = "Weekly";
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();

		startDate = startDate.minusWeeks(1).plusDays(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);
		
		tracker.setStartDate(startDate.minusDays(1));
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear/52.0f);
	}
	public void testMonthly() {
		float initialHours = 0;
		float hoursUsed = 0;
		float hoursPerYear = 12;
		String leaveInterval = "Monthly";
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();

		startDate = startDate.minusMonths(1).plusDays(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);
		
		tracker.setStartDate(startDate.minusDays(1));
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear/12.0f);
	}
	public void testBiWeekly() {
		float initialHours = 0;
		float hoursUsed = 0;
		float hoursPerYear = 26;
		String leaveInterval = "Bi-Weekly";
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();

		startDate = startDate.minusWeeks(2).plusDays(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);
		
		tracker.setStartDate(startDate.minusDays(1));
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear/26.0f);
	}
}
