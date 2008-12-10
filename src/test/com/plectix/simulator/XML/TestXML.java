package com.plectix.simulator.XML;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.simulator.Simulator;

public abstract class TestXML {

	private static Simulator mySimulator;
	private static Initializator myInitializator = new Initializator();

	private static final String mySourceDirPath = PathFinder.MAIN_DIR + "source_ka/";

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
		mySimulator = initializator.getSimulator();
		mySimulator.getSimulationData().setTimeLength(time);

		mySimulator.getSimulationData().getObservables()
        .init(time, 0, -1, -1, true);

		mySimulator.getSimulationData().setXmlSessionName(
				PathFinder.MAIN_DIR + "out_java\\" + fileName.substring(0, fileName.indexOf(".ka")) + "_java.xml");
		try{
			mySimulator.run(0);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		int i = 0;
		for (String fileName : getAllTestFileNames(mySourceDirPath)) {
			setup(fileName, time[i]);
			i++;
		}
	}
}
