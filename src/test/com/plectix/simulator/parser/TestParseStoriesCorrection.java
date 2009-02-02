package com.plectix.simulator.parser;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.QuantityDataParser;
import com.plectix.simulator.util.Info.InfoType;

@RunWith(value = Parameterized.class)
public class TestParseStoriesCorrection {
	private static Simulator mySimulator;
	private static int myRunQuant = 0;
	private static int myTestQuant = 2;
	private static String FilePath = "";
	private final static String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";
	private static final String myPrefixFileName = "test.data/parser/stories/test";
	private static Map<String, Integer> myExpectedData;

	@Parameters
	public static Collection<Object[]> regExValues() {
		LinkedList<Object[]> parameters = new LinkedList<Object[]>();
		StringBuffer suffixFileName;
		for (int i = 0; i < myTestQuant; i++) {
			suffixFileName = new StringBuffer();
			if (i < 9) {
				suffixFileName.append("0");
			}
			suffixFileName.append(i + 1);
			parameters.add(new Object[] { (myPrefixFileName + suffixFileName) });
		}
		return Collections.unmodifiableList(parameters);
	}

	public TestParseStoriesCorrection(String testFilePath) {
		FilePath = testFilePath;
		init();
	}

	private static String[] prepareTestArgs(String filePath) {
		String arg1 = new String("--debug");
		String arg2 = new String("--storify");
		String arg3 = new String(filePath);
		String arg4 = new String("--no_save_all");

		String[] args = new String[4];
		args[0] = arg1;
		args[1] = arg2;
		args[2] = arg3;
		args[3] = arg4;
		return args;
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
		System.out.println(filePath);
		
		mySimulator.getSimulationData().setSimulationArguments(InfoType.OUTPUT,commandLine.getSimulationArguments());
		mySimulator.resetSimulation(InfoType.OUTPUT);
	}
	
	public static void init() {
		myExpectedData = (new QuantityDataParser("test.data/parser/stories/data")).parse();
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
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
		simulationData.initialize(InfoType.OUTPUT);
	}

	@Before
	public void setup() {
		myRunQuant++;
	}

	@Test
	public void test() {
		CStories stories = mySimulator.getSimulationData().getStories();
		String file = FilePath.substring(FilePath.lastIndexOf("/")+1);
		if (myExpectedData.get(file) == null) {
			fail("File not find!");
		} else {
			int expected = myExpectedData.get(file);
			int ruleIDAtStory = stories.getRuleIdAtStories(0);
			if(ruleIDAtStory!=expected)
				fail("Error parse stories at "+FilePath);
			
		}
	}
}
