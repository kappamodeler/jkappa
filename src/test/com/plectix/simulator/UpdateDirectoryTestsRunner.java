package com.plectix.simulator;

import java.util.*;

import org.apache.log4j.Logger;

import org.junit.*;

import static org.junit.Assert.*;

import com.plectix.simulator.util.*;
import com.plectix.simulator.components.*;
import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
import com.plectix.simulator.simulator.Model;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorManager;

public abstract class UpdateDirectoryTestsRunner extends DirectoryTestsRunner {

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

	protected UpdateDirectoryTestsRunner(String fileName) {
		super();
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
