package com.plectix.simulator.parser;

import java.util.*;

import org.junit.*;

import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.util.*;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.components.*;
import com.plectix.simulator.simulator.SimulatorManager;

public class TestParseResult extends DirectoryTestsRunner {
	private static final String myTestFileNamePrefix = RunParserTests
			.getFileNamePrefix()
			+ "rules/";
	private String myTestFileName;
	private SimulatorManager myManager;
	private CRule myRule;
	private static final Map<String, RuleStructure> myRulesCCData = (new RuleCCDataParser(
			myTestFileNamePrefix + "RulesCCData")).parse();
	private static final Map<String, Integer> myInitQuantData = (new QuantityDataParser(
			myTestFileNamePrefix + "InitQuantData")).parse();

	private final Failer myFailer = new Failer();

	public TestParseResult(String fileName) {
		super();
		myTestFileName = fileName;
		myFailer.loadTestFile(fileName);
		String fullTestFilePath = getPrefixFileName() + myTestFileName;
		Initializator initializator = getInitializator();
		initializator.init(fullTestFilePath);
		myManager = initializator.getManager();
		myRule = myManager.getSimulationData().getRules().get(0);
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		return DirectoryTestsRunner.getAllTestFileNames(myTestFileNamePrefix);
	}

	@BeforeClass
	public static void setRescale() {
		getInitializator().setRescale(100.0);
	}

	@AfterClass
	public static void removeRescale() {
		getInitializator().setRescale(1.0);
	}

	private RuleStructure getRuleStructure() {
		List<String> myLeftCCLines = new ArrayList<String>();
		for (CConnectedComponent cc : myRule.getLeftHandSide()) {
			myLeftCCLines.add(Converter.toString(cc));
		}
		
		List<String> myRightCCLines = new ArrayList<String>();
		if (myRule.getRightHandSide() == null) {
			myRightCCLines.add("");
		} else {
			for (CConnectedComponent cc : myRule.getRightHandSide()) {
				myRightCCLines.add(Converter.toString(cc));
			}
		}
		return new RuleStructure(myLeftCCLines, myRightCCLines);
	}
	
	@Test
	public void testRule() {
		RuleStructure rule = getRuleStructure();
		
		RuleStructure expected = myRulesCCData.get(myTestFileName);
		if (expected.getLHS().isEmpty() || expected.getRHS().isEmpty()) {
			myFailer.fail("Missing data for " + myTestFileName + " file");
		} else {
			myFailer.assertEquals("Rule components", expected, rule);
		}
	}

	@Test
	public void testInitQuant() {
		int size = myManager.getSimulationData().getSolution().split().size();
		Integer expected = myInitQuantData.get(myTestFileName);
		if (expected == null) {
			myFailer.fail("Missing data for " + myTestFileName + " file");
		}
		myFailer.assertEquals("Init quantity", expected, size);
	}

	public String getPrefixFileName() {
		return myTestFileNamePrefix;
	}
}
