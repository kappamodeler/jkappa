package com.plectix.simulator.parser.abstractmodel.reader;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.observables.AbstractObservables;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

/*package*/class ObservablesParagraphReader extends
		KappaParagraphReader<AbstractObservables> {

	public ObservablesParagraphReader(KappaModel model, SimulationArguments arguments,
			AgentFactory factory) {
		super(model, arguments, factory);
	}

	public final AbstractObservables readComponent(KappaFileParagraph observablesParagraph)
			throws ParseErrorException, DocumentFormatException {
		AbstractObservables observables = new AbstractObservables();
		int obsId = 0;

		for (KappaFileLine itemDS : observablesParagraph.getLines()) {
			String line = itemDS.getLine().trim();
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
					observables.addRuleName(name, obsId);
				} else
					observables.addComponent(parseAgent(line), name, line,
							obsId);//, getArguments().isOcamlStyleObservableNames());
				obsId++;
			} catch (ParseErrorException e) {
				e.setLineDescription(itemDS);
				throw e;
			}
		}
		return observables;
	}
}
