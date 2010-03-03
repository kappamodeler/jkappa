package com.plectix.simulator.simulator.api.steps;

import java.util.LinkedHashSet;
import java.util.Set;

import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

@SuppressWarnings("serial")
public abstract class AbstractOperation<E> {
	private final OperationType type;
	final SimulationData simulationData;
	
	private static Set<OperationType> silentSteps = new LinkedHashSet<OperationType>() {{
		add(OperationType.DUMP_HELP);
		add(OperationType.DUMP_VERSION);
		add(OperationType.SIMULATOR_INITIALIZATION);
	}};
	
	protected AbstractOperation(SimulationData simulationData, OperationType type) {
		this.type = type;
		this.simulationData = simulationData;
	}
	
	public OperationType getType() {
		return type;
	}
	          
	protected E perform() throws Exception {
		if (simulationData != null && !this.isSilent()) {
			PlxTimer initializationTimer = new PlxTimer();
			initializationTimer.startTimer();
		
			E result = this.performDry();
		
			SimulationClock.stopTimer(simulationData, InfoType.OUTPUT, initializationTimer, "-" + type + ":");
		
			return result;
		} else {
			return this.performDry();
		}
	}

	private boolean isSilent() {
		return silentSteps.contains(this.getType());
	}

	protected abstract E performDry() throws Exception;

	protected abstract boolean noNeedToPerform();
	
	/**
	 * If operation has no need to be performed, then it's resulting data
	 * is already stored somewhere. This method searches for it
	 */
	protected abstract E retrievePreparedResult() throws Exception;
}
