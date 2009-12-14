package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.List;

import com.plectix.simulator.parser.BadOptionException;
import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.ParseErrorMessage;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelSolution;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

/*package*/ final class SolutionParagraphReader extends
		KappaParagraphReader<ModelSolution> {
	private final SimulationArguments simulationArguments;
	
	public SolutionParagraphReader(SimulationArguments simulationArguments,
			AgentFactory agentFactory) {
		super(simulationArguments, agentFactory);
		this.simulationArguments = getArguments();
	}

	public final ModelSolution readComponent(KappaFileParagraph solutionParagraph)
			throws SimulationDataFormatException {
		ModelSolution solution = new ModelSolution();
		long count;
		String line;
		String[] result;

		for (KappaFileLine itemDS : solutionParagraph.getLines()) {
			String item = itemDS.getLine();
			count = 1;
			item = item.replace(" ","" );
			for(int i=1;i<item.length();i++){
				if(item.charAt(i)=='*'){
					try{
						Integer.parseInt(item.substring(i-1, i));
					}catch(NumberFormatException e){
					throw new ParseErrorException(itemDS,
							ParseErrorMessage.STAR_IN_SITE_NAME, item);
					}	
				}
			}
			result = item.split("\\*");
			int length = result.length;
			result[0] = result[0].trim();
			count = 1;
			double countInFile = 1;
			if (length != 1) {
				double rescale = simulationArguments.getRescale();
				if (rescale < 0 || Double.isNaN(rescale)) {
					rescale = 1.;
				}

				try {
					countInFile = Double.valueOf(result[0]) * rescale;
				} catch (NumberFormatException e) {
					throw new ParseErrorException(itemDS,
							ParseErrorMessage.INTEGER_EXPECTED, result[0]);
				}

				// if (countInFile - Math.floor(countInFile) < 1e-16)
				long round = Math.round(countInFile);
				if (Math.abs(countInFile - round) < 1e-12) {
					// count = (long) countInFile;
					count = round;
				} else {
					throw new BadOptionException(itemDS,
							ParseErrorMessage.BAD_RESCALE);
				}
			}
			line = result[length - 1].trim();

			// In the future will be create another addAgents to Solution,
			// without parse "count" once "line"
			try {
				if (countInFile > 0) {
					line = line.replaceAll("[ 	]", "");
					List<ModelAgent> listAgent = parseAgents(line);
					solution.addAgents(count, listAgent);

					if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.COMPILE) {
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
