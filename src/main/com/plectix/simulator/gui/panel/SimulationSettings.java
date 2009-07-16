package com.plectix.simulator.gui.panel;

import java.io.File;

public class SimulationSettings {
	public static final String DATA_DIRECTORY = "data" + File.separator;
	
	private String kappaFilename = null;
	private boolean isTime = true;	
	private long event;
	private double timeLength = 0;
	
	public SimulationSettings(String kappaFilename, String mode, String limit) {
		this.kappaFilename = kappaFilename;
		if (mode.equalsIgnoreCase("time")) {
			isTime = true;
			timeLength = Double.parseDouble(limit);
		} else if (mode.equalsIgnoreCase("event")) {
			isTime = false;
			event = Long.parseLong(limit);
		} else {
			throw new RuntimeException("Unknown mode: " + mode);
		}
	}

	@Override
	public final String toString() {
		if (isTime) {
			return kappaFilename + " (time= " + timeLength + ")";  
		} else {
			return kappaFilename + " (event= " + event + ")";  
		}
	}
	
	public final String getCommandLineOptions() {
		if (isTime) {
			return "--sim " + DATA_DIRECTORY + kappaFilename + " --time " + timeLength;  
		} else {
			return "--sim " + DATA_DIRECTORY + kappaFilename + " --event " + event;  
		}
	}

	public final String getKappaFilename() {
		return kappaFilename;
	}

	public final boolean isTime() {
		return isTime;
	}

	public final long getEvent() {
		return event;
	}

	public final double getTimeLength() {
		return timeLength;
	}
}
