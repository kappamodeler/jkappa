package com.plectix.simulator.parser;

import java.util.*;

import java.io.*;

import org.junit.Test;

import com.plectix.simulator.components.*;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.*;

public class TestParseRuleCorrection {
	private static final String myTestFileNamePrefix = RunParserTests.getFileNamePrefix();
	private final Parser myParser;
	private EasyFileReader myReader; 
	private MessageConstructor myMC;
	private Failer myFailer = new Failer();
	
	public TestParseRuleCorrection() {
		String fileName = myTestFileNamePrefix + "ParseRulesTestFile";
		try {
			myReader = new EasyFileReader(fileName);
		} catch(FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		Simulator mySimulator = new Simulator(new SimulationData());
		myParser = new Parser(new DataReading(fileName), mySimulator.getSimulationData(), mySimulator);
		myMC = new MessageConstructor();
	}
	
	private void failOnLine(String line, boolean value) {
		myMC.addValue(line);
		if (value) {
			myMC.addComment("expected 'correct', but was 'incorrect'");
		} else {
			myMC.addComment("expected 'incorrect', but was 'correct'");
		}
	}
	
	private void testParseRule(String line, boolean isCorrect) {
		List<CDataString> listRules = new ArrayList<CDataString>();
		listRules.add(new CDataString(0, line));
		try {
			myParser.createRules(listRules);
			if (!isCorrect) {
				failOnLine(line, isCorrect);
			}
		} catch(ParseErrorException e) {
			if (isCorrect) {
				failOnLine(line, isCorrect);
			}
		}
	}

	@Test
	public void testParseRules() {
		String line = myReader.getStringFromFile();
		boolean currentCorrectionValue = true;
		
		while (line != null) {
			if (!"".equals(line)) {
				if ("#INCORRECT".equals(line)) {
					currentCorrectionValue = false;
				} else if (!"#CORRECT".equals(line)) {
					testParseRule(line, currentCorrectionValue);
				}
			} 
			line = myReader.getStringFromFile();
		}
		myFailer.failOnMC(myMC);
	}
}
