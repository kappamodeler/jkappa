package com.plectix.simulator.smiles;

public class FusedRingsCreator {
	private static final String[] AGENT_NAMES = { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J" };

	private static int linkcounter = 0;

	public static void main(String[] args) {
		int count = 0;
		for (int i = 2; i < 10; i++) {
			count += createFusedRings(i);
		}
		System.out.println("\nThere are " + count + " fused rings");
	}

	private static int createFusedRings(int quantity) {
		String ccomp = "";
		int count = 0;
		linkcounter = 0;
		for (int j = 5; j < 16; j++) {
			for (int i = 0; i < quantity; i++) {
				ccomp += addRing(i, j, quantity);
			}
			count++;
			System.out.println(ccomp);
			ccomp = "";
		}
		return count;
	}

	private static String addRing(int number, int vertices, int quantity) {
		String ring = "";
		if (number == 0) {
			for (int i = 0; i < vertices - 2; i++) {
				ring += "A(x!" + ++linkcounter + ",y!" + (linkcounter + 1)
						+ "),";
			}
			ring += "A(x!" + ++linkcounter + ",y!" + (linkcounter + 1) + ",z!"
					+ (linkcounter + 2) + "), ";
			ring += "A(x!0,y!" + ++linkcounter + ",z!" + (linkcounter + 2)
					+ "),";
		} else if (number < quantity - 1) {
			int free = vertices - 4;
			for (int i = 0; i < free / 2; i++) {
				ring += "A(x!" + ++linkcounter + ",y!" + (linkcounter + 2)
						+ "),";
				ring += "A(x!" + ++linkcounter + ",y!" + (linkcounter + 2)
						+ "),";
			}
			if (free % 2 == 1) {
				ring += "A(x!" + ++linkcounter + ",y!" + (linkcounter + 2)
						+ "),";
			}
			ring += "A(x!" + ++linkcounter + ",y!" + (linkcounter + 2) + ",z!"
					+ (linkcounter + 3) + "),";
			ring += "A(x!" + ++linkcounter + ",y!" + ++linkcounter + ",z!"
					+ (linkcounter + 2) + "),";
		} else if (number == quantity - 1) {
			ring += "A(x!" + ++linkcounter + ",y!" + (linkcounter + 2) + "),";
			++linkcounter;
			for (int i = 1; i < vertices - 3; i++) {
				ring += "A(x!" + ++linkcounter + ",y!" + (linkcounter + 1)
						+ "),";
			}
			ring += "A(x!" + ++linkcounter + ",y!"
					+ (linkcounter - vertices + 3) + ")";
		}
		return ring;
	}

}
