package com.plectix.simulator.commandline;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.parser.EasyReader;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.simulator.api.steps.AbstractOperation;
import com.plectix.simulator.simulator.api.steps.CommandLineDefinedWorkflow;

@SuppressWarnings("serial")
public class TestCommandLineHandling {
	
	@Test
	public void test() throws Exception {
		String dataDirectory = "test.data" + File.separator + "commandLine" + File.separator;
		EasyReader linesFileReader = new EasyReader(dataDirectory + "commandLines.test");
		EasyReader operationsFileReader = new EasyReader(dataDirectory + "operations.test");
		
		String line = linesFileReader.getLine();
		String operations = operationsFileReader.getLine();
		while (line != null && operations != null) {
			String[] commandLineArgs = line.split(" ");
			String[] operationModeifiers = operations.split(", ");
			
			testCommandLine(commandLineArgs, operationModeifiers);
			
			line = linesFileReader.getLine();
			operations = operationsFileReader.getLine();
		}
	}
	
	private void testCommandLine(String[] commandLine, String[] operations) throws Exception {
		Set<String> operationsSet = new HashSet<String>();
		for (String operation : operations) {
			operationsSet.add(operation);
		}
		Assert.assertEquals(operationsSet, operationsToStrings(this.getResult(commandLine)));
	}
	
	private final Set<String> operationsToStrings(Set<OperationType> set) {
		Set<String> result = new HashSet<String>();
		for (OperationType operation : set) {
			result.add(operation.name());
		}
		return result;
	}
	
	private Set<OperationType> getResult(String[] commandLineArgs) throws Exception {
		SimulatorCommandLine commandLine = new SimulatorCommandLine(commandLineArgs);
		SimulatorInputData inputData = new SimulatorInputData(commandLine.getSimulationArguments());
		final List<AbstractOperation<?>> list = 
			new CommandLineDefinedWorkflow(new Simulator(), inputData).checkoutOperationsSet();
		Set<OperationType> set = new LinkedHashSet<OperationType>() {{
			for (AbstractOperation<?> operation : list) {
				add(operation.getType());
			}
		}};
		return set;
	}
}
