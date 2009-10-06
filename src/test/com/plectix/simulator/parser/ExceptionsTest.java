package com.plectix.simulator.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.parser.abstractmodel.reader.KappaParagraphReader;
import com.plectix.simulator.parser.abstractmodel.reader.RulesParagraphReader;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.util.DefaultPropertiesForTest;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.MessageConstructor;

public class ExceptionsTest extends DefaultPropertiesForTest {
	
	private final String separator = File.separator;
	private final String prefix = "test.data" + separator + "new_parser"
			+ separator;
	private final String mySourceFilePath = prefix
			+ "ExceptionsTestFile" + DEFAULT_EXTENSION_FILE;
	private final String myExceptionsDataFilePath = prefix
			+ "ExceptionsTestData" + DEFAULT_EXTENSION_FILE;
	private final AgentFactory myAgentFactory = new AgentFactory(false);
	private final Map<Integer, String> myExceptionsData = new LinkedHashMap<Integer, String>();

	private enum ReaderUsageModifier {
		RULES("#RULES"), SOLUTION("#SOLUTION"), STORIES("#STORIES"), OBSERVABLES(
				"#OBSERVABLES"), PERTURBATIONS("#PERTURBATIONS");

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

	private KappaParagraphReader<?> checkReader(String line)
			throws ParseException {
		ReaderUsageModifier modifier = ReaderUsageModifier.getModifier(line);
		if (modifier == null) {
			return null;
		}
		switch (modifier) {
		case RULES:
			return new RulesParagraphReader(Initializator
					.prepareDefaultArguments(mySourceFilePath), myAgentFactory);
		}
		return null;
	}

	@Before
	public void setup() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					myExceptionsDataFilePath));
			String line = reader.readLine();
			for (int i = 1; line != null; i++) {
				myExceptionsData.put(i, line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	@Test
	public void test() {
		Failer failer = new Failer();
		MessageConstructor mc = new MessageConstructor();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					mySourceFilePath));
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
				} catch (SimulationDataFormatException e) {
					failer.assertEquals("", myExceptionsData.get(i), e
							.getErrorType()
							+ "");
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
