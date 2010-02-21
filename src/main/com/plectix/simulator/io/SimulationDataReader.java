package com.plectix.simulator.io;

import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.steps.KappaFileCompilationOperation;
import com.plectix.simulator.simulator.api.steps.KappaFileLoadingOperation;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.util.Info.InfoType;

public class SimulationDataReader {
	private final SimulationData simulationData;

	public SimulationDataReader(SimulationData simulationData) {
		this.simulationData = simulationData;
	}

	private final KappaFile readSimulationFile()	throws Exception {
		OperationManager manager = simulationData.getKappaSystem().getOperationManager();
		return manager.performSequentially(new KappaFileLoadingOperation(simulationData, simulationData.getSimulationArguments().getInputFileName()));
	}

	public final KappaSystem compileKappaFile(KappaFile kappaFile, InfoType outputType) throws Exception {
		OperationManager manager = simulationData.getKappaSystem().getOperationManager();
		return manager.performSequentially(new KappaFileCompilationOperation(simulationData, kappaFile, outputType));
	}

	public final KappaSystem readAndCompile() throws Exception {
		KappaFile file = this.readSimulationFile();
		return this.compileKappaFile(file, InfoType.INFO);
	}
}
