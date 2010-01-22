package com.plectix.simulator.simulator.api.steps;

import java.io.IOException;

import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class KappaFileLoadingOperation extends AbstractOperation {
	public KappaFileLoadingOperation() {
		super(OperationType.KAPPA_FILE_LOADING);
	}
	
	public KappaFile perform(Simulator simulator, String kappaFileId) throws RuntimeException, SimulationDataFormatException, IOException {
		SimulationArguments arguments = simulator.getSimulationData().getSimulationArguments();
		arguments.setInputFilename(kappaFileId);
		return (new SimulationDataReader(simulator.getSimulationData())).readSimulationFile();
	}
}
