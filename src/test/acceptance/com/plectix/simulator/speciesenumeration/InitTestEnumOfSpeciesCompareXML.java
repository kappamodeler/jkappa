package com.plectix.simulator.speciesenumeration;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.simulator.options.SimulatorFlagOption;
import com.plectix.simulator.util.GenerateXMLByModel;

public class InitTestEnumOfSpeciesCompareXML extends GenerateXMLByModel {

	public InitTestEnumOfSpeciesCompareXML() {
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
		options.append(SimulatorFlagOption.ENUMERATE_COMPLEXES);
		options.append(SimulatorFlagOption.SHORT_CONSOLE_OUTPUT);
		options.append(SimulatorFlagOption.NO_BUILD_INFLUENCE_MAP);
		options.appendOperationMode(getOperationMode());
		return options;
	}
}
