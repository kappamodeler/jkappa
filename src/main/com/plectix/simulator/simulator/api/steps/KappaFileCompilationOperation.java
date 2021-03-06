package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.builders.KappaSystemBuilder;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.simulator.api.SimulatorState;

public class KappaFileCompilationOperation extends AbstractOperation<KappaSystem> {
	private final SimulationData simulationData;
	private KappaModel kappaModel = null;
		
	public KappaFileCompilationOperation(SimulationData simulationData, KappaModel kappaModel) {
		super(simulationData, OperationType.KAPPA_FILE_COMPILATION);
		this.simulationData = simulationData;
		this.kappaModel = kappaModel;
	}
	
	public KappaFileCompilationOperation(SimulationData simulationData) {
		super(simulationData, OperationType.KAPPA_FILE_COMPILATION);
		this.simulationData = simulationData;
	}
	
	protected KappaSystem performDry() throws SimulationDataFormatException {
		if (kappaModel == null) {
			kappaModel = simulationData.getInitialModel();
			if (kappaModel == null) {
				throw new NoKappaInputException();
			}
		}
		
		try {
			KappaSystem kappaSystem = new KappaSystemBuilder(simulationData).build(kappaModel);
			kappaSystem.getState().setKappaFileCompiled();
			return kappaSystem;
		} catch (SimulationDataFormatException e) {
			ConsoleOutputManager console = simulationData
					.getConsoleOutputManager();
			SimulationArguments simulationArguments = simulationData.getSimulationArguments();
			console.println("Error in file \""
					+ simulationArguments.getInputFileName() + "\" :");
			throw e;
			//TODO HANDLE THIS ERROR
			/*
			 * return new CompiledkappaModel(kappaModel); + exceptions!
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
