package com.plectix.simulator.util;

import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.simulator.Simulator;

public class SimulatorRenewer {
	public static final void renew(Simulator simulator) throws Exception {
		simulator.getSimulationData().clearAll();
		new SimulationDataReader(simulator.getSimulationData()).readAndCompile();
		simulator.initializeKappaSystem();
	}
}
