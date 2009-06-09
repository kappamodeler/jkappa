package com.plectix.simulator.events;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.Info.InfoType;

@RunWith(value = Parameterized.class)
public class TestEvents {
	private static final String separator = File.separator;
	private static final String testDirectory = "test.data"+ separator + "events" + separator;
	private static String prefixFileName = "";

	private Simulator mySimulator;
	private Integer [] eventsNumbers = {0, 1, 10, 100, 500, 1000, 10000};

	
	@Parameters
	public static Collection<Object[]> data() {
		String[] files = new String[] { 
				"test" 
					 };
		Collection<Object[]> data = new ArrayList<Object[]>();
		for (String string : files) {
			Object[] obj = new Object[1];
			obj[0] = string;
			data.add(obj);
		}
		return data;
	}
	
	public TestEvents(String filename) {
		prefixFileName  = filename;
//		SimulationMain.initializeLogging();
	}

	@Test
	public void test() {
		for (int i = 0; i < eventsNumbers.length; i++) {
			setup(eventsNumbers[i]);
//			System.out.println(mySimulator.getSimulationData().getSimulationArguments().getEvent());
		}
	}
	
	public void setup(Integer eventNumber) {
		init(testDirectory + prefixFileName, eventNumber);
		try {
			mySimulator.run(0);
		} catch (Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	
	
	public void init(String filePath, Integer eventNumber) {
		mySimulator = null;
		mySimulator = new Simulator();
		SimulationData simulationData = mySimulator.getSimulationData();
		SimulationArguments args = null;
		try {
			args = Initializator.prepareEventNumberArguments(filePath, eventNumber);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, args);
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
	}

}
