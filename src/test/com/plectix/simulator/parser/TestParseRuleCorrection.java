package com.plectix.simulator.parser;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.EasyFileReader;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.MessageConstructor;

public class TestParseRuleCorrection {
	private static final String myTestFileNamePrefix = RunParserTests.getFileNamePrefix();
	private KappaSystemParser myParser;
	private EasyFileReader myReader; 
	private MessageConstructor myMC;
	private Failer myFailer = new Failer();
	
	@Before
	public void setup() {
		String fileName = myTestFileNamePrefix + "ParseRulesTestFile";
		try {
			myReader = new EasyFileReader(fileName);
		} catch(FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		Simulator mySimulator = new Simulator();
		KappaFileReader reader = new KappaFileReader(fileName);
		try {
			myParser = new KappaSystemParser(reader.parse(), mySimulator.getSimulationData());
		} catch(Exception e) {
			myFailer.fail("File Reading Error : " + e);
		}
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
		KappaFileParagraph rule = new KappaFileParagraph();
		rule.addLine(new KappaFileLine(0, line));
		try {
			myParser.createRules(rule);
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
