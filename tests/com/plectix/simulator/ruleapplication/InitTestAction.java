package com.plectix.simulator.ruleapplication;

import java.util.List;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.SimulatorOption;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.util.PlxLogger;
import com.plectix.simulator.util.Info.InfoType;

public class InitTestAction extends DirectoryTestsRunner {
	private Simulator simulator;
	private static final PlxLogger LOGGER = ThreadLocalData
			.getLogger(InitTestAction.class);
	private double currentTime = 0.;
	private Rule activeRule;
	private static int iterationNumber = 0;
	private static int iterationsLimit = 70;
	private final String filePath;

	public InitTestAction(String testFilePath) {
		this.filePath = testFilePath;
	}

	private static SimulatorTestOptions prepareTestArgs(String filePath) {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.append(SimulatorOption.ALLOW_INCOMPLETE_SUBSTANCE);
		options.append(SimulatorOption.DEBUG_INIT);
		options.append(SimulatorOption.NO_SAVE_ALL);
		options.append(SimulatorOption.NO_MAPS);
		options.appendSimulation(filePath);
		return options;
	}

	@Before
	public void init() {
		simulator = new Simulator();

		SimulationData simulationData = simulator.getSimulationData();

		SimulatorCommandLine commandLine = null;
		try {
			commandLine = prepareTestArgs(filePath).toCommandLine();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine
				.getSimulationArguments());
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);

		// run();
	}

	@Override
	public void reset(String filePath) {
		SimulatorCommandLine commandLine = null;
		try {
			commandLine = prepareTestArgs(filePath).toCommandLine();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulator.getSimulationData().setSimulationArguments(InfoType.OUTPUT,
				commandLine.getSimulationArguments());
		simulator.resetSimulation(InfoType.OUTPUT);
	}

	@After
	public void teardown() {
		if (iterationNumber != iterationsLimit) {
			reset(filePath);
		}
	}

	protected List<Injection> run() {
		KappaSystem kappaSystem = simulator.getSimulationData()
				.getKappaSystem();
		activeRule = kappaSystem.getRandomRule();

		if (activeRule == null) {
			simulator.getSimulationData().setTimeLength(currentTime);
			System.exit(0);
		}

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Rule: " + activeRule.getName());

		List<Injection> injectionsList = kappaSystem
				.chooseInjectionsForRuleApplication(activeRule);

		currentTime += simulator.getSimulationData().getKappaSystem()
				.getTimeValue();

		// apply(injectionsList);
		return injectionsList;
	}

	protected void apply(List<Injection> injectionsList) throws StoryStorageException {
		if (!isClash(injectionsList)) {
			// negative update
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("negative update");

			activeRule.applyRule(injectionsList, simulator.getSimulationData());

		} else {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Clash");
		}
	}

	private boolean isClash(List<Injection> injections) {
		if (injections.size() == 2) {
			for (Site siteCC1 : injections.get(0).getSiteList())
				for (Site siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getParentAgent().getId() == siteCC2
							.getParentAgent().getId())
						return true;
		}
		return false;
	}

	@Override
	public String getPrefixFileName() {
		return null;
	}

	public Rule getActiveRule() {
		return activeRule;
	}

	public SimulationData getSimulationData() {
		return simulator.getSimulationData();
	}

}
