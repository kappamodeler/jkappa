package com.plectix.simulator.parser;

import org.junit.Before;
import org.junit.Test;

import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.Failer;

public class TestSuccessParsing {
	private static final String myTestFileNamePrefix = RunParserTests.getFileNamePrefix();
	private KappaSystemParser myParser;
	private Failer myFailer = new Failer();
	
//	@Before
//	public void setup() {
//		String fileName = myTestFileNamePrefix + "ParsePerturbationsTestFile";
//		Simulator mySimulator = new Simulator();
//		
//	}
	
	@Test
	public void testParseAgents() {
		String fileName = myTestFileNamePrefix + "ParsePerturbationsTestFile";
		Simulator mySimulator = new Simulator();
		KappaFileReader reader = new KappaFileReader(fileName);
		try {
			myParser = new KappaSystemParser(reader.parse(), mySimulator.getSimulationData());
		} catch(Exception e) {
			myFailer.fail("File Reading Error : " + e);
		}
		try {
			myParser.parse();
		} catch (Exception e) {
			myFailer.fail(e.toString());
		}
	}
}
