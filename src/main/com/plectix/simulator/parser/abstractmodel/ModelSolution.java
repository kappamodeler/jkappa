package com.plectix.simulator.parser.abstractmodel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.simulationclasses.solution.SolutionLine;

public final class ModelSolution {
	private final List<SolutionLineData> solutionLinesData = new LinkedList<SolutionLineData>();
	private final List<SolutionLine> solutionLines = new ArrayList<SolutionLine>();
	
	public final void addAgents(long quant, List<ModelAgent> agents) {
		if (agents == null || agents.isEmpty() || quant <= 0) {
			return;
		}
		this.solutionLinesData.add(new SolutionLineData(agents, quant));
	}
	
	public final void removeLineData(SolutionLineData lineData) {
		this.solutionLinesData.remove(lineData);
	}

	public final List<SolutionLineData> getAgents() {
		return solutionLinesData;
	}

	public final List<SolutionLine> getSolutionLines() {
		return solutionLines;
	}
	
	public final void checkSolutionLinesAndAdd(String line, long count) {
		line = line.replaceAll("[ 	]", "");
		while (line.indexOf("(") == 0) {
			line = line.substring(1, line.length() - 1);
		}
		for (SolutionLine sl : solutionLines) {
			if (sl.getLine().equals(line)) {
				sl.setNumber(sl.getNumber() + count);
				return;
			}
		}
		solutionLines.add(new SolutionLine(line, count));

	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		for (SolutionLineData line : solutionLinesData) {
			sb.append("%init: " + line + "\n");
		}
		return sb.toString();
	}
}
