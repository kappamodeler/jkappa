package com.plectix.simulator.miscellaneous;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.FileNameCollectionGenerator;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.util.DefaultPropertiesForTest;
import com.plectix.simulator.util.Info.InfoType;

@RunWith(value = Parameterized.class)
public class TestInitTime extends DefaultPropertiesForTest {
	private static final String separator = File.separator;
	private static final String testDirectory = "test.data" + separator
			+ "initTime" + separator;
	private String prefixFileName = "";

	private Simulator simulator;
	private final Double[] initTimes = { -1.0, 1.0, 1.213 };
	private final Integer operationMode;

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> fileNames = FileNameCollectionGenerator
				.getAllFileNames(testDirectory);
		return OperationModeCollectionGenerator.generate(fileNames,true);
	}

	public TestInitTime(String filename, Integer opMode) {
		prefixFileName = filename;
		operationMode = opMode;
	}

	@Test
	public void test() throws Exception {
		try{
		for (int i = 0; i < initTimes.length; i++) {
			setup(initTimes[i]);
			Observables observables = simulator.getSimulationData()
					.getKappaSystem().getObservables();
			Double time = observables.getCountTimeList().get(0).getTime();
			String message = "initTime = " + initTimes[i] + "\nfirstTime = "
					+ time + "\n";
			assertTrue(message, !initTimes[i].equals(-1.0) ? initTimes[i]
					.equals(time) : time.equals(0.0));
		}} catch (Exception e) {
			assertTrue("" + prefixFileName + " " + operationMode, !simulator.getSimulationData()
					.getKappaSystem().getObservables().getCountTimeList().isEmpty());
			throw e;
		}
	}

	public void setup(Double initTime) throws Exception {
		init(testDirectory + prefixFileName, initTime);
		try {
			simulator.run(new SimulatorInputData(simulator.getSimulationData()
					.getSimulationArguments()));
		} catch (Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	public void init(String filePath, Double initTime) throws Exception {
		simulator = null;
		simulator = new Simulator();
		SimulationData simulationData = simulator.getSimulationData();
		SimulationArguments args = null;
		try {
			args = Initializator.prepareInitTimeArguments(filePath, initTime,
					operationMode);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, args);
		(new SimulationDataReader(simulationData)).readAndCompile();
		simulationData.getKappaSystem().initialize();
	}

}
