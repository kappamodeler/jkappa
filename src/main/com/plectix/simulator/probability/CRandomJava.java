package com.plectix.simulator.probability;

import java.util.Random;

import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.util.Info.InfoType;

/*package*/ final class CRandomJava implements IRandom {

	private final Random rand;

	public CRandomJava(InfoType outputType, SimulationData data) {
		int seed = data.getSimulationArguments().getSeed();
		if (seed == SimulationArguments.DEFAULT_SEED) {
			rand = new Random();
		} else {
			data.addInfo(outputType,InfoType.INFO,
							"--Seeding random number generator with given seed "
									+ Integer.valueOf(seed).toString());
			rand = new Random(seed);
		} 
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
