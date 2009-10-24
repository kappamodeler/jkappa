package com.plectix.simulator.xmlmap.rulecompression;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.simulator.SimulatorOption;
import com.plectix.simulator.util.GenerateXMLByModel;

public class InitTestRuleCompression  extends GenerateXMLByModel {
	
	public InitTestRuleCompression() {
		super();
	}

	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.appendContactMap(defaultPathModelFileName());
		options.appendQuantitativeCompression("");
		options.append(SimulatorOption.NO_COMPUTE_LOCAL_VIEWS);
		options.append(SimulatorOption.SHORT_CONSOLE_OUTPUT);
		options.append(SimulatorOption.NO_BUILD_INFLUENCE_MAP);
		return options;
	}
	
	@Override
	public String getComparePath() {
		return defaultPathXMLFileName();
	}
}
