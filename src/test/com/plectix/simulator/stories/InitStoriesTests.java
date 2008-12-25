package com.plectix.simulator.stories;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.CStoryTrees;
import com.plectix.simulator.components.CStoryType;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.options.SimulatorArguments;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.Simulator;


public class InitStoriesTests extends DirectoryTestsRunner{
	private final static String testDirectory = "test.data/stories/";
	
	
	private final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	private String FilePath = "";
	private Simulator mySimulator;
	private SimulatorArguments myArguments;
	private CStoryTrees storyTrees;

	private Initializator myInitializator = new Initializator();
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

	
	private void parseArgs(String filePath) {
		String[] args;
		args = new String[7];
		args[0] = "--storify";
		args[1] = filePath;
		args[2] = "--event";
		args[3] = "1000";
		args[4] = "--no_compress_stories";
		args[5] = "--iteration";
		args[6] = "10";
		myArguments = SimulationUtils.parseArguments(
				mySimulator.getSimulationData(), args);
	}
	
	
	public void init(String filePath) {
			PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
			mySimulator = new Simulator();
			parseArgs(filePath);
			SimulationUtils.readSimulatonFile(mySimulator, myArguments);
			mySimulator.init(myArguments);
		}

	public static String getDirectory() {
		return testDirectory;
	}
	
	public CStories getStories(){
		return  mySimulator.getSimulationData().getStories();
	}
	
	public CStoryTrees getStoryTrees(){
		return  storyTrees;
	}
	
	public  List<IRule> getRules(){
		return  mySimulator.getSimulationData().getRules();
	}
	

	
	
	
	
	
}
