package com.plectix.simulator.localviews;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.simulator.options.SimulatorFlagOption;
import com.plectix.simulator.util.GenerateXMLByModel;

public class InitTestLocalViewsCompareXML extends GenerateXMLByModel {

	public InitTestLocalViewsCompareXML() {
		super();
	}

	@Override
	public String getComparePath() {
		return defaultPathXMLFileName();
	}

	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.appendContactMap(defaultPathModelFileName());
		options.append(SimulatorFlagOption.COMPUTE_LOCAL_VIEWS);
		options.append(SimulatorFlagOption.SHORT_CONSOLE_OUTPUT);
		options.append(SimulatorFlagOption.NO_BUILD_INFLUENCE_MAP);
		return options;
	}
}
