package com.plectix.simulator.parser;

import org.junit.*;

import com.plectix.simulator.util.*;

public class TestSuccessParsing {
	private static final String myTestFileNamePrefix = RunParserTests.getFileNamePrefix();
	private Parser myParser;
	private DataReading myDR;
	private Failer myFailer = new Failer();
	
	@Before
	public void setup() {
		String fileName = myTestFileNamePrefix + "ParsePerturbationsTestFile";
		myDR = new DataReading(fileName);
		myParser = new Parser(myDR);
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
