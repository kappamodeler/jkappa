package com.plectix.simulator.parser.newtests;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.ParseException;
import org.junit.*;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.parser.*;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.reader.*;
import com.plectix.simulator.parser.exceptions.*;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.MessageConstructor;

public class ExceptionsTest {
	private final String mySourceFilePath = "test.data/new_parser/ExceptionsTestFile";
	private final String myExceptionsDataFilePath = "test.data/new_parser/ExceptionsTestData";
	private final KappaModel myModel = new KappaModel();
	private final AgentFactory myAgentFactory = new AgentFactory();
	private final Map<Integer, String> myExceptionsData = new HashMap<Integer, String>();
	
	private enum ReaderUsageModifier {
		RULES("#RULES"),
		SOLUTION("#SOLUTION"),
		STORIES("#STORIES"),
		OBSERVABLES("#OBSERVABLES"),
		PERTURBATIONS("#PERTURBATIONS");
		
		private final String myLine;
		
		private ReaderUsageModifier(String line) {
			myLine = line;
		}
		
		public static ReaderUsageModifier getModifier(String line) {
			for (ReaderUsageModifier modifier : ReaderUsageModifier.values()) {
				if (modifier.myLine.equals(line)) {
					return modifier; 
				}
			}
			return null;
		}
	}
	
	private KappaParagraphReader<?> checkReader(String line) throws ParseException {
		ReaderUsageModifier modifier = ReaderUsageModifier.getModifier(line);
		if (modifier == null) {
			return null;
		}
		switch(modifier) {
		case RULES:
			return new RulesParagraphReader(myModel, Initializator
					.prepareDefaultArguments(mySourceFilePath), myAgentFactory);
		}
		return null;
	}
	
	@Before
	public void setup() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(myExceptionsDataFilePath));
			String line = reader.readLine();
			for (int i = 1; line != null; i++) {
				myExceptionsData.put(i, line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void test() {
		Failer failer = new Failer();
		MessageConstructor mc = new MessageConstructor();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(mySourceFilePath));
			String line = reader.readLine();
			KappaFileParagraph oneLineParagraph;
			KappaParagraphReader<?> kappaReader = null;
			
			for (int i = 0; line != null; line = reader.readLine()) {
				KappaParagraphReader<?> checkReader = checkReader(line);
				if (checkReader != null) {
					kappaReader = checkReader;
					continue;
				} else {
					i++;
				}
				
				oneLineParagraph = new KappaFileParagraph();
				oneLineParagraph.addLine(new KappaFileLine(i, line));
				try {
					kappaReader.readComponent(oneLineParagraph);
					mc.addValue("line " + (i + 1));
					mc.addComment("No exceptions was catched");
				} catch(SimulationDataFormatException e) {
					failer.assertEquals("", myExceptionsData.get(i), e.getErrorType() + "");
				}
			}
			failer.failOnMC(mc);
		} catch (IOException e) {
			failer.fail(e.getMessage());
		} catch (ParseException e) {
			failer.fail(e.getMessage());
		}
	}
}
