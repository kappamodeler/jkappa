package com.plectix.simulator.updates;

import java.util.*;

import org.junit.runners.Parameterized.Parameters;

import org.junit.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.*;

public class TestPositiveUpdate extends TestUpdate {
	private String myTestFileName = "";
	private Map<String, Integer> myObsInjectionsQuantity;
	private Map<String, Integer> myLHSInjectionsQuantity;
	private static final String myPrefixFileName = "test.data/positiveUpdate/";
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
	// we can only check for quantity of injections, meaning correct injections setting
	public void testObs() {
		SortedSet<Long> solutionLinkingForCurrentObs = new TreeSet<Long>();

		for (IObservablesConnectedComponent cc : getInitializator().getObservables()) {
			for (IInjection injection : cc.getInjectionsList()) {
				for (IAgentLink agentLink : injection.getAgentLinkList()) {
					solutionLinkingForCurrentObs.add(agentLink.getAgentTo()
							.getId());
				}
			}
		}
		
		myFailer.assertSizeEquality("Observatory injections", solutionLinkingForCurrentObs,
				myObsInjectionsQuantity.get(myTestFileName));
	}

	@Test
	// the same way, we're checking common injections quantity for all the cc from lhs
	public void testLHS() {
		List<IConnectedComponent> leftHand = getActiveRule().getLeftHandSide();
		for (IConnectedComponent cc : leftHand) {
			Collection<IInjection> componentInjections = cc.getInjectionsList();
			if (!lhsIsEmpty(leftHand)) {
				myFailer.assertSizeEquality("LHS injections", componentInjections,
						myLHSInjectionsQuantity.get(myTestFileName));
			} else {
				myFailer.assertTrue("LHS injections", (componentInjections.size() == 1)
						&& (componentInjections.contains(CInjection.EMPTY_INJECTION)));
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
		myObsInjectionsQuantity = (new QuantityDataParser (myPrefixFileName 
				+ "ObsInjectionsData")).parse(); 
		myLHSInjectionsQuantity = (new QuantityDataParser (myPrefixFileName 
				+ "LHSInjectionsData")).parse();
	}
}
