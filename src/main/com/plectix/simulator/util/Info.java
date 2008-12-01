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
	private double time;
	private int count;
	private double position;
	private boolean isHaveTime = false;

	private static int index = 0;

	public String getType() {
		return type;
	}

	public String getMessage() {
		if (isHaveTime)
			return message + time + " sec. CPU";
		else
			return message;
	}

	public final String getMessageWithoutTime() {
		return message;
	}

	public final void upCount(double time) {
		this.time += time;
		count++;
	}

	public String getCount() {
		return Integer.valueOf(count).toString();
	}

	public String getPosition() {
		return Double.valueOf(position).toString();
	}

	public Info() {

	}

	public Info(byte type, String message) {
		setType(type);
		this.count = 1;
		this.message = message;
		this.position = index++;
		System.out.println(message);
	}

	public final double getTime() {
		return time;
	}

	private void setType(byte type) {
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
	}

	public Info(byte type, String message, double time, int count) {
		setType(type);
		this.count = count;
		isHaveTime = true;
		this.time = time;
		this.message = message;// + " sec. CPU";
		this.position = index++;
	}

}
