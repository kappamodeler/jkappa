package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaSystemParser;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.simulator.api.SimulatorState;
import com.plectix.simulator.util.Info.InfoType;

public class KappaFileCompilationOperation extends AbstractOperation<KappaSystem> {
	private final SimulationData simulationData;
	private final KappaFile kappaFile;
	private final InfoType infoType;
		
	public KappaFileCompilationOperation(SimulationData simulationData, KappaFile kappaFile, InfoType infoType) {
		super(simulationData, OperationType.KAPPA_FILE_COMPILATION);
		this.simulationData = simulationData;
		this.kappaFile = kappaFile;
		this.infoType = infoType;
	}
	
	public KappaFileCompilationOperation(SimulationData simulationData, InfoType infoType) {
		super(simulationData, OperationType.KAPPA_FILE_COMPILATION);
		this.simulationData = simulationData;
		this.kappaFile = simulationData.getKappaInput();
		this.infoType = infoType;
	}
	
	protected KappaSystem performDry() throws SimulationDataFormatException {
		try {
			KappaSystemParser parser = new KappaSystemParser(kappaFile,	simulationData);
			KappaSystem kappaSystem = parser.parse(infoType);
			kappaSystem.getState().setKappaFileCompilationStatus(true);
			return kappaSystem;
		} catch (SimulationDataFormatException e) {
			ConsoleOutputManager console = simulationData
					.getConsoleOutputManager();
			SimulationArguments simulationArguments = simulationData.getSimulationArguments();
			console.println("Error in file \""
					+ simulationArguments.getInputFilename() + "\" :");
			throw e;
			//TODO HANDLE THIS ERROR
			/*
			 * return new CompiledKappaFile(kappaFile); + exceptions!
			 */
		}
	}

	@Override
	protected boolean noNeedToPerform() {
		SimulatorState state = simulationData.getKappaSystem().getState();
		SimulationType latestType = state.getLatestSimulationType();
		SimulationType currentType = simulationData.getSimulationArguments().getSimulationType();
		return state.isKappaFileCompiled() && latestType.hasSimilarCompilationStage(currentType);
	}

	@Override
	protected KappaSystem retrievePreparedResult() {
		return simulationData.getKappaSystem();
	}

}
