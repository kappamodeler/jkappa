package com.plectix.simulator.simulator.api;

import java.io.File;

import org.apache.commons.cli.ParseException;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.api.steps.KappaFileCompilationOperation;
import com.plectix.simulator.simulator.api.steps.KappaFileLoadingOperation;
import com.plectix.simulator.simulator.api.steps.SimulationOperation;
import com.plectix.simulator.simulator.api.steps.SimulatorInitializationOperation;
import com.plectix.simulator.simulator.api.steps.SolutionInitializationOperation;

public abstract class SimulatorAPI {
	/*
	 * resetFile = loadFile
	 * reset
	 */
	
	public static void main(String[] args) {
		SimulatorInputData inputData;
		try {
//			inputData = new SimulatorInputData(new SimulatorCommandLine(args).getSimulationArguments(), System.out);
//			Simulator simulator = SimulatorAPI.createSimulator(inputData);
//			SimulatorAPI.loadKappaFile(simulator, new File("data" + File.separator + "example.ka"));
//			SimulatorAPI.initializeKappaSystem(simulator);
//			SimulatorAPI.runSimulation(simulator, 10);
//			simulator.outputCurrentSimulationDataToXML();
			
			inputData = new SimulatorInputData(new SimulatorCommandLine(args).getSimulationArguments(), System.out);
			Simulator simulator = new SimulatorInitializationOperation().perform(inputData);
			KappaFile kappaFile = new KappaFileLoadingOperation().perform(simulator, "data" + File.separator + "example.ka");
			new KappaFileCompilationOperation().perform(simulator, kappaFile);
			new SolutionInitializationOperation().perform(simulator);
			new SimulationOperation().performTimeSimulation(simulator, 10);
			simulator.outputCurrentSimulationDataToXML();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
