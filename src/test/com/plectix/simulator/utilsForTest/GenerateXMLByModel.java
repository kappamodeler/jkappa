package com.plectix.simulator.utilsForTest;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

public abstract class GenerateXMLByModel {

	private static String LOG4J_PROPERTIES_FILENAME; 
	
	private static final String testModel = "model";
	
	private static Simulator mySimulator;
	private static String sessionPath;
	private static String mycount;
	private static String directory;
	
	private static SimulationArguments argSimulation;

	public GenerateXMLByModel(String log4JPropertiesFilename) {
		
		LOG4J_PROPERTIES_FILENAME = log4JPropertiesFilename;
		
	}	
	
	public abstract String[] prepareTestArgs(String count); 
	
	public abstract String[] prepareTestModelArgs(String count);

	public abstract String getSessionPath();
	
	public abstract String getComparePath();

	
	public void generateXML(String dir, String dirResult, String prefixFile) {
		
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		mySimulator = new Simulator();
		directory = dir;
		sessionPath = dirResult;
		mycount = prefixFile;
		
		String[] testArgs = null;
		if(dir.contains(testModel))
			testArgs = prepareTestModelArgs(prefixFile);
		else 
			testArgs = prepareTestArgs(prefixFile);

		SimulationData simulationData = mySimulator.getSimulationData();

		SimulatorCommandLine commandLine = null;
		try {
			commandLine = new SimulatorCommandLine(testArgs);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine.getSimulationArguments());
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
		simulationData.getSimulationArguments().setXmlSessionName(getSessionPath());
		try {
			mySimulator.run(0);
		} catch (Exception e) {
			junit.framework.Assert.fail(e.getMessage());
		}
		
		// Output XML data:
		Source source;
		try {
			simulationData.outputData(0);
		} catch (TransformerException e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		} catch (StoryStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}
	
	public String getSessionPathInside() {
		return sessionPath;
	}
	public String getMyCountInside() {
		return mycount;
	}
	
	public String getDirectoryInside() {
		return directory;
	}
	
	
}
