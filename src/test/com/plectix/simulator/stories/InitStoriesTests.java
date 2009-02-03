package com.plectix.simulator.stories;

import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.components.stories.CStoryTrees;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

public class InitStoriesTests extends DirectoryTestsRunner {
	private final static String testDirectory = "test.data/stories/";

	private final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";
	
	private String FilePath = "";
	private Simulator mySimulator;
	private CStoryTrees storyTrees;

	private double time = 10;

	@Override
	public String getPrefixFileName() {
		return getDirectory();
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(getDirectory());
	}

	public InitStoriesTests(String testFilePath) {
		FilePath = testFilePath;
	}

	@Before
	public void setup() {
		init(getDirectory() + FilePath);
		mySimulator.getSimulationData().setTimeLength(time);
		try {
			mySimulator.runStories();
		} catch (Exception e) {
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	private String[] prepareTestArgs(String filePath) {
		String[] args;
		args = new String[7];
		args[0] = "--storify";
		args[1] = filePath;
		args[2] = "--event";
		args[3] = "1000";
		args[4] = "--no_compress_stories";
		args[5] = "--iteration";
		args[6] = "10";
		return args;
	}
	
	public void init(String filePath) {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		mySimulator = new Simulator();
		
		String[] testArgs = prepareTestArgs(filePath);

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

	public static String getDirectory() {
		return testDirectory;
	}

	public CStories getStories() {
		return mySimulator.getSimulationData().getStories();
	}

	public CStoryTrees getStoryTrees() {
		return storyTrees;
	}

	public List<IRule> getRules() {
		return mySimulator.getSimulationData().getRules();
	}

}
