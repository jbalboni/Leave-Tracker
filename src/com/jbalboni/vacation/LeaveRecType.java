package com.jbalboni.vacation;

public enum LeaveRecType {
	USE(0), ADD(1);
	private int recVal;
	private LeaveRecType(int recVal) {
		 this.recVal = recVal;
	}
	
	public int getVal() {
		return recVal; 
	}

	public static LeaveRecType getLeaveRecType(int recType) {
		if (recType == 0)
			return LeaveRecType.USE;
		else if (recType == 1)
			return LeaveRecType.ADD;
		return null;
	}
}

