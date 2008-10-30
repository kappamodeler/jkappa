package com.plectix.simulator;

import java.util.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.junit.*;

import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
import com.plectix.simulator.simulator.SimulatorManager;

@RunWith(value=Suite.class)
@SuiteClasses(value = {
		TestInjectionsCorrection.class, 
		TestInjectionsLifts.class,
		TestInjectionsQuantity.class,
		TestInjectionsAgentLinking.class
	})
public class RunInjectionsTests {
	private static SimulatorManager manager;
	private static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	private static List<ObservablesConnectedComponent> myObsComponents;
	
	@BeforeClass
	public static void setup() {
		SimulationMain instance = new SimulationMain();
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);

		
		String arg1 = new String("--debug");
		String arg2 = new String("--sim");
		String arg3 = new String("test.data/TheGreatTestFile");
		
		String[] args = new String[3];
		args[0] = arg1;
		args[1] = arg2;
		args[2] = arg3;
		instance.parseArguments(args);
		instance.readSimulatonFile();
		instance.initialize();
		manager = SimulationMain.getSimulationManager();

		myObsComponents = manager.getSimulationData().getObservables().getConnectedComponentList();
	}
	
	protected static List<ObservablesConnectedComponent> getObservatory() {
		return Collections.unmodifiableList(myObsComponents);
	}
	
	protected static String getNameById(int id) {
		return manager.getNameDictionary().getName(id);
	}

}