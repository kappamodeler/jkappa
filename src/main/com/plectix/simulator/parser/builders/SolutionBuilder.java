package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.solution.SolutionFactory;
import com.plectix.simulator.components.solution.SolutionLine;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.parser.abstractmodel.AbstractSolution;
import com.plectix.simulator.parser.abstractmodel.SolutionLineData;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;

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
	
	public final ISolution build(AbstractSolution arg) {
		ISolution solution = (new SolutionFactory()).produce(myArguments.getOperationMode(), myKappaSystem);
		
		for (SolutionLineData lineData : arg.getAgents()) {
			List<CAgent> list = mySubstanceBuilder.buildAgents(lineData.getAgents());
			long quant = lineData.getCount();
			
			if (myArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP 
					|| myArguments.getSimulationType() == SimulationArguments.SimulationType.GENERATE_MAP) {
				//myKappaSystem.getContactMap().addAgentFromSolution(list);
				myKappaSystem.getContactMap().setSimulationData(myData);
				solution.addInitialConnectedComponents(1, list);
			} else {
				solution.addInitialConnectedComponents(quant, list);
			}
			if (myArguments.getSimulationType() == SimulationArguments.SimulationType.COMPILE) {
				for (SolutionLine line : arg.getSolutionLines()) {
					solution.checkSolutionLinesAndAdd(line.getLine(), line.getCount());
				}
			}
		}
		return solution;
	}
}
