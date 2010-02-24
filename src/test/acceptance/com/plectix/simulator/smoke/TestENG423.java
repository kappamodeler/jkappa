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

public class TestENG423 {
	@Test
	public final void testCase3b() {
		try {
			String kappaFile = "data" + File.separator + "example.ka";
			this.processCommandLine(
					" --no-compute-qualitative-compression" +
					" --no-compute-quantitative-compression" +
					" --no-dump-iteration-number" +
					" --no-dump-rule-iteration" +
					" --no-enumerate-complexes" +
					" --contact-map " + kappaFile + 
					" --build-influence-map");
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	private void processCommandLine(String commandLine) throws Exception {
		Simulator simulator = new Simulator();
		SimulatorCommandLine simulatorCommandLine = new SimulatorCommandLine(commandLine);
		SimulationArguments arguments = simulatorCommandLine.getSimulationArguments();
		SimulatorInputData inputData = new SimulatorInputData(arguments);
		KappaSystem kappaSystem = simulator.getSimulationData().getKappaSystem();
		OperationManager manager = kappaSystem.getOperationManager();
		CommandLineDefinedWorkflow operation = new CommandLineDefinedWorkflow(simulator, inputData);
		operation.turnOffXMLOutput();
		manager.performSequentially(operation);
	}
}
