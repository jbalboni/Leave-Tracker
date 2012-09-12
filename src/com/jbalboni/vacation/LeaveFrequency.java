package com.jbalboni.vacation;

public enum LeaveFrequency {
	DAILY("Daily"), WEEKLY("Weekly"), BIWEEKLY("Bi-Weekly"), MONTHLY("Monthly"), TWICEMONTHLY("Twice Monthly"), YEARLY("Yearly");
	private String name;
	private LeaveFrequency(String name) {
		 this.name = name;
	}
	
	@Override
	public String toString() {
		return name; 
	}

	public static LeaveFrequency getLeaveFrequency(String name) {
		if (name.equals("Daily")) {
			return LeaveFrequency.DAILY;
		} else if (name.equals("Weekly")) {
			return LeaveFrequency.WEEKLY;
		} else if (name.equals("Bi-Weekly")) {
			return LeaveFrequency.BIWEEKLY;
		} else if (name.equals("Monthly")) {
			return LeaveFrequency.MONTHLY;
		} else if (name.equals("Twice Monthly")) {
			return LeaveFrequency.TWICEMONTHLY;
		} else if (name.equals("Yearly")) {
			return LeaveFrequency.YEARLY;
		}
		return LeaveFrequency.WEEKLY;
	}
}
