package com.plectix.simulator.perturbations;

import org.junit.Before;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.Test;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.staticanalysis.Rule;

public abstract class TestPerturbation extends DirectoryTestsRunner implements
		Test {
	private String myTestFileName = "";
	private Simulator mySimulator;

	protected TestPerturbation(String fileName) {
		super();
		myTestFileName = fileName;
	}

	@Before
	public void setup() {
		String fullTestFilePath = getPrefixFileName() + myTestFileName;
		Initializator initializator = getInitializator();

		initializator.init(fullTestFilePath);
		mySimulator = initializator.getSimulator();
		try {
			mySimulator.getSimulationData().setSnapshotTime("0");
			mySimulator.run(0);
		} catch (Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
		init();
	}

	@Override
	public abstract String getPrefixFileName();

	public abstract void init();

	public Rule getRuleByName(String name) {
		for (Rule rule : mySimulator.getSimulationData().getKappaSystem()
				.getRules()) {
			if (name.equals(rule.getName())) {
				return rule;
			}
		}
		return null;
	}
}
