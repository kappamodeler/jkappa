package com.plectix.simulator.smoke;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.api.steps.CommandLineDefinedWorkflow;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.util.io.XMLOutputOracle;

public class TestENG413 {
	@Test
	public final void test() {
		try {
			String kappaFile = "data" + File.separator + "example.ka";
			KappaSystem kappaSystem = this.processCommandLine("--sim " + kappaFile 
					+ " --event 35 --output-final-state");
			
			Assert.assertTrue("simulation plot contains no data", 
					XMLOutputOracle.finalStateDataIsNotEmpty(kappaSystem));
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	private KappaSystem processCommandLine(String commandLine) throws Exception {
		Simulator simulator = new Simulator();
		SimulatorCommandLine simulatorCommandLine = new SimulatorCommandLine(commandLine);
		SimulationArguments arguments = simulatorCommandLine.getSimulationArguments();
		SimulatorInputData inputData = new SimulatorInputData(arguments);
		KappaSystem kappaSystem = simulator.getSimulationData().getKappaSystem();
		OperationManager manager = kappaSystem.getOperationManager();
		CommandLineDefinedWorkflow operation = new CommandLineDefinedWorkflow(simulator, inputData);
		operation.turnOffXMLOutput();
		manager.perform(operation);
		
		return kappaSystem;
	}
}
