package com.plectix.simulator.perturbations;

import java.io.File;
import java.util.*;

import org.junit.*;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.components.CRule;

import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.QuantityDataParser;

public class TestTimeCondition extends TestPerturbation {
	
	private static final String separator = File.separator;
	private static final String myPrefixFileName = "test.data" + separator + "perturbations" + separator;
	private String myTestFileName = "";
	private Failer myFailer = new Failer(); 
	private CRule myActiveRule;
	private static Map<String, Integer> myExpectedData;
	
	@BeforeClass
	public static void initialize() {
		myExpectedData = (new QuantityDataParser(myPrefixFileName + "RateData" + RunAllTests.FILENAME_EXTENSION)).parse();
	}
	
	public TestTimeCondition(String fileName) {
		super(fileName);
		myTestFileName = fileName;
		myFailer.loadTestFile(myTestFileName);
	}
	
	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(myPrefixFileName);
	}
	
	@Override
	public String getPrefixFileName() {
		return myPrefixFileName;
	}
	
	@Override
	public void init() {
	}
	
	@Test
	public void test() {
		myActiveRule = getRuleByName("rule");
		double rate = myActiveRule.getRate();
		if (myExpectedData.get(myTestFileName) == null) {
			myFailer.fail("Unhandled test "  + myTestFileName);
		} else {
			double expected = myExpectedData.get(myTestFileName);
			myFailer.assertDoubleEquals("Rule rate", expected, rate);
		}
	}
	                    
}
