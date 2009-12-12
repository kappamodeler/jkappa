package com.plectix.simulator.ruleapplication;

import java.util.List;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.io.SimulationDataReader;
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
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.io.PlxLogger;

public class InitTestAction extends DirectoryTestsRunner {
	private Simulator simulator;
	private static final PlxLogger LOGGER = ThreadLocalData
			.getLogger(InitTestAction.class);
	private double currentTime = 0.;
	private Rule activeRule;
	private static int iterationNumber = 0;
	private static int iterationsLimit = 70;
	private final String filePath;
	private final Integer operationMode;

	public InitTestAction(String testFilePath, Integer opMode) {
		this.filePath = testFilePath;
		this.operationMode = opMode;
	}

	private static SimulatorTestOptions prepareTestArgs(String filePath, Integer opMode) {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.append(SimulatorOption.ALLOW_INCOMPLETE_SUBSTANCE);
		options.append(SimulatorOption.DEBUG_INIT);
		options.append(SimulatorOption.NO_MAPS);
		options.appendSimulation(filePath);
		options.appendOperationMode(opMode);
		return options;
	}

	@Before
	public void init() {
		simulator = new Simulator();

		SimulationData simulationData = simulator.getSimulationData();

		SimulatorCommandLine commandLine = null;
		try {
			commandLine = prepareTestArgs(filePath, operationMode).toCommandLine();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine
				.getSimulationArguments());
		(new SimulationDataReader(simulationData)).readSimulationFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);

		// run();
	}

	@Override
	public void reset(String filePath, Integer opMode) {
		SimulatorCommandLine commandLine = null;
		try {
			commandLine = prepareTestArgs(filePath, opMode).toCommandLine();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulator.getSimulationData().setSimulationArguments(InfoType.OUTPUT,
				commandLine.getSimulationArguments());
		simulator.resetSimulation();
	}

	@After
	public void teardown() {
		if (iterationNumber != iterationsLimit) {
			reset(filePath, operationMode);
		}
	}

	protected List<Injection> run() {
		KappaSystem kappaSystem = simulator.getSimulationData()
				.getKappaSystem();
		kappaSystem.updateRuleActivities();
		activeRule = kappaSystem.getRandomRule();

		if (activeRule == null) {
			simulator.getSimulationData().getClock().setTimeLength(currentTime);
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
