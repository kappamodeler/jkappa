package com.plectix.simulator.simulator;

import java.util.Random;

import com.plectix.simulator.interfaces.IRandom;

final class CRandomJava implements IRandom {

	private final Random rand;

	public CRandomJava(int seed) {
		if(seed == SimulationArguments.DEFAULT_SEED)
			rand = new Random();
		else
			rand = new Random(seed);
	}

	public final double getDouble() {
		return rand.nextDouble();
	}

	public final int getInteger(int limit) {
		return rand.nextInt(limit);
	}
	
	public final void setSeed(long seed) {
		if(seed != SimulationArguments.DEFAULT_SEED)
			rand.setSeed(seed);
	}
}
