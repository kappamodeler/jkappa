package com.plectix.simulator.ruleCompressions;

import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.complex.localviews.CLocalViewsMain;
import com.plectix.simulator.components.complex.subviews.CMainSubViews;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

public class InitTestRuleCompressions {

	private static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	private static final String FILENAME_EXTENSION = InitData.FILENAME_EXTENSION;
	
	private static final String testModel = "model";
	
	private static Simulator mySimulator;
	private static String sessionPath;
	private static String mycount;
	private static String directory;
	
	private static SimulationArguments argSimulation;
	
	private static String[] prepareTestModelArgs(String count) {
		
		String[] args = new String[9];
		
		args[0] = "--short-console-output";
		args[1] = "--contact-map";
		args[2] = directory + "~kappa" + count + FILENAME_EXTENSION; //".tmp";
		args[3] = "--no-dump-iteration-number";
		args[4] = "--no-dump-rule-iteration";
		args[5] = "--no-build-influence-map";
		args[6] = "--no-compute-quantitative-compression";
		args[7] = "--no-compute-qualitative-compression";
		args[8] = "--no-enumerate-complexes";
		return args;
	}

	public static void init(String dir, String dirResult, String count) {
		
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		
		mySimulator = new Simulator();
		directory = dir;
		sessionPath = dirResult;
		mycount = count;
		
		String[] testArgs = null;
		
		if(dir.contains(testModel))
			testArgs = prepareTestModelArgs(count);
		else
			//testArgs = prepareTestArgs(count);
			return;

		SimulationData simulationData = mySimulator.getSimulationData();

		SimulatorCommandLine commandLine = null;
		try {
			commandLine = new SimulatorCommandLine(testArgs);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		argSimulation = commandLine.getSimulationArguments();
			
		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine.getSimulationArguments());
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
		
	}
	
	public static KappaSystem getKappaSystem() {
		
		return mySimulator.getSimulationData().getKappaSystem();
	}
	
	public static IAllSubViewsOfAllAgents getSubViews() {
		return mySimulator.getSimulationData().getKappaSystem().getSubViews();
	}
	
	public static List<CRule> getRules(){
		return mySimulator.getSimulationData().getKappaSystem().getRules();
	}
	
	public static String getResultPath(){
		return sessionPath + "result" + mycount + ".tmp";
	}
	
}
