package com.plectix.simulator.parser.abstractmodel.reader;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.observables.ModelObservables;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

/*package*/ final class ObservablesParagraphReader extends KappaParagraphReader<ModelObservables> {

	public ObservablesParagraphReader(SimulationArguments simulationArguments, 
			AgentFactory agentFactory) {
		super(simulationArguments, agentFactory);
	}

	public final ModelObservables readComponent(KappaFileParagraph observablesParagraph)
			throws ParseErrorException, DocumentFormatException {
		
		ModelObservables observables = new ModelObservables();
		int observableId = 0;

		for (KappaFileLine observableLine : observablesParagraph.getLines()) {
			String line = observableLine.getLine().trim();
			try {
				String name = null;
				if (line.indexOf("'") != -1) {
					line = line.substring(line.indexOf("'") + 1);
					int index = line.indexOf("'");
					if (index != -1) {
						name = line.substring(0, index).trim();
						line = line.substring(index + 1, line.length()).trim();
					}
				}

				if (line.length() == 0) {
					//TODO
					observables.addRuleName(name, observableId);
				} else
					observables.addComponent(parseAgents(line), name, line,
							observableId);//, getArguments().isOcamlStyleObservableNames());
				observableId++;
			} catch (ParseErrorException e) {
				e.setLineDescription(observableLine);
				throw e;
			}
		}
		return observables;
	}
}
