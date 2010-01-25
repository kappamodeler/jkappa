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
import com.plectix.simulator.util.Info.InfoType;

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
			Simulator simulator = new SimulatorInitializationOperation(inputData).perform();
			KappaFile kappaFile = new KappaFileLoadingOperation(simulator.getSimulationData(), "data" + File.separator + "example.ka").perform();
			new KappaFileCompilationOperation(simulator.getSimulationData(), kappaFile, InfoType.OUTPUT).perform();
			new SolutionInitializationOperation(simulator).perform();
			new SimulationOperation(simulator, 10.0).perform();
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
