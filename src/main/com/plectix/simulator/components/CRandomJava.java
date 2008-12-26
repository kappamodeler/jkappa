package com.plectix.simulator.components;

import java.util.Random;

import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.util.Info;

/*package*/ final class CRandomJava implements IRandom {

	private final Random rand;

	public CRandomJava(SimulationData data) {
		int seed = data.getSeed();
		if (seed != SimulationData.DEFAULT_SEED) {
			data.addInfo(
					new Info(Info.TYPE_INFO,
							"--Seeding random number generator with given seed "
									+ Integer.valueOf(seed).toString()));
			rand = new Random(seed);
		} else
			rand = new Random();
	}

	public final double getDouble() {
		return rand.nextDouble();
	}

	public final int getInteger(int limit) {
		return rand.nextInt(limit);
	}

}
