package com.plectix.simulator.util;

public enum InequalitySign {
	LESS("<"),
	GREATER(">");
	
	private String string;
	
	private InequalitySign(String string) {
		this.string = string;
	}
	
	public boolean satisfy(double a, double b) {
		if (this == GREATER) {
			return a > b;
		} else {
			return a < b;
		}
	}
	
	@Override
	public final String toString() {
		return this.string;
	}
}
