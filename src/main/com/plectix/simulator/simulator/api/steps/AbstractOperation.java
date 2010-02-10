package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

/*package*/ abstract class AbstractOperation<E> {
	private final OperationType type;
	private final SimulationData simulationData;
	
	private boolean isOn = false;
	private boolean isPerformed = false;
	
	protected AbstractOperation(SimulationData simulationData, OperationType type) {
		this.type = type;
		this.simulationData = simulationData;
	}
	
	protected OperationType getType() {
		return type;
	}
	          
	protected E perform() throws Exception {
		if (simulationData != null) {
			PlxTimer initializationTimer = new PlxTimer();
			initializationTimer.startTimer();
		
			E result = this.performDry();
		
			SimulationClock.stopTimer(simulationData, InfoType.OUTPUT, initializationTimer, "-" + type + ":");
		
			return result;
		} else {
			return this.performDry();
		}
	}

	protected abstract E performDry() throws Exception;

	protected abstract boolean noNeedToPerform();
	
	/**
	 * If operation has no need to be performed, then it's resulting data
	 * is already stored somewhere. This method searches for it
	 */
	protected abstract E retrievePreparedResult() throws Exception;
}
