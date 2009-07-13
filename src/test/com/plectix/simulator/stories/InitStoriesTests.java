package com.plectix.simulator.stories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.stories.CStories;

import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.Info.InfoType;

public class InitStoriesTests extends DirectoryTestsRunner {
	private static String testDirectory = "";

	private String FileName = "";
	private Simulator mySimulator;
//	private CStoryTrees storyTrees;

	private double time = 10;
	private boolean isSlow;
	private boolean isWeak;

	@Override
	public String getPrefixFileName() {
		return testDirectory;
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(getDirectory());
	}

	public InitStoriesTests(String path, String fileName, boolean isSlow, boolean isWeak) {
		testDirectory = path;
		FileName = fileName;
		this.isSlow = isSlow;
		this.isWeak = isWeak;
	}

	@Before
	public void setup() {
		init(testDirectory + FileName);
		mySimulator.getSimulationData().setTimeLength(time);
		try {
			mySimulator.runStories();
		} catch (Exception e) {
			e.printStackTrace();
//			junit.framework.Assert.fail(e.getMessage());
		}
	}

	public void init(String filePath) {
		mySimulator = new Simulator();

		SimulationData simulationData = mySimulator.getSimulationData();

		SimulationArguments args = null;
		try {
			if (!isSlow)
				args = Initializator.prepareStorifyArguments(filePath, isWeak);
			else 
				args = Initializator.prepareStorifyArgumentsSlow(filePath, isWeak);
				
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, args);
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
	}

	public static String getDirectory() {
		return testDirectory;
	}

	public CStories getStories() {
		return mySimulator.getSimulationData().getKappaSystem().getStories();
	}

//	public CStoryTrees getStoryTrees() {
//		return storyTrees;
//	}

	public List<CRule> getRules() {
		return mySimulator.getSimulationData().getKappaSystem().getRules();
	}

}
