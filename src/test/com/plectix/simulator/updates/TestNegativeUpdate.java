package com.plectix.simulator.updates;

import java.io.File;
import java.util.*;

import org.junit.runners.Parameterized.Parameters;

import org.junit.*;

import static org.junit.Assert.*;

import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.*;

public class TestNegativeUpdate extends TestUpdate {

	private static final String separator = File.separator;
	private static final String myPrefixFileName = "test.data" + separator
			+ "negativeUpdate" + separator;

	private String myTestFileName = "";
	private Map<String, Integer> myObsInjectionsQuantity;
	private Map<String, Integer> myLHSInjectionsQuantity;
	private Failer myFailer = new Failer();

	public TestNegativeUpdate(String fileName) {
		super(fileName);

		myTestFileName = fileName;
		myFailer.loadTestFile(myTestFileName);
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(myPrefixFileName);
	}

	@Test
	public void testLHS() {
		List<IConnectedComponent> leftHand = getActiveRule().getLeftHandSide();
		for (IConnectedComponent cc : leftHand) {
			Collection<CInjection> componentInjections = cc.getInjectionsList();
			if (!lhsIsEmpty(leftHand)) {

				myFailer.assertEquals("LHS injections",
						myLHSInjectionsQuantity.get(myTestFileName), 
						(int)cc.getInjectionsWeight());
				for (CInjection injection : getCurrentInjectionsList()) {

					assertFalse(cc.getInjectionsList().contains(injection));
					for (CSite site : injection.getChangedSites()) {

						myFailer.assertEmpty("LHS lifts", site.getLift());
					}
				}
			} else {
				myFailer.assertTrue("LHS injections", (componentInjections
						.size() == 1)
						&& (componentInjections
								.contains(CInjection.EMPTY_INJECTION)));
			}
		}
	}

	@Test
	public void testObs() {
		for (IObservablesConnectedComponent cc : getInitializator()
				.getObservables()) {
			myFailer.assertEquals("Observables injections",
					myObsInjectionsQuantity.get(myTestFileName), (int)cc.getInjectionsWeight());
		}
	}

	@Override
	public boolean isDoingPositive() {
		return false;
	}

	@Override
	public String getPrefixFileName() {
		return myPrefixFileName;
	}

	@Override
	public void init() {
		myObsInjectionsQuantity = (new QuantityDataParser(myPrefixFileName
				+ "ObsInjectionsData" + RunAllTests.FILENAME_EXTENSION)).parse();
		myLHSInjectionsQuantity = (new QuantityDataParser(myPrefixFileName
				+ "LHSInjectionsData" + RunAllTests.FILENAME_EXTENSION)).parse();
	}
}
