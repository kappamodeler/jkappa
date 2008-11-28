package com.plectix.simulator.XML;

import java.io.File;
import java.util.*;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.simulator.Model;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorManager;

public abstract class TestXML {

	private static Model myModel;
	private static Simulator mySimulator;
	private static SimulatorManager myManager;
	private static Initializator myInitializator = new Initializator();

	private static final String mySourceDirPath = PathFinder.MAIN_DIR + "source .ka/";

	private static final double[] time = new double[] { 0.01, 1000, 
        0.5, 100, 
        50, 50, 
        25, 25, 
        10, 10, 
        10,     40, 
        10, 10, 
        15, 10 //,1000
        ,15, 20 };

	public static Collection<String> getAllTestFileNames(String prefix) {
		LinkedList<String> parameters = new LinkedList<String>();
		try {
			File testFolder = new File(prefix);
			if (testFolder.isDirectory()) {
				for (String fileName : testFolder.list()) {
					if (fileName.endsWith(".ka")) {
						parameters.add(fileName);
					}
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return Collections.unmodifiableList(parameters);
	}

	public static void setup(String fileName, double time) {
		String fullTestFilePath = mySourceDirPath + fileName;
		Initializator initializator = myInitializator;

		initializator.init(fullTestFilePath);
		myModel = initializator.getModel();
		myModel.getSimulationData().setTimeLength(time);

		myModel.getSimulationData().getObservables()
        .init(time, 0, -1, -1, true);

		mySimulator = initializator.getSimulator();
		myModel.getSimulationData().setXmlSessionName(
				PathFinder.MAIN_DIR + "out_java\\" + fileName.substring(0, fileName.indexOf(".ka")) + "_java.xml");
		myManager = initializator.getManager();
		mySimulator.run(null);
		
		
	}

	public static void main(String[] args) {
		int i = 0;
		for (String fileName : getAllTestFileNames(mySourceDirPath)) {
			setup(fileName, time[i]);
			i++;
		}
	}
}
