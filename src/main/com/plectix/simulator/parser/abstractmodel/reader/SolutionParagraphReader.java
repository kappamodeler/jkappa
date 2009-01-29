package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.*;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

/*package*/class SolutionParagraphReader extends
		KappaParagraphReader<AbstractSolution> {
	private final SimulationArguments myArguments;
	
	public SolutionParagraphReader(KappaModel model, SimulationArguments arguments,
			AgentFactory factory) {
		super(model, arguments, factory);
		myArguments = getArguments();
	}

	public final AbstractSolution addComponent(KappaFileParagraph solutionParagraph)
			throws ParseErrorException {
		AbstractSolution solution = new AbstractSolution();
		long count;
		String line;
		String[] result;

		for (KappaFileLine itemDS : solutionParagraph.getLines()) {
			String item = itemDS.getLine();
			count = 1;
			result = item.split("\\*");
			int length = result.length;
			result[0] = result[0].trim();
			count = 1;
			double countInFile = 1;
			if (length != 1) {
				double rescale = myArguments.getRescale();
				if (rescale < 0 || Double.isNaN(rescale)) {
					rescale = 1.;
				}

				try {
					countInFile = Double.valueOf(result[0]) * rescale;
				} catch (NumberFormatException e) {
					throw new ParseErrorException(itemDS,
							"Quantity must have numerical format: " + result[0]);
				}

				// if (countInFile - Math.floor(countInFile) < 1e-16)
				long round = Math.round(countInFile);
				if (Math.abs(countInFile - round) < 1e-12) {
					// count = (long) countInFile;
					count = round;
				} else {
					throw new ParseErrorException(itemDS,
							"Integer quantity expected, use '--rescale' option");
				}
			}
			line = result[length - 1].trim();

			// In the future will be create another addAgents to Solution,
			// without
			// parse "count" once "line"
			try {
				if (countInFile > 0) {
					line = line.replaceAll("[ 	]", "");
					List<AbstractAgent> listAgent = parseAgent(line);
					solution.addAgents(count, listAgent);

					if (myArguments.getSimulationType() == SimulationArguments.SimulationType.COMPILE) {
						solution.checkSolutionLinesAndAdd(line, count);
					}
				}
			} catch (ParseErrorException e) {
				e.setLineDescription(itemDS);
				throw e;
			}
		}
		return solution;
	}
}
