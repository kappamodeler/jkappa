package com.plectix.simulator;

import java.util.*;

import org.junit.runners.Parameterized.Parameters;

import org.junit.*;

import static org.junit.Assert.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.util.*;

public class TestNegativeUpdate extends UpdateDirectoryTestsRunner {

	private String myTestFileName = "";
	private Map<String, Integer> myObsInjectionsQuantity;
	private Map<String, Integer> myLHSInjectionsQuantity;
	private static final String myPrefixFileName = "test.data/negativeUpdate/";
	private Failer myFailer = new Failer(); 
		
	public TestNegativeUpdate(String fileName) {
		super(fileName);
		myTestFileName = fileName;
		myFailer.loadTestFile(myTestFileName);
	}
	
	@Parameters
	public static Collection<Object[]> regExValues() {
		return UpdateDirectoryTestsRunner.getAllTestFileNames(myPrefixFileName);
	}
	
	@Test
	public void testLHS() {
		List<CConnectedComponent> leftHand = getActiveRule().getLeftHandSide();
		for (CConnectedComponent cc : leftHand) {
			List<CInjection> componentInjections = cc.getInjectionsList();
			if (!RunUpdateTests.lhsIsEmpty(leftHand)) {
			
				myFailer.assertSizeEquality("LHS injections", componentInjections,
						myLHSInjectionsQuantity.get(myTestFileName));
				for (CInjection injection : getCurrentInjectionsList()) {
				
					assertFalse(cc.getInjectionsList().contains(injection));
					for (CSite site : injection.getChangedSites()) {
						
						myFailer.assertEmpty("LHS lifts", site.getLift());
					}
				}
			} else {
				myFailer.assertTrue("LHS injections", (componentInjections.size() == 1)
						&& (componentInjections.contains(CConnectedComponent.EMPTY_INJECTION)));
			}
		}
	}
	
	@Test
	public void testObs() {
		for (ObservablesConnectedComponent cc : getObservables()) {
			List<CInjection> componentInjections = cc.getInjectionsList();
			
			myFailer.assertSizeEquality("Observables injections",
					componentInjections, myObsInjectionsQuantity.get(myTestFileName));
		}			
	}
	
	public boolean isDoingPositive() {
		return false;
	}
	
	public String getPrefixFileName() {
		return myPrefixFileName;
	}
	
	public void init() {
		myObsInjectionsQuantity = (new InjectionsQuantityDataParser (myPrefixFileName 
				+ "ObsInjectionsData")).parse(); 
		myLHSInjectionsQuantity = (new InjectionsQuantityDataParser (myPrefixFileName 
				+ "LHSInjectionsData")).parse();
	}
}
