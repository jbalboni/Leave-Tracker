package com.jbalboni.vacation.test;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import android.test.AndroidTestCase;

import com.jbalboni.vacation.LeaveCapType;
import com.jbalboni.vacation.LeaveFrequency;
import com.jbalboni.vacation.LeaveRecType;
import com.jbalboni.vacation.VacationTracker;
import com.jbalboni.vacation.LeaveItem;

public class VacationTrackerTest extends AndroidTestCase {
	public VacationTrackerTest() {
		super();
	}

	public void testInitial() {
		float initialHours = 6;
		VacationTracker tracker = new VacationTracker(new LocalDate(), new ArrayList<LeaveItem>(), 80.0f, initialHours,
				LeaveFrequency.DAILY, true, LeaveCapType.NONE, 0);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);
	}

	public void testAccrualOff() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 80;
		LeaveFrequency leaveInterval = LeaveFrequency.DAILY;
		boolean accrualOn = false;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusMonths(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear);
	}

	public void testAccrualOn() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 120;
		LeaveFrequency leaveInterval = LeaveFrequency.DAILY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusYears(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(Math.round(hoursPerYear), Math.round(tracker.calculateHours((new LocalDate()).minusDays(1))));
	}

	public void testHoursUsed() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		hoursUsed.add(new LeaveItem((new LocalDate()).minusDays(2), 17, LeaveRecType.USE));
		float hoursPerYear = 120;
		LeaveFrequency leaveInterval = LeaveFrequency.DAILY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusYears(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(Math.round(hoursPerYear - (hoursUsed.get(0)).getHours()),
				Math.round(tracker.calculateHours((new LocalDate()).minusDays(1))));
	}

	public void testWeekly() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 52;
		LeaveFrequency leaveInterval = LeaveFrequency.WEEKLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusWeeks(1).plusDays(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);

		tracker.setStartDate(startDate.minusDays(1));
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear / 52.0f);
	}

	public void testMonthly() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 12;
		LeaveFrequency leaveInterval = LeaveFrequency.MONTHLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusMonths(1).plusDays(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);

		tracker.setStartDate(startDate.minusDays(1));
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear / 12.0f);
	}

	public void testBiWeekly() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 26;
		LeaveFrequency leaveInterval = LeaveFrequency.BIWEEKLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusWeeks(2).plusDays(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);

		tracker.setStartDate(startDate.minusDays(1));
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear / 26.0f);
	}

	public void testMaxLeaveCap() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 120;
		LeaveFrequency leaveInterval = LeaveFrequency.DAILY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 100;
		LeaveCapType leaveCapType = LeaveCapType.MAX;

		startDate = startDate.minusYears(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(tracker.calculateHours(new LocalDate()), leaveCap);
	}

	public void testCarryoverInitial() {
		float initialHours = 60;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 120;
		LeaveFrequency leaveInterval = LeaveFrequency.MONTHLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate(2011, 12, 2);
		float leaveCap = 40;
		LeaveCapType leaveCapType = LeaveCapType.CARRYOVER;

		LocalDate endDate = new LocalDate(2012, 2, 2);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(60f, tracker.calculateHours(endDate));
	}

	public void testCarryover() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 120;
		LeaveFrequency leaveInterval = LeaveFrequency.MONTHLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate(2011, 11, 2);
		float leaveCap = 5;
		LeaveCapType leaveCapType = LeaveCapType.CARRYOVER;

		LocalDate endDate = new LocalDate(2012, 1, 2);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(15f, tracker.calculateHours(endDate));
	}

	public void testCarryoverDaily() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 365;
		LeaveFrequency leaveInterval = LeaveFrequency.DAILY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate(2011, 12, 2);
		float leaveCap = 5;
		LeaveCapType leaveCapType = LeaveCapType.CARRYOVER;

		LocalDate endDate = new LocalDate(2012, 2, 2);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(tracker.calculateHours(endDate), 38f);
	}

	public void testCarryoverWeekly() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 52;
		LeaveFrequency leaveInterval = LeaveFrequency.WEEKLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate(2011, 12, 2);
		float leaveCap = 4;
		LeaveCapType leaveCapType = LeaveCapType.CARRYOVER;

		LocalDate endDate = new LocalDate(2012, 2, 3);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(tracker.calculateHours(endDate), 9f);
	}

	public void testCarryoverBiWeekly() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 52;
		LeaveFrequency leaveInterval = LeaveFrequency.BIWEEKLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate(2011, 11, 2);
		float leaveCap = 2;
		LeaveCapType leaveCapType = LeaveCapType.CARRYOVER;

		LocalDate endDate = new LocalDate(2012, 1, 5);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(2f, tracker.calculateHours(endDate));
	}

	public void testCarryoverTwoYear() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 120;
		LeaveFrequency leaveInterval = LeaveFrequency.MONTHLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate(2010, 1, 2);
		float leaveCap = 15;
		LeaveCapType leaveCapType = LeaveCapType.CARRYOVER;

		LocalDate endDate = new LocalDate(2012, 3, 2);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(45f, tracker.calculateHours(endDate));
	}

	public void testCarryoverPostMonth() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		hoursUsed.add(new LeaveItem(new LocalDate(2012, 1, 8), 8, LeaveRecType.USE));
		float hoursPerYear = 120;
		LeaveFrequency leaveInterval = LeaveFrequency.MONTHLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate(2011, 1, 2);
		float leaveCap = 15;
		LeaveCapType leaveCapType = LeaveCapType.CARRYOVER;

		LocalDate endDate = new LocalDate(2012, 1, 21);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(17f, tracker.calculateHours(endDate));
	}

	public void testFutureLeave() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		hoursUsed.add(new LeaveItem((new LocalDate()).plusWeeks(3), 8, LeaveRecType.USE));
		float hoursPerYear = 26;
		LeaveFrequency leaveInterval = LeaveFrequency.BIWEEKLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusWeeks(2).plusDays(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);

		tracker.setStartDate(startDate.minusDays(1));
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear / 26.0f);
	}

	public void testPastLeave() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 26;
		LeaveFrequency leaveInterval = LeaveFrequency.BIWEEKLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusWeeks(2).plusDays(1);
		hoursUsed.add(new LeaveItem(startDate.minusWeeks(3), 8, LeaveRecType.USE));

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(tracker.calculateHours(new LocalDate()), initialHours);

		tracker.setStartDate(startDate.minusDays(1));
		assertEquals(tracker.calculateHours(new LocalDate()), hoursPerYear / 26.0f);
	}

	public void testYearly() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 12;
		LeaveFrequency leaveInterval = LeaveFrequency.YEARLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusYears(1).plusDays(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(initialHours, tracker.calculateHours(new LocalDate()));

		tracker.setStartDate(startDate.minusDays(1));
		assertEquals(hoursPerYear, tracker.calculateHours(new LocalDate()));
	}

	public void testAddedLeave() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		hoursUsed.add(new LeaveItem((new LocalDate()).minusWeeks(1), 8, LeaveRecType.ADD));
		float hoursPerYear = 12;
		LeaveFrequency leaveInterval = LeaveFrequency.MONTHLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.minusMonths(1).plusDays(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(initialHours + 8, tracker.calculateHours(new LocalDate()));

		tracker.setStartDate(startDate.minusDays(1));
		assertEquals((hoursPerYear / 12.0f) + 8f, tracker.calculateHours(new LocalDate()));
	}

	public void testTwiceMonthly() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		float hoursPerYear = 24;
		LeaveFrequency leaveInterval = LeaveFrequency.TWICEMONTHLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate();
		float leaveCap = 0;
		LeaveCapType leaveCapType = LeaveCapType.NONE;

		startDate = startDate.withDayOfMonth(1);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(initialHours, tracker.calculateHours(startDate.withDayOfMonth(14)));

		assertEquals(hoursPerYear / 24.0f, tracker.calculateHours(startDate.withDayOfMonth(15)));
	}
	
	public void testCarryoverTwoYearAddedLeave() {
		float initialHours = 0;
		List<LeaveItem> hoursUsed = new ArrayList<LeaveItem>();
		hoursUsed.add(new LeaveItem((new LocalDate(2010, 1, 2)).plusWeeks(1), 8, LeaveRecType.ADD));
		float hoursPerYear = 120;
		LeaveFrequency leaveInterval = LeaveFrequency.MONTHLY;
		boolean accrualOn = true;
		LocalDate startDate = new LocalDate(2010, 1, 2);
		float leaveCap = 15;
		LeaveCapType leaveCapType = LeaveCapType.CARRYOVER;

		LocalDate endDate = new LocalDate(2012, 3, 2);

		VacationTracker tracker = new VacationTracker(startDate, hoursUsed, hoursPerYear, initialHours, leaveInterval,
				accrualOn, leaveCapType, leaveCap);
		assertEquals(45f, tracker.calculateHours(endDate));
	}
}
