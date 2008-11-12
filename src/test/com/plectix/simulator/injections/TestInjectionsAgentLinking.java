package com.plectix.simulator.injections;

import java.util.*;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.*;

@RunWith(value = Parameterized.class)
public class TestInjectionsAgentLinking extends TestInjections {
	private static int myNumber = 0;
	private static int[] myObsAgentsOrder;
	private static final int myScaryQuantity = 3;
	private static final int myHalfInitPower = 199;
	private static final int myInitPower = myScaryQuantity * 100 + 28 + myHalfInitPower + 3;
		
	private static int[] shift(int[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] += myInitPower;
		}
		return a;
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		Object[][] parameters = new Object[][] {
				{ 1, shift(new int[] { 8, 9, 0, 1, 2, 3, 4, 5, 6, 7})},
				{ 2, shift(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9})},
				{ 3, shift(new int[] { 1, 0, 2, 3, 4, 5, 6, 7, 8, 9})},
				{ 4, shift(new int[] { 12, 13, 10, 11, 9, 8, 7, 6, 5, 4, 2, 3, 0, 1})}
			};
		return Collections.unmodifiableList(Arrays.asList(parameters));
	}

	public TestInjectionsAgentLinking(int number, int[] obsAgentsOrder) {
		myNumber = number;
		myObsAgentsOrder = obsAgentsOrder;
	}

	private CAgent getAgentFromCCById(CConnectedComponent cc, int id) {
		for (CAgent agent : cc.getAgents()) {
			if (agent.getIdInConnectedComponent() == id) {
				return agent;
			}
		}
		return null;
	}

	@Test
	public void testScaryAgentLinking() {

		for (ObservablesConnectedComponent c : getInitializator().getObservables()) {
			StringBuffer name = new StringBuffer("scary");
			if (myNumber < 10) {
				name.append(0);
			}
			name.append(myNumber);
			if (name.toString().equals(c.getName())) {
				Collection<CInjection> injectionsList = c.getInjectionsList();

				int run = 0;
				for (CInjection injection : injectionsList) {
					CConnectedComponent cc = injection.getConnectedComponent();
					for (CAgentLink link : injection.getAgentLinkList()) {
						int from = link.getIdAgentFrom();
						CAgent agentFrom = getAgentFromCCById(cc, from);
						int to = (int) link.getAgentTo().getId();
						int index = to - myObsAgentsOrder.length * run - myHalfInitPower
								- 100 * (myNumber - 1);

						assertEquals(myObsAgentsOrder[index] + (myNumber - 1) * 10, 
								agentFrom.getId());
					}
					run++;
				}

				break;
			}
		}
	}
}
