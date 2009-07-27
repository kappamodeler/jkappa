package com.plectix.simulator.injections;

import java.io.File;
import java.util.*;

import org.junit.*;

import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.components.CAgentLink;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.*;

public class TestInjectionsCorrection extends TestInjections {
	private static final String separator = File.separator;
	private static Map<String, SortedSet<Long>> myCompareData = new LinkedHashMap<String, SortedSet<Long>>();
	private Failer myFailer = new Failer();
	private boolean antiFlag = false;

	public TestInjectionsCorrection() {
		myCompareData = (new CorrectionsDataParser(
				"test.data" + separator +"InjectionsCorrectionData" + RunAllTests.FILENAME_EXTENSION)).parse();
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
		if (c.getName().startsWith("scary")) {
			return true;
		}
		SortedSet<Long> solutionLinkingForCurrentObs = new TreeSet<Long>();

		Collection<CInjection> injectionsList = c.getInjectionsList();
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
		}
		return (ohohoh);
	}
}
