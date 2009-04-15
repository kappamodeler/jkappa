package com.plectix.simulator.updates;

import java.io.File;
import java.util.*;

import org.junit.runners.Parameterized.Parameters;

import org.junit.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.*;

public class TestPositiveUpdate extends TestUpdate {

	private static final String separator = File.separator;
	private static final String myPrefixFileName = "test.data" + separator
			+ "positiveUpdate" + separator;

	private String myTestFileName = "";
	private Map<String, Integer> myObsInjectionsQuantity;
	private Map<String, Integer> myLHSInjectionsQuantity;
	private Failer myFailer = new Failer();

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(myPrefixFileName);
	}

	public TestPositiveUpdate(String filePath) {
		super(filePath);
		myTestFileName = filePath;
		myFailer.loadTestFile(myTestFileName);
	}

	@Test
	// we can only check for quantity of injections, meaning correct injections
	// setting
	public void testObs() {
		SortedSet<Long> solutionLinkingForCurrentObs = new TreeSet<Long>();

		for (IObservablesConnectedComponent cc : getInitializator()
				.getObservables()) {
			for (CInjection injection : cc.getInjectionsList()) {
				for (CAgentLink agentLink : injection.getAgentLinkList()) {
					solutionLinkingForCurrentObs.add(agentLink.getAgentTo()
							.getId());
				}
			}
//			myFailer.assertEquals("Observables injections",
//					myObsInjectionsQuantity.get(myTestFileName), cc.getCommonPower());
		}

		myFailer.assertSizeEquality("Observatory injections",
				solutionLinkingForCurrentObs, myObsInjectionsQuantity
						.get(myTestFileName));
	}

	@Test
	// the same way, we're checking common injections quantity for all the cc
	// from lhs
	public void testLHS() {
		List<IConnectedComponent> leftHand = getActiveRule().getLeftHandSide();
		for (IConnectedComponent cc : leftHand) {
			Collection<CInjection> componentInjections = cc.getInjectionsList();
			if (!lhsIsEmpty(leftHand)) {
				myFailer.assertEquals("LHS injections",
						myLHSInjectionsQuantity.get(myTestFileName), 
						cc.getCommonPower());
			} else {
				myFailer.assertTrue("LHS injections", (componentInjections
						.size() == 1)
						&& (componentInjections
								.contains(CInjection.EMPTY_INJECTION)));
			}
		}
	}

	@Override
	public boolean isDoingPositive() {
		return true;
	}

	@Override
	public String getPrefixFileName() {
		return myPrefixFileName;
	}

	@Override
	public void init() {
		myObsInjectionsQuantity = (new QuantityDataParser(myPrefixFileName
				+ "ObsInjectionsData")).parse();
		myLHSInjectionsQuantity = (new QuantityDataParser(myPrefixFileName
				+ "LHSInjectionsData")).parse();
	}
}
