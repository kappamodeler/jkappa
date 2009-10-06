package com.plectix.simulator.parser.abstractmodel.reader;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelStories;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

/*package*/ final class StoriesParagraphReader extends KappaParagraphReader<ModelStories> {
	public StoriesParagraphReader(SimulationArguments arguments, AgentFactory factory) {
		super(arguments, factory);
	}

	public final ModelStories readComponent(KappaFileParagraph storiesParagraph)
			throws ParseErrorException {
		ModelStories stories = new ModelStories();
		for (KappaFileLine storyLine : storiesParagraph.getLines()) {
			String line = storyLine.getLine().trim();
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
