package com.plectix.simulator.util;

public class Info {
	private final static String INFO = "INFO";
	public final static byte TYPE_INFO = 0;
	private final static String INTERNAL = "INTERNAL";
	public final static byte TYPE_INTERNAL = 1;
	private final static String WARNING = "WARNING";
	public final static byte TYPE_WARNING = 2;

	private String type;
	private String message;
	private int count;
	private double position;

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public String getCount() {
		return Integer.valueOf(count).toString();
	}

	public String getPosition() {
		return Double.valueOf(position).toString();
	}

	public Info() {

	}

	public Info(byte type, String message, int count, double position) {
		switch (type) {
		case TYPE_INFO:
			this.type = INFO;
			break;
		case TYPE_INTERNAL:
			this.type = INTERNAL;
			break;
		case TYPE_WARNING:
			this.type = WARNING;
			break;
		}

		this.count = count;
		this.message = message + " sec. CPU";
		this.position = position;
	}

}
