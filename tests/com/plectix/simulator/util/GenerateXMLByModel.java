package com.plectix.simulator.util;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.io.xml.SimulationDataXMLWriter;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.io.StringBufferWriter;

public abstract class GenerateXMLByModel extends DefaultPropertiesForTest {
	
	private Simulator mySimulator;
	private String sessionPath;
	private String mycount;
	private String directory;
	private Integer operationMode;

	public abstract SimulatorTestOptions prepareTestModelArgs();

//	public abstract SimulatorTestOptions prepareTestModelArgs(String count);

	public abstract String getComparePath();

	public String generateXML(String dir, String prefixFile, Integer opMode) throws Exception {
		
		return generateXML(dir, null, prefixFile, opMode);
		
	}
	
	private String generateXML(String dir, String dirResult, String prefixFile, Integer opMode) throws Exception {

		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		mySimulator = new Simulator();
		directory = dir;
		sessionPath = dirResult;
		mycount = prefixFile;
		operationMode = opMode;
		
		SimulatorCommandLine commandLine;
		try {
			commandLine = prepareTestModelArgs().toCommandLine();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		SimulationData simulationData = mySimulator.getSimulationData();
		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine
				.getSimulationArguments());
		(new SimulationDataReader(simulationData)).readSimulationFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);

		try {
			StringBufferWriter writer = new StringBufferWriter();
			new SimulationDataXMLWriter(simulationData).outputXMLData(writer);
			return writer.toString(); 
		} catch (Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
			return null;
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
	
	public Integer getOperationMode() {
		return operationMode;
	}
	
	public String fileName(String suffix, String number, String extension) {
		return directory + suffix + number + extension;
	}
//	
//	public String defaultModelFileName(String number) {
//		return directory + DEFAULT_PREFIX_MODEL_FILE + number + DEFAULT_EXTENTION_FILE;
//	}
//	
//	public String defaultXMLFileName(String number) {
//		return directory + DEFAULT_PREFIX_XML_FILE + number + DEFAULT_EXTENTION_FILE;
//	}
	
	public String defaultPathModelFileName() {
		return directory + DEFAULT_PREFIX_MODEL_FILE + mycount + DEFAULT_EXTENSION_FILE;
	}
	
	public String defaultPathXMLFileName() {
		return directory + DEFAULT_PREFIX_XML_FILE + mycount + DEFAULT_EXTENSION_FILE;
	}

}
