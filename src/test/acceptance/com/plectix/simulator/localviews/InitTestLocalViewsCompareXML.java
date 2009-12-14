package com.plectix.simulator.localviews;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.simulator.SimulatorOption;
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
		options.append(SimulatorOption.COMPUTE_LOCAL_VIEWS);
		options.append(SimulatorOption.SHORT_CONSOLE_OUTPUT);
		options.append(SimulatorOption.NO_BUILD_INFLUENCE_MAP);
		options.append(SimulatorOption.ALLOW_INCOMPLETE_SUBSTANCE);
		return options;
	}
}
