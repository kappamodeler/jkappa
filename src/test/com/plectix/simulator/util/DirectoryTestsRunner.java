package com.plectix.simulator.util;

import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.*;

import org.junit.*;

import static org.junit.Assert.*;

import com.plectix.simulator.*;
import com.plectix.simulator.components.*;
import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
import com.plectix.simulator.simulator.Model;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorManager;

@RunWith(Parameterized.class)
public abstract class DirectoryTestsRunner {

	private static Model myModel;
	private static Simulator mySimulator;
	private static SimulatorManager myManager;
	private final Logger LOGGER = Logger.getLogger(Simulator.class);
	private double currentTime = 0.;
	private CRule myActiveRule;
	private static int myRunQuant = 0;

	private String myTestFileName = "";
	private List<ObservablesConnectedComponent> myObsComponents;

	private List<CInjection> myCurrentInjectionsList;

	public static Collection<Object[]> getAllTestFileNames(String prefix) {
		LinkedList<Object[]> parameters = new LinkedList<Object[]>();
		try {
			File testFolder = new File(prefix);
			if (testFolder.isDirectory()) {
				for (String fileName : testFolder.list()) {
					if (fileName.startsWith("test")) {
						parameters.add(new Object[] { fileName });
					}
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return Collections.unmodifiableList(parameters);
	}

	protected DirectoryTestsRunner(String fileName) {
		myTestFileName = fileName;
	}

	public abstract void init();
	public abstract String getPrefixFileName();
	public abstract boolean isDoingPositive();
	

	@Before
	public void setup() {
		String fullTestFilePath = getPrefixFileName() + myTestFileName;

		if (myRunQuant == 0) {
			RunUpdateTests.init(fullTestFilePath);
			mySimulator = RunUpdateTests.getSimulator();
			myModel = RunUpdateTests.getModel();
			myManager = RunUpdateTests.getManager();
		} else {
			RunUpdateTests.reset(fullTestFilePath);
		}
		myRunQuant++;
		run();
		myObsComponents = myManager.getSimulationData().getObservables()
				.getConnectedComponentList();

		init();
	}

	public List<ObservablesConnectedComponent> getObservables() {
		return myObsComponents;
	}

	public CRule getActiveRule() {
		return myActiveRule;
	}

	public List<CRule> getRules() {
		return myManager.getRules();
	}

	public List<CInjection> getCurrentInjectionsList() {
		return myCurrentInjectionsList;
	}

	private void run() {
		SimulationMain.getSimulationManager().startTimer();
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				myModel.getSimulationData().getRules(), myModel
						.getSimulationData().getSeed());

		myModel.getSimulationData().getObservables().calculateObs(currentTime);
		myActiveRule = ruleProbabilityCalculation.getRandomRule();

		if (myActiveRule == null) {
			myModel.getSimulationData().setTimeLength(currentTime);
			fail(myTestFileName + " : there's no active rules");
		}

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Rule: " + myActiveRule.getName());

		myCurrentInjectionsList = ruleProbabilityCalculation
				.getSomeInjectionList(myActiveRule);
		currentTime += ruleProbabilityCalculation.getTimeValue();

		if (!RunUpdateTests.isClash(myCurrentInjectionsList)) {
			// negative update
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("negative update");

			myActiveRule.applyRule(myCurrentInjectionsList);
			mySimulator.doNegativeUpdate(myCurrentInjectionsList);
			if (isDoingPositive()) {
				mySimulator.doPositiveUpdate(myActiveRule);
			}

		} else {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Clash");
		}
	}
}
