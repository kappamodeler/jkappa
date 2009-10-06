package com.plectix.simulator.smiles;

import java.util.Arrays;
import java.util.List;

public class FusedRingsCreator {
	private static final String[] AGENT_NAMES = { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J" };

	private static final String[] SITE_NAMES = { "x", "y", "z" };
	private static int linkcounter = 0;

	public static void main(String[] args) {
		int count = 0;
		for (int i = 2; i < 9; i++) {
			count += createFusedRings(i);
		}
		// System.out.println("\nThere are " + count + " fused rings");
	}

	private static int createFusedRings(int quantity) {
		StringBuffer ccomp = new StringBuffer();
		int count = 0;
		linkcounter = 0;
		int numberOfAgentsName = 10;
		for (int j = 5; j < 13; j++) {
			for (int i = 0; i < quantity; i++) {
				ccomp.append(addRing(i, j, quantity, numberOfAgentsName));
			}
			count++;
			// System.out.println(ccomp.substring(0, ccomp.length() - 1));
			ccomp = null;
			ccomp = new StringBuffer();
		}
		return count;
	}

	private static String createAgent(int n, List<Integer> links) {
		StringBuffer sb = new StringBuffer("(");
		for (int i = 0; i < links.size(); i++) {
			sb.append(SITE_NAMES[i] + "!" + links.get(i));
			sb.append((i == links.size() - 1) ? ")," : ",");
		}
		return AGENT_NAMES[n] + sb.toString();
	}

	private static String addRing(int number, int vertices, int quantity,
			int numberOfAgentsName) {
		StringBuffer ring = new StringBuffer();
		if (number == 0) {
			linkcounter = 0;
			for (int i = 0; i < vertices - 2; i++) {
				ring.append(createAgent(linkcounter % numberOfAgentsName,
						Arrays.asList(linkcounter, ++linkcounter)));
			}
			ring.append(createAgent(linkcounter % numberOfAgentsName, Arrays
					.asList(linkcounter, ++linkcounter, linkcounter + 1)));
			ring.append(createAgent(linkcounter % numberOfAgentsName, Arrays
					.asList(0, linkcounter, linkcounter + 2)));
		} else if (number < quantity - 1) {
			for (int i = 0; i < vertices - 4; i++) {
				ring.append(createAgent(linkcounter % numberOfAgentsName,
						Arrays.asList(++linkcounter, linkcounter + 2)));
			}

			ring.append(createAgent(linkcounter % numberOfAgentsName, Arrays
					.asList(++linkcounter, linkcounter + 2, linkcounter + 3)));
			ring.append(createAgent(linkcounter % numberOfAgentsName, Arrays
					.asList(++linkcounter, ++linkcounter, linkcounter + 2)));
		} else if (number == quantity - 1) {
			ring.append(createAgent(linkcounter % numberOfAgentsName, Arrays
					.asList(++linkcounter, linkcounter + 2)));
			++linkcounter;
			for (int i = 1; i < vertices - 3; i++) {
				ring.append(createAgent(linkcounter % numberOfAgentsName,
						Arrays.asList(++linkcounter, linkcounter + 1)));
			}
			ring.append(createAgent(linkcounter % numberOfAgentsName, Arrays
					.asList(++linkcounter, linkcounter - vertices + 3)));

		}
		return ring.toString();
	}

}
