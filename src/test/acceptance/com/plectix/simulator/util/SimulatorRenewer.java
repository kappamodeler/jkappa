package com.plectix.simulator.util;

import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.simulator.Simulator;

public class SimulatorRenewer {
	public static final void renew(Simulator simulator) throws Exception {
		simulator.getSimulationData().clearAll();
		new SimulationDataReader(simulator.getSimulationData()).readAndCompile();
		simulator.initializeKappaSystem();
		
//		SimulationData simulationData = simulator.getSimulationData();
//		simulationData.clearAll();
//		System.out.println("----------------start----------------------");
//		OperationManager manager = simulationData.getKappaSystem().getOperationManager();
//		manager.performSequentially(new KappaFileLoadingOperation(simulationData));
//		manager.perform(new KappaModelBuildingOperation(simulationData));
//		System.out.println("----------------finishlol----------------------");
//		manager.perform(new KappaFileCompilationOperation(simulationData));
//		manager.perform(new SolutionInitializationOperation(simulationData));
		
	}
}