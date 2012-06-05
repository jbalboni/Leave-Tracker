package com.jbalboni.vacation;

public enum LeaveCategory {
	LEFT("catLeft_", "Sick Leave"), RIGHT("catRight_", "Comp Time"), CENTER("catCenter_", "Paid Leave");

	private String prefix;
	private String title;

	LeaveCategory(String prefix, String title) {
		this.prefix = prefix;
		this.setTitle(title);
	}

	public String getPrefix() {
		return prefix;
	};

	public static LeaveCategory getCategoryByPosition(int position) {
		if (position == 0) {
			return LeaveCategory.LEFT;
		} else if (position == 1) {
			return LeaveCategory.CENTER;
		} else if (position == 2)
			return LeaveCategory.RIGHT;
		else
			throw new IllegalArgumentException("Bad leave position value");
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
