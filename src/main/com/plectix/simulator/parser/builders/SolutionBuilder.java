package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractSolution;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;

public class SolutionBuilder {
	private final SimulationData data;
	
	public SolutionBuilder(SimulationData data) {
		this.data = data;
	}
	
	public ISolution build(AbstractSolution arg, IdGenerator idGenerator) {
		CSolution solution = new CSolution();

		SimulationArguments arguments = data.getSimulationArguments();

		for (List<IAgent> list : arg.getAgents().keySet()) {
			long quant = arg.getAgents().get(list);
			solution.addAgents(list);
			if (arguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
				data.getContactMap().addAgentFromSolution(list);
				data.getContactMap().setSimulationData(data);

			} else {
				for (int i = 1; i < quant; i++) {
					solution.addAgents(solution.cloneAgentsList(list,
							data));
				}
			}
			if (arguments.getSimulationType() == SimulationArguments.SimulationType.COMPILE) {
				for (SolutionLines line : arg.getSolutionLines()) {
					solution.checkSolutionLinesAndAdd(line.getLine(), line.getCount());
				}
			}
		}
		return solution;
	}
}
