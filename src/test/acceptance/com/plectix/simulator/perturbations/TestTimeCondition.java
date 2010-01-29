package com.plectix.simulator.perturbations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.QuantityDataParser;

public class TestTimeCondition extends TestPerturbation {

	private static final String separator = File.separator;
	private static final String myPrefixFileName = "test.data" + separator
			+ "perturbations" + separator;
	private String myTestFileName = "";
	private final Failer myFailer = new Failer();
	private Rule myActiveRule;
	private static Map<String, Integer> myExpectedData;

	@BeforeClass
	public static void initialize() throws FileNotFoundException, SimulationDataFormatException, IOException {
		myExpectedData = (new QuantityDataParser(myPrefixFileName + "RateData"
				+ DEFAULT_EXTENSION_FILE)).parse();
	}

	public TestTimeCondition(String fileName, Integer opMode) {
		super(fileName, opMode);
		myTestFileName = fileName;
		myFailer.loadTestFile(myTestFileName);
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		return OperationModeCollectionGenerator
				.generate(getAllTestFileNames(myPrefixFileName),true);
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
			myFailer.fail("Unhandled test " + myTestFileName);
		} else {
			double expected = myExpectedData.get(myTestFileName);
			myFailer.assertDoubleEquals("Rule rate", expected, rate);
		}
	}

}
