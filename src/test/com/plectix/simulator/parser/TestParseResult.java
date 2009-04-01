package com.plectix.simulator.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.Converter;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.QuantityDataParser;
import com.plectix.simulator.util.RuleCCDataParser;
import com.plectix.simulator.util.RuleStructure;

public class TestParseResult extends DirectoryTestsRunner {
	private static final String myTestFileNamePrefix = RunParserTests
			.getFileNamePrefix()
			+ "rules/";
	private String myTestFileName;
	private Simulator mySimulator;
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
		mySimulator = initializator.getSimulator();
		myRule = mySimulator.getSimulationData().getKappaSystem().getRules().get(0);
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
		for (IConnectedComponent cc : myRule.getLeftHandSide()) {
			myLeftCCLines.add(Converter.toString(cc));
		}
		
		List<String> myRightCCLines = new ArrayList<String>();
		if (myRule.getRightHandSide() == null) {
			myRightCCLines.add("");
		} else {
			for (IConnectedComponent cc : myRule.getRightHandSide()) {
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
		int size = mySimulator.getSimulationData().getKappaSystem().getSolution().split().size();
		Integer expected = myInitQuantData.get(myTestFileName);
		if (expected == null) {
			myFailer.fail("Missing data for " + myTestFileName + " file");
		}
		myFailer.assertEquals("Init quantity", expected, size);
	}

	@Override
	public String getPrefixFileName() {
		return myTestFileNamePrefix;
	}
}
