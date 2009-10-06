package com.plectix.simulator.localviews;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.component.complex.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulatorOption;
import com.plectix.simulator.util.BasicTestByModel;


public class InitTestLocalViews extends BasicTestByModel {
	
	public InitTestLocalViews() {
		super();
	}
	
	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.appendContactMap(defaultModelFileName());
		options.append(SimulatorOption.COMPUTE_LOCAL_VIEWS);
		options.append(SimulatorOption.SHORT_CONSOLE_OUTPUT);
		options.append(SimulatorOption.NO_BUILD_INFLUENCE_MAP);
		return options;
	}
	
	public KappaSystem getKappaSystem() {
		return getSimulator().getSimulationData().getKappaSystem();
	}

	public AllSubViewsOfAllAgentsInterface getSubViews() {
		return getSimulator().getSimulationData().getKappaSystem().getSubViews();
	}

	public String getSourcePath() {
		return defaultSourseFileName(InitData.pathForSource);
	}

}
