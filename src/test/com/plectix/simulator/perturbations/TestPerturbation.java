package com.plectix.simulator.perturbations;


import org.junit.Before;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.Test;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.simulator.Simulator;

public abstract class TestPerturbation extends DirectoryTestsRunner implements Test {
	private String myTestFileName = "";
	private Simulator mySimulator;
	
	protected TestPerturbation (String fileName) {
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
		} catch(Exception e) {
			junit.framework.Assert.fail(e.getMessage());
		}
		init();
	}
	
	@Override
	public abstract String getPrefixFileName();
	public abstract void init();
	
	public IRule getRuleByName(String name) {
		for (IRule rule : mySimulator.getSimulationData().getRules()) {
			if (name.equals(rule.getName())) {
				return rule;
			}
		}
		return null;
	}
}
