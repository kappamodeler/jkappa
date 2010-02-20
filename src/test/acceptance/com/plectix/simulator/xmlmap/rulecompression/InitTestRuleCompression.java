package com.plectix.simulator.xmlmap.rulecompression;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.simulator.options.SimulatorFlagOption;
import com.plectix.simulator.util.GenerateXMLByModel;

public class InitTestRuleCompression  extends GenerateXMLByModel {
	
	public InitTestRuleCompression() {
		super();
	}

	//TODO COPYPASTE DETECTED, see InitTestSubViewsCompareXML
	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.appendContactMap(defaultPathModelFileName());
		options.appendQuantitativeCompression("");
		options.append(SimulatorFlagOption.NO_COMPUTE_LOCAL_VIEWS);
		options.append(SimulatorFlagOption.SHORT_CONSOLE_OUTPUT);
		options.append(SimulatorFlagOption.NO_BUILD_INFLUENCE_MAP);
		return options;
	}
	
	@Override
	public String getComparePath() {
		return defaultPathXMLFileName();
	}
}
