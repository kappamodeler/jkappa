package com.plectix.simulator.parser;

import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.reader.KappaModelCreator;
import com.plectix.simulator.parser.builders.KappaSystemBuilder;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.util.Info.InfoType;

/**
 * KappaSystemParser contains the only method parse(), which
 * parses kappa file, defined in constructor and returns KappaSystem object.
 * @see KappaSystem
 * @see KappaFile
 * @author evlasov
 */
public final class KappaSystemParser {
	private final KappaFile kappaFile;
	private final SimulationData simulationData;

	public KappaSystemParser(KappaFile kappaFile, SimulationData simulationData) {
		this.kappaFile = kappaFile;
		this.simulationData = simulationData;
	}

	/**
	 * Builds KappaSystem object, using given KappaFile. 
	 * @param outputType output mode for logger
	 * @throws SimulationDataFormatException when an error occurred
	 */
	public final void parse(InfoType outputType) throws SimulationDataFormatException {
		KappaModel model = (new KappaModelCreator(
				simulationData.getSimulationArguments())).createModel(kappaFile);
		simulationData.setInitialModel(model);
		new KappaSystemBuilder(simulationData).build();
	}
}
