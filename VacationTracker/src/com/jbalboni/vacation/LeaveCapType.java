package com.jbalboni.vacation;

public enum LeaveCapType {
	NONE("None"),
	MAX("Max"),
	CARRYOVER("Carryover");
	private String capType;
	private LeaveCapType(String capType) {
		this.capType = capType;
	}
	@Override
	public String toString() {
		return capType;
	}
	public static LeaveCapType getLeaveCapType(String capType) {
		if (capType.equals("None"))
			return LeaveCapType.NONE;
		else if (capType.equals("Max"))
			return LeaveCapType.MAX;
		else if (capType.equals("Carryover"))
			return LeaveCapType.CARRYOVER;
		return null;
	}
}
