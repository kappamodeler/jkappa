package com.plectix.simulator.parser;

import java.util.*;

import java.io.*;

import org.junit.Test;

import com.plectix.simulator.components.*;
import com.plectix.simulator.parser.Exeptions.ParseErrorException;
import com.plectix.simulator.simulator.DataReading;
import com.plectix.simulator.util.*;

public class TestParseAgentCorrection {
	private static final String myTestFileNamePrefix = RunParserTests.getFileNamePrefix();
	private final Parser myParser;
	private EasyFileReader myReader; 
	private MessageConstructor myMC;
	private Failer myFailer = new Failer();
	
	public TestParseAgentCorrection() {
		String fileName = myTestFileNamePrefix + "ParseAgentsTestFile";
		try {
			myReader = new EasyFileReader(fileName);
		} catch(FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		myParser = new Parser(new DataReading(fileName));
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
	
	private void tryParseAgent(String line, boolean isCorrect) {
		List<CDataString> listRules = new ArrayList<CDataString>();
		listRules.add(new CDataString(0, line));
		try {
			myParser.parseAgent(line);
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
	public void testParseAgents() {
		String line = myReader.getStringFromFile();
		boolean currentCorrectionValue = true;
		
		while (line != null) {
			if (!"".equals(line)) {
				if ("#INCORRECT".equals(line)) {
					currentCorrectionValue = false;
				} else if (!"#CORRECT".equals(line)) {
					tryParseAgent(line, currentCorrectionValue);
				}
			} 
			line = myReader.getStringFromFile();
		}
		myFailer.failOnMC(myMC);
	}
}
