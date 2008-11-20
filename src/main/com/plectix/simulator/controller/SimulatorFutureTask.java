package com.plectix.simulator.controller;

import java.util.concurrent.FutureTask;

public class SimulatorFutureTask extends FutureTask<SimulatorResultsData> {

	private SimulatorCallable simulatorCallable;
	
	public SimulatorFutureTask(SimulatorCallable callable) {
		super(callable);
		
		if (callable == null) {
			throw new RuntimeException("Callable can not be null!");
		}
		
		simulatorCallable = callable;
	}
	
	public SimulatorInterface getSimulator() {
		return simulatorCallable.getSimulator();
	}

}
