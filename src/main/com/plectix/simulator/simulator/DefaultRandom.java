package com.plectix.simulator.simulator;

import java.util.Random;

import com.plectix.simulator.interfaces.RandomInterface;

/*package*/ final class DefaultRandom implements RandomInterface {

	private final Random random;

	public DefaultRandom(int seed) {
		if(seed == SimulationArguments.DEFAULT_SEED)
			random = new Random();
		else
			random = new Random(seed);
	}

	public final double getDouble() {
		return random.nextDouble();
	}

	public final int getInteger(int limit) {
		return random.nextInt(limit);
	}
	
	public final void setSeed(long seed) {
		if(seed != SimulationArguments.DEFAULT_SEED)
			random.setSeed(seed);
	}
}
