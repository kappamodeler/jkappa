package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.solution.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.parser.abstractmodel.*;
import com.plectix.simulator.simulator.*;

public class SolutionBuilder {
	private final KappaSystem myKappaSystem;
	private final SimulationArguments myArguments;
	private final SubstanceBuilder mySubstanceBuilder;
	private final SimulationData myData;
	
	public SolutionBuilder(SimulationData data) {
		myData = data;
		myKappaSystem = data.getKappaSystem();
		myArguments = data.getSimulationArguments();
		mySubstanceBuilder = new SubstanceBuilder(myKappaSystem);
	}
	
	public ISolution build(AbstractSolution arg) {
		UniversalSolution solution = new UniversalSolution(OperationMode.FIRST);

		for (SolutionLineData lineData : arg.getAgents()) {
			List<IAgent> list = mySubstanceBuilder.buildAgents(lineData.getAgents());
			long quant = lineData.getCount();
			
			solution.addConnectedComponents(SimulationUtils.buildConnectedComponents(list));
//			solution.addAgents(list);

			if (myArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
				//myKappaSystem.getContactMap().addAgentFromSolution(list);
				myKappaSystem.getContactMap().setSimulationData(myData);

			} else {
				for (int i = 1; i < quant; i++) {
					List<IAgent> cloned = SolutionUtils.cloneAgentsList(list, myKappaSystem);
//					solution.addAgents(cloned);
					solution.addConnectedComponents(SimulationUtils.buildConnectedComponents(cloned));
				}
			}
			if (myArguments.getSimulationType() == SimulationArguments.SimulationType.COMPILE) {
				for (SolutionLines line : arg.getSolutionLines()) {
					solution.checkSolutionLinesAndAdd(line.getLine(), line.getCount());
				}
			}
		}
		return solution;
	}
}
