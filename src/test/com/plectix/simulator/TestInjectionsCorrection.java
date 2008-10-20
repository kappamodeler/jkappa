package com.plectix.simulator;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
import com.plectix.simulator.components.*;

import com.plectix.simulator.util.*;

public class TestInjectionsCorrection {
	private static Map<String, SortedSet<Long>> myCompareData = new HashMap<String, SortedSet<Long>>();

	private boolean antiFlag = false;

	public TestInjectionsCorrection() {
		myCompareData = (new CorrectionsDataParser(
				"test.data/InjectionsCorrectionData")).parse();
	}

	@Test
	public void test0() {
		MessageConstructor mc = new MessageConstructor();
		for (ObservablesConnectedComponent c : TestInjections.getObservatory()) {
			if (!c.getName().startsWith("q")) {
				if (!testCC(c)) {
					mc.addValue(c.getName());
				}
			}
		}
		if (!mc.isEmpty()) {
			fail(mc.toString());
		}
	}

	public boolean testCC(ObservablesConnectedComponent c) {
		
		SortedSet<Long> solutionLinkingForCurrentObs = new TreeSet<Long>();

		List<CInjection> injectionsList = c.getInjectionsList();
		for (CInjection injection : injectionsList) {
			for (CAgentLink agentLink : injection.getAgentLinkList()) {
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
