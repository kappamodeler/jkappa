package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaSystemParser;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
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
	
	protected KappaSystem performDry() throws SimulationDataFormatException {
		try {
			KappaSystemParser parser = new KappaSystemParser(kappaFile,	simulationData);
			return parser.parse(infoType);
		} catch (SimulationDataFormatException e) {
			ConsoleOutputManager console = simulationData
					.getConsoleOutputManager();
			SimulationArguments simulationArguments = simulationData.getSimulationArguments();
			console.println("Error in file \""
					+ simulationArguments.getInputFilename() + "\" :");
			if (console.initialized()) {
				e.printStackTrace(console.getPrintStream());
			}
			
			throw e;
			//TODO HANDLE THIS ERROR
			/*
			 * return new CompiledKappaFile(kappaFile); + exceptions!
			 */
		}
	}

}
