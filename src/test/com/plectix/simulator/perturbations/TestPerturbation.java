package com.plectix.simulator.perturbations;

import java.util.List;

import org.junit.Before;

import com.plectix.simulator.*;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.simulator.Model;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorManager;

public abstract class TestPerturbation extends DirectoryTestsRunner implements Test {
	private String myTestFileName = "";
	private double currentTime = 0.;
	private Model myModel;
	private Simulator mySimulator;
	private SimulatorManager myManager;
	
	protected TestPerturbation (String fileName) {
		super();
		myTestFileName = fileName;
	}
	
	@Before
	public void setup() {
		currentTime = 0.;
		String fullTestFilePath = getPrefixFileName() + myTestFileName;
		Initializator initializator = getInitializator();
		
		initializator.init(fullTestFilePath);
		mySimulator = initializator.getSimulator();
		myModel = initializator.getModel();
		myManager = initializator.getManager();
		mySimulator.run(null);
		init();
	}
	
	public abstract String getPrefixFileName();
	public abstract void init();
	
	public CRule getRuleByName(String name) {
		for (CRule rule : myManager.getRules()) {
			if (name.equals(rule.getName())) {
				return rule;
			}
		}
		return null;
	}
}
