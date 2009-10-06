package com.plectix.simulator.xmlmap;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.util.GenerateXMLByModel;


public class InitTestInfluenceMap extends GenerateXMLByModel{

	public InitTestInfluenceMap() {
		super();
	}

	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		return SimulatorTestOptions.defaultContactMapOptions(defaultPathModelFileName(), true);
	}

	@Override
	public String getComparePath() {
		return defaultPathXMLFileName();
	}
}
