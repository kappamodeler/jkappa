package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractSolution;
import com.plectix.simulator.parser.abstractmodel.SolutionLineData;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;

public class SolutionBuilder {
	private final SimulationData data;
	private final SubstanceBuilder mySubstanceBuilder;
	
	public SolutionBuilder(SimulationData data) {
		this.data = data;
		mySubstanceBuilder = new SubstanceBuilder(data);
	}
	
	public ISolution build(AbstractSolution arg) {
		CSolution solution = new CSolution();

		SimulationArguments arguments = data.getSimulationArguments();

		for (SolutionLineData lineData : arg.getAgents()) {
			List<IAgent> list = mySubstanceBuilder.buildAgents(lineData.getAgents());
			long quant = lineData.getCount();
			
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
