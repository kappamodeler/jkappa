package com.plectix.simulator.api;

import org.apache.commons.cli.ParseException;

import com.plectix.simulator.controller.SimulatorCallable;
import com.plectix.simulator.controller.SimulatorFutureTask;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;

class CommandLineRunner {
	
	public static final SimulatorFutureTask getSimulatorFutureTask(String[] commandLineArguments) throws ParseException {
		Simulator simulator = new Simulator();
		
		SimulatorCommandLine commandLine = new SimulatorCommandLine(commandLineArguments);
		SimulatorInputData inputData = new SimulatorInputData(commandLine.getSimulationArguments());
		SimulatorCallable callable = new SimulatorCallable(simulator, inputData, null);
		return new SimulatorFutureTask(callable);
	}

}
