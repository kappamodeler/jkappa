package com.plectix.simulator.gui.panel;

import java.io.File;

class SimulationSettings {
	private static final String DATA_DIRECTORY = "data" + File.separator;
	
	private String kappaFileName = null;
	private boolean isTime = true;	
	private long event;
	private double timeLength = 0;
	
	public SimulationSettings(String kappaFilename, String mode, String limit) {
		this.kappaFileName = kappaFilename;
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
			return kappaFileName + " (time= " + timeLength + ")";  
		} else {
			return kappaFileName + " (event= " + event + ")";  
		}
	}
	
	public final String getCommandLineOptions() {
		if (isTime) {
			return "--sim " + DATA_DIRECTORY + kappaFileName + " --time " + timeLength;  
		} else {
			return "--sim " + DATA_DIRECTORY + kappaFileName + " --event " + event;  
		}
	}

	public final String getKappaFilename() {
		return kappaFileName;
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
