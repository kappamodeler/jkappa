package com.plectix.simulator.injections;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.staticanalysis.Agent;

@RunWith(value = Parameterized.class)
public class TestInjectionsAgentLinking extends TestInjections {
	private final int number;
	private final int[] obsAgentsOrder;
	private static final int myScaryQuantity = 3;
	private static final int myHalfInitPower = 199 + 609;
	private static final int myStraightInitPower = myScaryQuantity * 100 + 28
			+ myHalfInitPower + 2 - 1136;
	/*
	 * myScaryQuantity 10 + 14 is quantity of agents in scary substances
	 * myHalfInitPower - 1 - previous agents (-1 caused by 2 D() in one place)
	 */
	private static final int mySuperInitPower = myScaryQuantity * 10 + 14
			+ myHalfInitPower + 2 - 1;

	private int shiftStraight(int a) {
		return a + myStraightInitPower;
	}

	private int shiftSuper(int a) {
		return a + mySuperInitPower;
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		Object[][] parameters = new Object[][] {
				{ 1, new int[] { 8, 9, 0, 1, 2, 3, 4, 5, 6, 7 } },
				{ 2, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 } },
				{ 3, new int[] { 1, 0, 2, 3, 4, 5, 6, 7, 8, 9 } },
				{ 4, new int[] { 12, 13, 10, 11, 9, 8, 7, 6, 5, 4, 2, 3, 0, 1 } } };
		return Arrays.asList(parameters);
	}

	public TestInjectionsAgentLinking(int number, int[] obsAgentsOrder) {
		this.number = number;
		this.obsAgentsOrder = obsAgentsOrder;
	}

	private Agent getAgentFromCCById(ConnectedComponentInterface cc, int id) {
		for (Agent agent : cc.getAgents()) {
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

		while ((value >= trimTo) || (value < 0)) {
			value = value - sign * trimTo;
		}
		return value;
	}

	@Test
	public void testScaryAgentLinking() {

		for (ObservableConnectedComponentInterface c : getInitializator()
				.getObservables()) {
			StringBuffer name = new StringBuffer("scary");
			if (number < 10) {
				name.append(0);
			}
			name.append(number);
			if (name.toString().equals(c.getName())) {
				Collection<Injection> injectionsList = c.getInjectionsList();

				for (Injection injection : injectionsList) {
					ConnectedComponentInterface cc = injection
							.getConnectedComponent();
					for (Map.Entry<Integer, Agent> link : injection
							.getCorrespondence().entrySet()) {
						int from = link.getKey();
						Agent agentFrom = getAgentFromCCById(cc, from);
						int to = (int) link.getValue().getId();

						if (injection.isSuper()) {
							int index = to - myHalfInitPower + 1 - 10
									* (number - 1);
							assertEquals(shiftSuper(obsAgentsOrder[index])
									+ (number - 1) * 10, agentFrom.getId());
							break;
						} else {
							int index = to - myHalfInitPower - 100
									* (number - 1);
							index = trimInteger(index, obsAgentsOrder.length);
							assertEquals(shiftStraight(obsAgentsOrder[index])
									+ (number - 1) * 10, agentFrom.getId());
						}
					}
				}

				break;
			}
		}
	}
}
