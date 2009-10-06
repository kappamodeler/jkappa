package com.plectix.simulator.simulator;

import com.plectix.simulator.controller.SimulatorFactoryInterface;
import com.plectix.simulator.controller.SimulatorInterface;

public final class DefaultSimulatorFactory implements SimulatorFactoryInterface {
	public final SimulatorInterface createSimulator() {
		return new Simulator();
	}
}
