package com.plectix.simulator.parser.abstractmodel.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelSolution;
import com.plectix.simulator.parser.abstractmodel.SolutionLineData;
import com.plectix.simulator.parser.abstractmodel.reader.ModelParseHelper;

public class ModelSolutionManager {
	private final ModelSolution solution;

	public ModelSolutionManager(ModelSolution solution) {
		this.solution = solution;
	}
	
	public final void removeSubstance(String componentsRepresentation) {
		Set<SolutionLineData> allMatchedLines = this.findAllLines(componentsRepresentation);
		if (allMatchedLines != null) {
			for (SolutionLineData line : allMatchedLines) {
				solution.removeLineData(line);
			}
		}
	}
	
	private final Set<SolutionLineData> findAllLines(String componentsRepresentation) {
		try {
			Set<SolutionLineData> result = new LinkedHashSet<SolutionLineData>();
			List<ModelAgent> agents = ModelParseHelper.readAgents(true, componentsRepresentation);
			SolutionLineData newLine = new SolutionLineData(agents, -1);
			for (SolutionLineData line : solution.getAgents()) {
				if (this.solutionLinesAreEqual(line, newLine)) {
					result.add(line);
				}
			}
		return result;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private final boolean solutionLinesAreEqual(SolutionLineData line1, SolutionLineData line2) {
		return (line1 == null || line2 == null) ? 
				(line1 == null && line2 == null) :
				(this.getSubstancePartOfLineData(line1).equals(this.getSubstancePartOfLineData(line2)));
	}
	
	private final String getSubstancePartOfLineData(SolutionLineData line) {
		return line.toString().split("\\*")[1].trim();
	}
}
