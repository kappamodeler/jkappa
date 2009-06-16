package com.plectix.simulator.simulator;

import java.util.Random;

import com.plectix.simulator.interfaces.IRandom;

/*package*/ final class CRandomJava implements IRandom {

	private final Random rand;

	public CRandomJava(int seed) {
		rand = new Random(seed);
	}

	public final double getDouble() {
		return rand.nextDouble();
	}

	public final int getInteger(int limit) {
		return rand.nextInt(limit);
	}
	
	public final void setSeed(long seed) {
		rand.setSeed(seed);
	}
}
