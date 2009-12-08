package com.plectix.simulator.events;

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
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.Info.InfoType;

@RunWith(value = Parameterized.class)
public final class TestEvents {
	private static final String SEPARATOR = File.separator;
	private static final String TEST_DIRECTORY = "test.data" + SEPARATOR
			+ "events" + SEPARATOR;
	private final String prefixFileName;
	private Simulator simulator;
	private final Integer[] eventsNumbers = { 0, 1, 10, 100, 500, 1000, 1001,
			1002 };
	private final Integer operationMode;

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> allFileNames = FileNameCollectionGenerator
				.getAllFileNames(TEST_DIRECTORY);
		return OperationModeCollectionGenerator.generate(allFileNames,true);
	}

	public TestEvents(String filename, Integer opMode) {
		prefixFileName = filename;
		operationMode = opMode;
	}

	@Test
	public void test() {
		for (int i = 0; i < eventsNumbers.length; i++) {
			setup(eventsNumbers[i]);
			assertTrue(eventsNumbers[i] == simulator.getSimulationData()
					.getSimulationArguments().getMaxNumberOfEvents());

		}
	}

	public void setup(Integer eventNumber) {
		init(TEST_DIRECTORY + prefixFileName, eventNumber);
		try {
			simulator.run(new SimulatorInputData(simulator.getSimulationData()
					.getSimulationArguments()));
		} catch (Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	public void init(String filePath, Integer eventNumber) {
		simulator = null;
		simulator = new Simulator();
		SimulationData simulationData = simulator.getSimulationData();
		SimulationArguments args = null;
		try {
			args = Initializator.prepareEventNumberArguments(filePath,
					eventNumber, operationMode);

		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, args);
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
	}

}
