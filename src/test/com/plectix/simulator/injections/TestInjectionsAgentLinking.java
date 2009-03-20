package com.plectix.simulator.injections;

import java.util.*;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;

@RunWith(value = Parameterized.class)
public class TestInjectionsAgentLinking extends TestInjections {
	private static int myNumber = 0;
	private static int[] myObsAgentsOrder;
	private static final int myScaryQuantity = 3;
	private static final int myHalfInitPower = 199;
	private static final int myStraightInitPower = myScaryQuantity * 100 + 28 + myHalfInitPower + 2;
	/* 
	 * myScaryQuantity * 10 + 14 is quantity of agents in scary substances
	 * myHalfInitPower - 1 - previous agents (-1 caused by 2 * D() in one place)
	 */
	private static final int mySuperInitPower = myScaryQuantity * 10 + 14 + myHalfInitPower + 2 - 1;
	
	private int shiftStraight(int a) {
		return a + myStraightInitPower;
	}
	
	private int shiftSuper(int a) {
		return a + mySuperInitPower;
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		Object[][] parameters = new Object[][] {
				{ 1, new int[] { 8, 9, 0, 1, 2, 3, 4, 5, 6, 7}},
				{ 2, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}},
				{ 3, new int[] { 1, 0, 2, 3, 4, 5, 6, 7, 8, 9}},
				{ 4, new int[] { 12, 13, 10, 11, 9, 8, 7, 6, 5, 4, 2, 3, 0, 1}}
			};
		return Collections.unmodifiableList(Arrays.asList(parameters));
	}

	public TestInjectionsAgentLinking(int number, int[] obsAgentsOrder) {
		myNumber = number;
		myObsAgentsOrder = obsAgentsOrder;
	}

	private IAgent getAgentFromCCById(IConnectedComponent cc, int id) {
		for (IAgent agent : cc.getAgents()) {
			if (agent.getIdInConnectedComponent() == id) {
				return agent;
			}
		}
		return null;
	}

	private int trimInteger(int arg, int trimTo) {
		int value = arg;
		int sign = 0;
		
		if (value >= trimTo) {
			sign = 1;
		} else if (value <= 0) {
			sign = -1;
		} else {
			return arg;
		}

		while((value >= trimTo) || (value < 0)) {
			value = value - sign*trimTo;
		}
		return value;
	}
	
	@Test
	public void testScaryAgentLinking() {

		for (IObservablesConnectedComponent c : getInitializator().getObservables()) {
			StringBuffer name = new StringBuffer("scary");
			if (myNumber < 10) {
				name.append(0);
			}
			name.append(myNumber);
			if (name.toString().equals(c.getName())) {
				Collection<IInjection> injectionsList = c.getInjectionsList();

				for (IInjection injection : injectionsList) {
					IConnectedComponent cc = injection.getConnectedComponent();
					for (IAgentLink link : injection.getAgentLinkList()) {
						int from = link.getIdAgentFrom();
						IAgent agentFrom = getAgentFromCCById(cc, from);
						int to = (int) link.getAgentTo().getId();
						

						if (injection.isSuper()) {
							int index = to - myHalfInitPower + 1 - 10 * (myNumber - 1);
							assertEquals(shiftSuper(myObsAgentsOrder[index]) + (myNumber - 1) * 10, 
									agentFrom.getId());
							break;
						} else {
							int index = to - myHalfInitPower - 100 * (myNumber - 1);
							index = trimInteger(index, myObsAgentsOrder.length);
							assertEquals(shiftStraight(myObsAgentsOrder[index]) + (myNumber - 1) * 10, 
									agentFrom.getId());
						}
					}
				}

				break;
			}
		}
	}
}
