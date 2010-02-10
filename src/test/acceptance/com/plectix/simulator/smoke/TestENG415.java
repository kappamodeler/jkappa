package com.plectix.simulator.smoke;

import java.io.File;

import org.junit.Test;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.BackingUpPrintStream;

public class TestENG415 {
	private final String generateCommand(String fileName) {
		final String commandLine = "--compile ";
		final String sourcePath = "\"test.data" + File.separator 
				+ "smoke_test" + File.separator 
				+ "source" + File.separator;
		return commandLine + sourcePath + fileName;
	}
	
	private final BackingUpPrintStream getCompiledContent(String fileName) throws Exception {
		BackingUpPrintStream ps = new BackingUpPrintStream();
		SimulatorCommandLine commandLine = new SimulatorCommandLine(this.generateCommand(fileName));
		SimulatorInputData inputData = new SimulatorInputData(commandLine.getSimulationArguments(), ps);
		new Simulator().run(inputData);
		return ps;
	}
	
	@Test
	public void test() {
		try {
			BackingUpPrintStream fileWithoutSpaces = this.getCompiledContent("eng415.test\"");
			BackingUpPrintStream fileWithSpaces = this.getCompiledContent("eng415 file name with spaces.test\"");
			
			if (fileWithSpaces.equals(fileWithoutSpaces)) {
				org.junit.Assert.fail();
			}
		} catch(Exception e) {
			org.junit.Assert.fail("Exception thrown from simulator : " + e.getMessage());
		}
	}
}
