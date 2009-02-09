package com.plectix.simulator.parser.abstractmodel.reader;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.abstractmodel.*;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

/*package*/class StoriesParagraphReader extends
		KappaParagraphReader<AbstractStories> {
	public StoriesParagraphReader(KappaModel model,
			SimulationArguments arguments, AgentFactory factory) {
		super(model, arguments, factory);
	}

	public final AbstractStories readComponent(KappaFileParagraph storiesParagraph)
			throws ParseErrorException {
		AbstractStories stories = new AbstractStories();

		for (KappaFileLine itemDS : storiesParagraph.getLines()) {
			String line = itemDS.getLine().trim();

			if (line.indexOf("'") != -1) {
				line = line.substring(line.indexOf("'") + 1);
				String name = line.substring(0, line.indexOf("'")).trim();
				line = line.substring(line.indexOf("'") + 1, line.length())
						.trim();
				stories.addName(name);
			}
		}
		return stories;
	}
}
