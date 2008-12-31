package com.plectix.simulator.parser;

import org.junit.Before;
import org.junit.Test;

import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.Failer;

public class TestSuccessParsing {
	private static final String myTestFileNamePrefix = RunParserTests.getFileNamePrefix();
	private Parser myParser;
	private DataReading myDR;
	private Failer myFailer = new Failer();
	
	@Before
	public void setup() {
		String fileName = myTestFileNamePrefix + "ParsePerturbationsTestFile";
		myDR = new DataReading(fileName);
		Simulator mySimulator = new Simulator();
		myParser = new Parser(myDR, mySimulator.getSimulationData());
	}
	
	@Test
	public void testParseAgents() {
		try {
			myDR.readData();
			myParser.parse();
		} catch (Exception e) {
			myFailer.fail(e.toString());
		}
	}
}
