package com.plectix.simulator.updates;

import java.util.*;

import org.apache.log4j.Logger;

import org.junit.*;

import static org.junit.Assert.*;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.Model;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorManager;

public abstract class TestUpdate extends DirectoryTestsRunner {

	private Model myModel;
	private Simulator mySimulator;
	private SimulatorManager myManager;
	private final Logger LOGGER = Logger.getLogger(Simulator.class);
	private double currentTime = 0.;
	private IRule myActiveRule;

	private String myTestFileName = "";

	private List<IInjection> myCurrentInjectionsList;

	protected TestUpdate(String fileName) {
		super();
		myTestFileName = fileName;
	}

	public abstract void init();

	@Override
	public abstract String getPrefixFileName();

	public abstract boolean isDoingPositive();

	@Before
	public void setup() {
		String fullTestFilePath = getPrefixFileName() + myTestFileName;
		Initializator initializator = getInitializator();

		initializator.init(fullTestFilePath);
		mySimulator = initializator.getSimulator();
		myModel = initializator.getModel();
		myManager = initializator.getManager();
		run();
		init();
	}

	public IRule getActiveRule() {
		return myActiveRule;
	}

	public List<IRule> getRules() {
		return myManager.getRules();
	}

	public List<IInjection> getCurrentInjectionsList() {
		return myCurrentInjectionsList;
	}

	private boolean isClash(List<IInjection> injections) {
		if (injections.size() == 2) {
			for (ISite siteCC1 : injections.get(0).getSiteList())
				for (ISite siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getAgentLink().getId() == siteCC2
							.getAgentLink().getId())
						return true;
		}
		return false;
	}

	private void run() {
		SimulationMain.getSimulationManager().startTimer();
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				myModel.getSimulationData().getRules(), myModel
						.getSimulationData().getSeed());

		myModel.getSimulationData().getObservables().calculateObs(currentTime,
				1, myModel.getSimulationData().isTime());
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

		if (!isClash(myCurrentInjectionsList)) {
			// negative update
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("negative update");

			myActiveRule.applyRule(myCurrentInjectionsList);
			mySimulator.doNegativeUpdate(myCurrentInjectionsList);
			if (isDoingPositive()) {
				mySimulator.doPositiveUpdate(myActiveRule,
						myCurrentInjectionsList);
			}

		} else {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Clash");
		}
	}

	public static boolean lhsIsEmpty(List<IConnectedComponent> lh) {
		return (lh.size() == 1) && (lh.contains(CRule.EMPTY_LHS_CC));
	}
}
