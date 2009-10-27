package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.ParseErrorMessage;
import com.plectix.simulator.parser.abstractmodel.ModelSolution;
import com.plectix.simulator.parser.abstractmodel.SolutionLineData;
import com.plectix.simulator.simulationclasses.solution.SolutionFactory;
import com.plectix.simulator.simulationclasses.solution.SolutionLine;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Agent;

public final class SolutionBuilder {
	private final KappaSystem kappaSystem;
	private final SimulationArguments simulationArguments;
	private final SubstanceBuilder substanceBuilder;
	
	public SolutionBuilder(SimulationData simulationData) {
		kappaSystem = simulationData.getKappaSystem();
		simulationArguments = simulationData.getSimulationArguments();
		substanceBuilder = new SubstanceBuilder(kappaSystem);
	}
	
	public final SolutionInterface build(ModelSolution abstractSolution, MasterSolutionModel masterSolutionModel) throws ParseErrorException {
		SolutionInterface solution = (new SolutionFactory()).produce(simulationArguments.getOperationMode(), kappaSystem);
		for (SolutionLineData lineData : abstractSolution.getAgents()) {
			List<Agent> list = substanceBuilder.buildAgents(lineData.getAgents());
			if(masterSolutionModel != null)
				masterSolutionModel.checkCorrect(list, lineData);

			long quant = lineData.getCount();

			if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP 
					|| simulationArguments.getSimulationType() == SimulationArguments.SimulationType.GENERATE_MAP) {
				kappaSystem.getContactMap().setSimulationData(kappaSystem);
				solution.addInitialConnectedComponents(1, list);
			} else {
				solution.addInitialConnectedComponents(quant, list);
			}
			if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.COMPILE) {
				for (SolutionLine line : abstractSolution.getSolutionLines()) {
					solution.checkSolutionLinesAndAdd(line.getLine(), line.getNumber());
				}
			}
		}
		return solution;
	}
}
