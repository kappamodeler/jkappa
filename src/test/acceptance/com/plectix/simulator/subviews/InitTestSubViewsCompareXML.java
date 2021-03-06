package com.plectix.simulator.subviews;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.simulator.options.SimulatorFlagOption;
import com.plectix.simulator.util.GenerateXMLByModel;

public class InitTestSubViewsCompareXML extends GenerateXMLByModel {

	public InitTestSubViewsCompareXML() {
		super();
	}

	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.appendContactMap(defaultPathModelFileName());
		options.append(SimulatorFlagOption.COMPUTE_SUB_VIEWS);
		options.append(SimulatorFlagOption.SHORT_CONSOLE_OUTPUT);
		options.append(SimulatorFlagOption.NO_BUILD_INFLUENCE_MAP);
		return options;
	}
	
	@Override
	public String getComparePath() {
		return defaultPathXMLFileName();
	}
}
