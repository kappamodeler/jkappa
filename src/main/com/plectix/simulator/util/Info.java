package com.plectix.simulator.util;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Formatter;

@SuppressWarnings("serial")
public final class Info implements Serializable{
	public enum InfoType {
		INFO,
		INTERNAL,
		WARNING,
		OUTPUT,
		DO_NOT_OUTPUT;
	}
	
	private int count;
	private final boolean haveTime;
	private final String message;
	private final double position;
	private double time;
	private InfoType type;

	public Info(InfoType outputType, InfoType type, String message, double time, int count) {
		this.type = type;
		this.count = count;
		this.haveTime = true;
		this.time = time;
		this.message = message;// + " sec. CPU";
		this.position = System.currentTimeMillis() / 1000.0;
	}

	public Info(InfoType outputType, InfoType type, String message, PrintStream printStream) {
		this.type = type;
		this.count = 1;
		this.haveTime = false;
		this.message = message;
		this.position = System.currentTimeMillis() / 1000.0;
		switch (outputType) {
		case OUTPUT:
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
		if (haveTime)
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

	public final InfoType getType() {
		return type;
	}

	public final void upCount(double time) {
		this.time += time;
		count++;
	}

}
