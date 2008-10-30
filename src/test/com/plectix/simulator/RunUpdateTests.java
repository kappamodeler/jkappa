package com.plectix.simulator;

import java.util.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.components.*;
import com.plectix.simulator.simulator.Model;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorManager;

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		TestNegativeUpdate.class,
		TestPositiveUpdate.class,
		//TestActivatedRules.class 
		})
public class RunUpdateTests {

	private static SimulatorManager myManager;
	private static Model myModel;
	private static Simulator mySimulator;
	private static boolean myFirstRun = true;
	
	private static SimulationMain instance;

	private static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	public static boolean isFirstRun() {
		return myFirstRun;
	}
	
	public static void parseArgs(String filePath) {
		String arg1 = new String("--debug");
		String arg2 = new String("--sim");
		String arg3 = new String(filePath);
		String arg4 = new String("--time");
		String arg5 = new String("15");

		String[] args = new String[5];
		args[0] = arg1;
		args[1] = arg2;
		args[2] = arg3;
		args[3] = arg4;
		args[4] = arg5;
		instance.parseArguments(args);
	}

	public static void reset(String filePath) {
		RunUpdateTests.parseArgs(filePath);
		mySimulator.resetSimulation();
	}
	
	public static void init(String filePath) {
		if (myFirstRun) {
			new SimulationMain();
			instance = SimulationMain.getInstance();

			parseArgs(filePath);

			instance.readSimulatonFile();
			instance.initialize();

			myManager = SimulationMain.getSimulationManager();

			myModel = new Model(myManager.getSimulationData());

			mySimulator = new Simulator(myModel);
			PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
			myFirstRun = false;
		} else {
			reset(filePath);
		}
	}
	
	public static Model getModel() {
		return myModel;
	}
	
	public static SimulatorManager getManager() {
		return myManager;
	}
	
	public static boolean isClash(List<CInjection> injections) {
		if (injections.size() == 2) {
			for (CSite siteCC1 : injections.get(0).getSiteList())
				for (CSite siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getAgentLink().getId() == siteCC2
							.getAgentLink().getId())
						return true;
		}
		return false;
	}
	
	public static Simulator getSimulator() { 
		return mySimulator;
	}
	
	public static boolean lhsIsEmpty(List<CConnectedComponent> lh) {
		return (lh.size() == 1) && (lh.contains(CRule.EMPTY_LHS_CC));
	}


}
