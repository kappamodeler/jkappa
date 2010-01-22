package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.util.Info.InfoType;

public class KappaFileCompilationOperation extends AbstractOperation {

	public KappaFileCompilationOperation() {
		super(OperationType.KAPPA_FILE_COMPILATION);
	}
	
	public KappaSystem perform(Simulator simulator, KappaFile kappaFile) throws SimulationDataFormatException {
		return (new SimulationDataReader(simulator.getSimulationData())).compileKappaFile(kappaFile, InfoType.OUTPUT);
	}

}
