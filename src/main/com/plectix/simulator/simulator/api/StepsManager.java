package com.plectix.simulator.simulator.api;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.api.steps.KappaFileCompilationOperation;
import com.plectix.simulator.simulator.api.steps.ReturnErrorOperation;
import com.plectix.simulator.simulator.api.steps.SimulatorInitializationOperation;
import com.plectix.simulator.simulator.api.steps.SolutionInitializationOperation;
import com.plectix.simulator.simulator.api.steps.SubviewsComputationOperation;

public class StepsManager {
	private static Map<OperationType, AbstractOperation> defaultOperations = new LinkedHashMap<OperationType, AbstractOperation>();
	
	static {
//		PrintStream defaultConsoleStream = System.out;
//		SimulatorInputData inputData = new SimulatorInputData(
//				new SimulationArguments(), defaultConsoleStream);
//		
//		defaultOperations.put(OperationType.SIMULATOR_INITIALIZATION, new SimulatorInitializationOperation(inputData));
//		//TODO or do something else to retrieve file name
//		defaultOperations.put(OperationType.KAPPA_FILE_LOADING, new ReturnErrorOperation("You should specify Kappa source"));
//		defaultOperations.put(OperationType.KAPPA_FILE_COMPILATION, new KappaFileCompilationOperation());
//		defaultOperations.put(OperationType.SOLUTION_INITIALIZATION, new SolutionInitializationOperation());
//		defaultOperations.put(OperationType.SUBVIEWS, new SubviewsComputationOperation());
	}
	
	
}
