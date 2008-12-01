package com.plectix.simulator.components;

import java.util.Random;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.util.Info;

/*package*/ final class CRandomJava implements IRandom {

	private final Random rand;

	public CRandomJava(int seed) {
		if (seed != 0) {
			SimulationMain.getSimulationManager().getSimulationData().addInfo(
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
