package com.plectix.simulator.updates;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.junit.Before;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.probability.CProbabilityCalculation;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.PlxLogger;
import com.plectix.simulator.util.Info.InfoType;

public abstract class TestUpdate extends DirectoryTestsRunner {

	private Simulator mySimulator;
	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(Simulator.class);
	private double currentTime = 0.;
	private CRule myActiveRule;

	private String myTestFileName = "";

	private List<CInjection> myCurrentInjectionsList;

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

	public CRule getActiveRule() {
		return myActiveRule;
	}

	public Collection<CRule> getRules() {
		return mySimulator.getSimulationData().getKappaSystem().getRules();
	}

	public List<CInjection> getCurrentInjectionsList() {
		return myCurrentInjectionsList;
	}

	private boolean isClash(List<CInjection> injections) {
		if (injections.size() == 2) {
			for (CSite siteCC1 : injections.get(0).getSiteList())
				for (CSite siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getAgentLink().getId() == siteCC2
							.getAgentLink().getId())
						return true;
		}
		return false;
	}

	private void run() {
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(InfoType.OUTPUT,mySimulator.getSimulationData());

		mySimulator.getSimulationData().getKappaSystem().getObservables().calculateObs(currentTime,
				1, mySimulator.getSimulationData().getSimulationArguments().isTime());
		myActiveRule = mySimulator.getSimulationData().getKappaSystem().getRandomRule();

		if (myActiveRule == null) {
			mySimulator.getSimulationData().setTimeLength(currentTime);
			fail(myTestFileName + " : there's no active rules");
		}

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Rule: " + myActiveRule.getName());

//		myCurrentInjectionsList = ruleProbabilityCalculation
//				.getSomeInjectionList(myActiveRule);
		myCurrentInjectionsList = ruleProbabilityCalculation.chooseInjectionsForRuleApplication(myActiveRule);
		currentTime += mySimulator.getSimulationData().getKappaSystem().getTimeValue();

		if (myCurrentInjectionsList != null) {
			// negative update
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("negative update");

			myActiveRule.applyRule(myCurrentInjectionsList, mySimulator.getSimulationData());
			SimulationUtils.doNegativeUpdate(myCurrentInjectionsList);
			if (isDoingPositive()) {
				mySimulator.getSimulationData().getKappaSystem().doPositiveUpdate(myActiveRule,
						myCurrentInjectionsList);
			}

		} else {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Clash");
		}
	}

	public static boolean lhsIsEmpty(List<IConnectedComponent> lh) {
		return (lh.size() == 1) && (lh.contains(CConnectedComponent.EMPTY));
	}
}
