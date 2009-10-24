package com.plectix.simulator.miscellaneous;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.component.Observables;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.DefaultPropertiesForTest;
import com.plectix.simulator.util.Info.InfoType;

@RunWith(value = Parameterized.class)
public class TestInitTime extends DefaultPropertiesForTest {
	private static final String separator = File.separator;
	private static final String testDirectory = "test.data" + separator
			+ "initTime" + separator;
	private String prefixFileName = "";

	private Simulator simulator;
	private Double[] initTimes = { -1.0, 1.0, 10.0, 10.213 };

	@Parameters
	public static Collection<Object[]> data() {
		String[] files = new String[] {
				"test" + DEFAULT_EXTENSION_FILE,
				"test01" + DEFAULT_EXTENSION_FILE,
				"test02" + DEFAULT_EXTENSION_FILE,
				"test03" + DEFAULT_EXTENSION_FILE,
				"test04" + DEFAULT_EXTENSION_FILE };
		Collection<Object[]> data = new ArrayList<Object[]>();
		for (String string : files) {
			Object[] obj = new Object[1];
			obj[0] = string;
			data.add(obj);
		}
		return data;
	}

	public TestInitTime(String filename) {
		prefixFileName = filename;
	}

	@Test
	public void test() {
		for (int i = 0; i < initTimes.length; i++) {
			setup(initTimes[i]);
			Observables observables = simulator.getSimulationData()
					.getKappaSystem().getObservables();
			Double time = observables.getCountTimeList().get(0).getTime();
			String message = "initTime = " + initTimes[i] + "\nfirstTime = "
					+ time + "\n";
			assertTrue(message, !initTimes[i].equals(-1.0) ? initTimes[i]
					.equals(time) : time.equals(0.0));
		}
	}

	public void setup(Double initTime) {
		init(testDirectory + prefixFileName, initTime);
		try {
			simulator.run(new SimulatorInputData(simulator.getSimulationData()
					.getSimulationArguments()));
		} catch (Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	public void init(String filePath, Double initTime) {
		simulator = null;
		simulator = new Simulator();
		SimulationData simulationData = simulator.getSimulationData();
		SimulationArguments args = null;
		try {
			args = Initializator.prepareInitTimeArguments(filePath, initTime);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, args);
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
	}

}
