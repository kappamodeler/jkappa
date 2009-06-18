package com.plectix.simulator.perturbations.triggering;

import org.junit.runner.RunWith;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.ParseException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.components.perturbations.CPerturbation;
import com.plectix.simulator.components.perturbations.CPerturbationType;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.Info.InfoType;

@RunWith(value = Parameterized.class)
public class TestPerturbationsTriggering {

	private static final String separator = File.separator;
	private static final String testDirectory = "test.data" + separator
			+ "perturbations" + separator + "triggering" + separator;
	private static String prefixFileName = "";

	private Simulator mySimulator;
	private Integer[] times = { 200 };

	@Parameters
	public static Collection<Object[]> data() {
		String[] files = new String[] { "test" ,  "test01" ,  "test02" ,  "test03" , "test04"};
		Collection<Object[]> data = new ArrayList<Object[]>();
		for (String string : files) {
			Object[] obj = new Object[1];
			obj[0] = string;
			data.add(obj);
		}
		return data;
	}

	public TestPerturbationsTriggering(String filename) {
		prefixFileName = filename;
	}

	@Test
	public void test() {
		for (int i = 0; i < times.length; i++) {
			setup(times[i]);
			KappaSystem kappaSystem = mySimulator.getSimulationData()
					.getKappaSystem();
			List<CPerturbation> perturbations = kappaSystem.getPerturbations();
			
			//check the perturbations have been triggered
			for (CPerturbation perturbation : perturbations) {
				if (!perturbation.isDo() && perturbation.getType() == CPerturbationType.TIME && perturbation.getTimeCondition() < times[i])
					fail("perturbation: $T > " + perturbation.getTimeCondition() + " has not been triggered");
				else if (perturbation.isDo() && perturbation.getType() == CPerturbationType.TIME && perturbation.getTimeCondition() > times[i])
					fail("perturbation: $T > " + perturbation.getTimeCondition() + " has been triggered, but it must not to");
			}
			
		}
	}

	public void setup(Integer time) {
		init(testDirectory + prefixFileName, time);
		try {
			mySimulator.run(new SimulatorInputData(mySimulator
					.getSimulationData().getSimulationArguments()));
		} catch (Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	public void init(String filePath, Integer time) {
		mySimulator = null;
		mySimulator = new Simulator();
		SimulationData simulationData = mySimulator.getSimulationData();
		SimulationArguments args = null;
		try {
			args = Initializator.prepareTimeArguments(filePath, time);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, args);
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
	}

}
