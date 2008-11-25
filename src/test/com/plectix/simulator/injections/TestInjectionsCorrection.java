package com.plectix.simulator.injections;

import java.util.*;

import org.junit.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.*;

public class TestInjectionsCorrection extends TestInjections {
	private static Map<String, SortedSet<Long>> myCompareData = new HashMap<String, SortedSet<Long>>();
	private Failer myFailer = new Failer();
	private boolean antiFlag = false;

	public TestInjectionsCorrection() {
		myCompareData = (new CorrectionsDataParser(
				"test.data/InjectionsCorrectionData")).parse();
	}

	@Test
	public void test0() {
		MessageConstructor mc = new MessageConstructor();
		for (IObservablesConnectedComponent c : getInitializator().getObservables()) {
			if (!c.getName().startsWith("q")) {
				if (!testCC(c)) {
					mc.addValue(c.getName());
				}
			}
		}
		if (!mc.isEmpty()) {
			myFailer.failOnMC(mc);
		}
	}

	public boolean testCC(IObservablesConnectedComponent c) {
		
		SortedSet<Long> solutionLinkingForCurrentObs = new TreeSet<Long>();

		Collection<IInjection> injectionsList = c.getInjectionsList();
		for (IInjection injection : injectionsList) {
			for (IAgentLink agentLink : injection.getAgentLinkList()) {
				solutionLinkingForCurrentObs
						.add(agentLink.getAgentTo().getId());
			}
		}
		
		boolean ohohoh = solutionLinkingForCurrentObs.equals(myCompareData
				.get(c.getName()));
		
		// print first failed test info in console
		if (!ohohoh & !antiFlag) {
			antiFlag = true;
			System.out.println("generate (" + c.getName() + ") : "
					+ solutionLinkingForCurrentObs);
			System.out.println("expected (" + c.getName() + ") : "
					+ myCompareData.get(c.getName()));
		}
		return (ohohoh);
	}
}
