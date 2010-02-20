package com.plectix.simulator.rulecompression;

import java.util.List;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.options.SimulatorFlagOption;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.util.BasicTestByModel;

public class InitTestRuleCompressions extends BasicTestByModel {
	
	public InitTestRuleCompressions() {
		super();
	}

	@Override
	public SimulatorTestOptions prepareTestModelArgs() {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.appendContactMap(defaultModelFileName());
		options.append(SimulatorFlagOption.NO_COMPUTE_LOCAL_VIEWS);
		options.append(SimulatorFlagOption.SHORT_CONSOLE_OUTPUT);
		options.append(SimulatorFlagOption.NO_BUILD_INFLUENCE_MAP);
		options.appendOperationMode(getOperationMode());
		return options;

	}
	
	public KappaSystem getKappaSystem() {
		return getSimulator().getSimulationData().getKappaSystem();
	}

	public AllSubViewsOfAllAgentsInterface getSubViews() {
		return getSimulator().getSimulationData().getKappaSystem().getSubViews();
	}

	public List<Rule> getRules() {
		return getSimulator().getSimulationData().getKappaSystem().getRules();
	}
}
