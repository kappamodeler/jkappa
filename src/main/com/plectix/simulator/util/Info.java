package com.plectix.simulator.util;

public class Info {
	private String message;
	private int count;
	private double position;

	public String getMessage() {
		return message;
	}

	public int getCount() {
		return count;
	}

	public double getPosition() {
		return position;
	}

	public Info() {

	}

	public Info(String message, int count, double position) {
		this.count = count;
		this.message = message+" sec. CPU";
		this.position = position;
	}

}
