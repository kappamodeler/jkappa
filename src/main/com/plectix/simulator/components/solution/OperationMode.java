package com.plectix.simulator.components.solution;

public enum OperationMode {
	FIRST("1"),
	SECOND("2"),
	THIRD("3"),
	FOURTH("4"), DEFAULT("DEFAULT");
	
	private final String string;
	
	private OperationMode(String string) {
		this.string = string;
	}
	
	public static OperationMode getValue(String string) {
		for (OperationMode mode : values()) {
			if (mode.string.equals(string)) {
				return mode;
			}
		}
		return FIRST;
	}
}
