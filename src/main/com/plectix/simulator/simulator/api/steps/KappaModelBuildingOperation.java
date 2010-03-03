package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.reader.KappaModelCreator;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.simulator.api.SimulatorState;
import com.plectix.simulator.util.Info.InfoType;

public class KappaModelBuildingOperation extends AbstractOperation<KappaModel> {
	private final SimulationData simulationData;
	private KappaFile kappaFile;
		
	public KappaModelBuildingOperation(SimulationData simulationData, KappaFile kappaFile, InfoType infoType) {
		super(simulationData, OperationType.KAPPA_MODEL_BUILDING);
		this.simulationData = simulationData;
		this.kappaFile = kappaFile;
	}
	
	public KappaModelBuildingOperation(SimulationData simulationData) {
		super(simulationData, OperationType.KAPPA_MODEL_BUILDING);
		this.simulationData = simulationData;
	}
	
	protected KappaModel performDry() throws SimulationDataFormatException {
		if (kappaFile == null) {
			if (simulationData.getKappaInput() == null) {
				throw new NoKappaInputException();
			} else {
				this.kappaFile = simulationData.getKappaInput();
			}
		}
		try {
			KappaModel model = (new KappaModelCreator(simulationData.getSimulationArguments())).createModel(kappaFile);
			simulationData.setInitialModel(model);
			return model;
		} catch (SimulationDataFormatException e) {
			ConsoleOutputManager console = simulationData
					.getConsoleOutputManager();
			SimulationArguments simulationArguments = simulationData.getSimulationArguments();
			console.println("Error in file \""
					+ simulationArguments.getInputFileName() + "\" :");
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
		return simulationData.getInitialModel() != null && latestType.hasSimilarCompilationStage(currentType);
	}

	@Override
	protected KappaModel retrievePreparedResult() {
		return simulationData.getInitialModel();
	}

}
