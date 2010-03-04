package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.List;

import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

public class ModelParseHelper {
	/**
	 * This method tries to read a line as a description of substances in kappa and returns
	 * a list of model agents which was read in the case of success.
	 * @param allowIncompletes <tt>true</tt> if and only if we allow to create incomplete substances
	 * @param line a string we try to interpret as a set of connected components
	 * @return list of ModelAgent objects as a result of the parsing
	 * @throws IncompletesDisabledException if incomplete substance was obtained but not allowed 
	 * @throws SimulationDataFormatException if the parsing went wrong
	 */
	public static final List<ModelAgent> readAgents(boolean allowIncompletes,
			String line) throws IncompletesDisabledException,
			SimulationDataFormatException {
		return new SolutionParagraphReader(new SimulationArguments(),
				new AgentFactory(allowIncompletes)).parseAgents(line);
	}
}
