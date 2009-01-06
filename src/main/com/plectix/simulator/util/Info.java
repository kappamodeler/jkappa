package com.plectix.simulator.util;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Formatter;

public class Info implements Serializable{
	private final static String INFO = "INFO";
	private final static String INTERNAL = "INTERNAL";
	public final static byte TYPE_INFO = 0;
	public final static byte TYPE_INTERNAL = 1;
	public final static byte TYPE_WARNING = 2;
	private final static String WARNING = "WARNING";

	private int count;
	private boolean isHaveTime = false;
	private String message;
	private double position;
	private double time;
	private String type;

	public Info(byte type, String message, double time, int count) {
		setType(type);
		this.count = count;
		isHaveTime = true;
		this.time = time;
		this.message = message;// + " sec. CPU";
		this.position = System.currentTimeMillis() / 1000.0;
	}

	public Info(byte type, String message, PrintStream printStream) {
		setType(type);
		this.count = 1;
		this.message = message;
		this.position = System.currentTimeMillis() / 1000.0;
		switch (type) {
		case TYPE_INFO:
			if (printStream != null) {
				printStream.println(message);
			}
			break;
		}
	}

	public final String getCount() {
		return Integer.toString(count); 
	}

	public final String getMessageWithTime() {
		if (isHaveTime)
			return message + time + " sec. CPU";
		else
			return message;
	}

	public final String getMessageWithoutTime() {
		return message;
	}

	public final String getPosition() {
		Formatter fr = new Formatter();
		fr.format("%.3f", position);
		return fr.toString();
	}

	public final double getTime() {
		return time;
	}

	public final String getType() {
		return type;
	}

	private final void setType(byte type) {
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

	public final void upCount(double time) {
		this.time += time;
		count++;
	}

}
