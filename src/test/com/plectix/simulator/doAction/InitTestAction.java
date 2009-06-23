package com.plectix.simulator.doAction;

import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;


import com.plectix.simulator.components.CSite;
import com.plectix.simulator.probability.CProbabilityCalculation;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.PlxLogger;
import com.plectix.simulator.util.Info.InfoType;

public class InitTestAction extends DirectoryTestsRunner{
	private static Simulator mySimulator;
	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(InitTestAction.class);
	private double currentTime = 0.;
	private CRule myActiveRule;
	private static int myRunQuant = 0;
	private static int myTestQuant = 70;
	private static String FilePath = "";
	private static String testDirectory;


	public InitTestAction(String testFilePath) {
		FilePath = testFilePath;
	}
	
	private static String[] prepareTestArgs(String filePath) {
		String arg1 = new String("--debug");
		String arg2 = new String("--sim");
		String arg3 = new String(filePath);
		String arg4 = new String("--no_save_all");

		String[] args = new String[4];
		args[0] = arg1;
		args[1] = arg2;
		args[2] = arg3;
		args[3] = arg4;
		return args;
	}

	@Before
	public void init() {
		mySimulator = new Simulator();
		
		String[] testArgs = prepareTestArgs(FilePath);
		
		SimulationData simulationData = mySimulator.getSimulationData();

		SimulatorCommandLine commandLine = null;
		try {
			commandLine = new SimulatorCommandLine(testArgs);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		simulationData.setSimulationArguments(InfoType.OUTPUT,commandLine.getSimulationArguments());
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
		
		
		
//		run();
	}
	
	public void reset(String filePath) {
		String[] testArgs = prepareTestArgs(filePath);
		SimulatorCommandLine commandLine = null;
		try {
			commandLine = new SimulatorCommandLine(testArgs);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		mySimulator.getSimulationData().setSimulationArguments(InfoType.OUTPUT,commandLine.getSimulationArguments());
		mySimulator.resetSimulation(InfoType.OUTPUT);
	}
	

	@After
	public void teardown() {
		if (myRunQuant != myTestQuant) {
			reset(FilePath);
		}
	}

	protected List<CInjection> run() {
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				InfoType.OUTPUT,mySimulator.getSimulationData());
		myActiveRule = mySimulator.getSimulationData().getKappaSystem().getRandomRule();

		if (myActiveRule == null) {
			mySimulator.getSimulationData().setTimeLength(currentTime);
			System.exit(0);
		}

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Rule: " + myActiveRule.getName());

		List<CInjection> injectionsList = ruleProbabilityCalculation.chooseInjectionsForRuleApplication(myActiveRule);

		currentTime += mySimulator.getSimulationData().getKappaSystem().getTimeValue();

//		apply(injectionsList);
		return injectionsList;
	}

	protected void apply(List<CInjection> injectionsList) {
		if (!isClash(injectionsList)) {
			// negative update
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("negative update");

			myActiveRule.applyRule(injectionsList, mySimulator.getSimulationData());

		} else {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Clash");
		}
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

	@Override
	public String getPrefixFileName() {
		return testDirectory;
	}
	
	public CRule getActiveRule(){
		return myActiveRule;
	}
	
	public SimulationData getSimulationData(){
		return mySimulator.getSimulationData();
	}


}
