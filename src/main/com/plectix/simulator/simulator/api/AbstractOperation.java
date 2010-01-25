package com.plectix.simulator.simulator.api;

import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

public abstract class AbstractOperation<E> {
	private final OperationType type;
	private final SimulationData simulationData;
	
	protected AbstractOperation(SimulationData simulationData, OperationType type) {
		this.type = type;
		this.simulationData = simulationData;
	}
	          
	public E perform() throws Exception {
		if (simulationData != null) {
			PlxTimer initializationTimer = new PlxTimer();
			initializationTimer.startTimer();
		
			E result = this.performDry();
		
			SimulationClock.stopTimer(simulationData, InfoType.OUTPUT,
					initializationTimer, "-" + type + ":");
		
			return result;
		} else {
			return this.performDry();
		}
	}

	protected abstract E performDry() throws Exception;
}
