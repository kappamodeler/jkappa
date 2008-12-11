package com.plectix.simulator.util;

import java.io.Serializable;
import java.text.Format;
import java.util.Formatter;

import com.plectix.simulator.simulator.Simulator;

public class Info implements Serializable{
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
		Formatter fr = new Formatter();
		fr.format("%.3f", position);
		return fr.toString();
	}

	public Info() {

	}

	public Info(byte type, String message) {
		setType(type);
		this.count = 1;
		this.message = message;
		this.position = System.currentTimeMillis() / 1000.0;
		switch (type) {
		case TYPE_INFO:
			Simulator.println(message);
			break;
		}
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
		this.position = System.currentTimeMillis() / 1000.0;
	}

}
