package com.plectix.simulator.speciesenumeration;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.simulator.SimulatorOption;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.util.BasicTestByModel;


public class InitTestEnumOfSpecies extends BasicTestByModel {

	public InitTestEnumOfSpecies() {
		super();
	}

	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.appendContactMap(defaultModelFileName());
		options.append(SimulatorOption.ENUMERATE_COMPLEXES);
		options.append(SimulatorOption.SHORT_CONSOLE_OUTPUT);
		options.append(SimulatorOption.NO_BUILD_INFLUENCE_MAP);
		options.appendOperationMode(getOperationMode());
		return options;
	}
	
	public AllSubViewsOfAllAgentsInterface getSubViews() {
		return getSimulator().getSimulationData().getKappaSystem().getSubViews();
	}
}
