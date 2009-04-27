package com.plectix.simulator.parser;

import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.reader.KappaModelCreator;
import com.plectix.simulator.parser.builders.*;
import com.plectix.simulator.parser.exceptions.*;
import com.plectix.simulator.simulator.*;
import com.plectix.simulator.util.Info.InfoType;

public class KappaSystemParser {
	private KappaFile myKappaFile;
	private final SimulationData simulationData;

	public KappaSystemParser(KappaFile file, SimulationData simulationData) {
		myKappaFile = file;
		this.simulationData = simulationData;
	}

	public final void parse(InfoType outputType) throws SimulationDataFormatException {
		simulationData.addInfo(outputType,InfoType.INFO,"--Computing initial state");
		
		KappaModel model = (new KappaModelCreator(
				simulationData.getSimulationArguments())).createModel(myKappaFile);
		simulationData.setInitialModel(model);
		new KappaSystemBuilder(simulationData).build();
	}
}
