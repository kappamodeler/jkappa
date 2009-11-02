package com.plectix.simulator.xmlmap;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.util.GenerateXMLByModel;

public class InitTestContactMap extends GenerateXMLByModel {

	private static final String FOLDER_RULES = "rules";

	public InitTestContactMap() {
		super();
	}

	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		
		SimulatorTestOptions options = SimulatorTestOptions.defaultContactMapOptions(defaultPathModelFileName(), getOperationMode());
		
		if (getDirectoryInside().contains(FOLDER_RULES)) {
			options.appendFocus(fileName(DEFAULT_PREFIX_FOCUS_FILE, getMyCountInside(), DEFAULT_EXTENSION_FILE));
		}
		
		return options;
	}
	
	@Override
	public String getComparePath() {
		return defaultPathXMLFileName();

	}


}
