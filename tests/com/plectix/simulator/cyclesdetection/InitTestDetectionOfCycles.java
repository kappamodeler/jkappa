package com.plectix.simulator.cyclesdetection;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.util.BasicTestByModel;

public class InitTestDetectionOfCycles extends BasicTestByModel {
	
	public InitTestDetectionOfCycles() {
		super();
	}

	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		return SimulatorTestOptions.defaultContactMapOptions(defaultModelFileName());
	}

	public AllSubViewsOfAllAgentsInterface getSubViews() {
		return getSimulator().getSimulationData().getKappaSystem().getSubViews();
	}

	public ContactMap getContactMap() {
		return getSimulator().getSimulationData().getKappaSystem().getContactMap();
	}

}
