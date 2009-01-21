package com.plectix.simulator.updates;

import static org.junit.Assert.fail;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.Info.InfoType;

public abstract class TestUpdate extends DirectoryTestsRunner {

	private Simulator mySimulator;
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
		run();
		init();
	}

	public IRule getActiveRule() {
		return myActiveRule;
	}

	public List<IRule> getRules() {
		return mySimulator.getSimulationData().getRules();
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
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(InfoType.OUTPUT,mySimulator.getSimulationData());

		mySimulator.getSimulationData().getObservables().calculateObs(currentTime,
				1, mySimulator.getSimulationData().getSimulationArguments().isTime());
		myActiveRule = ruleProbabilityCalculation.getRandomRule();

		if (myActiveRule == null) {
			mySimulator.getSimulationData().setTimeLength(currentTime);
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

			myActiveRule.applyRule(myCurrentInjectionsList, mySimulator.getSimulationData());
			SimulationUtils.doNegativeUpdate(myCurrentInjectionsList);
			if (isDoingPositive()) {
				mySimulator.getSimulationData().doPositiveUpdate(myActiveRule,
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
