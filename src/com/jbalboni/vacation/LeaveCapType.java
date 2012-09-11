package com.jbalboni.vacation;

public enum LeaveCapType {
	NONE(0), MAX(1), CARRYOVER(2);
	private int capType;
	private LeaveCapType(int capType) {
		 this.capType = capType;
	}
	
	public int getVal() {
		return capType; 
	}

	public static LeaveCapType getLeaveCapType(int capType) {
		if (capType == 0)
			return LeaveCapType.NONE;
		else if (capType == 1)
			return LeaveCapType.MAX;
		else if (capType == 2)
			return LeaveCapType.CARRYOVER;
		return null;
	}
}
