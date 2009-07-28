package com.plectix.simulator.parser.abstractmodel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.components.solution.SolutionLines;

public class AbstractSolution implements IAbstractComponent {
	//Long - counter, List - connectedcomponent
	private List<SolutionLineData> myAgents = new LinkedList<SolutionLineData>();
	private final List<SolutionLines> mySolutionLines = new ArrayList<SolutionLines>();
	
	public final void addAgents(long quant, List<AbstractAgent> agents) {
		if (agents == null || agents.isEmpty() || quant <= 0)
			return;
		myAgents.add(new SolutionLineData(agents, quant));
	}

	public List<SolutionLineData> getAgents() {
		return myAgents;
	}

	public List<SolutionLines> getSolutionLines() {
		return mySolutionLines;
	}
	
	public final void checkSolutionLinesAndAdd(String line, long count) {
		line = line.replaceAll("[ 	]", "");
		while (line.indexOf("(") == 0) {
			line = line.substring(1, line.length() - 1);
		}
		for (SolutionLines sl : mySolutionLines) {
			if (sl.getLine().equals(line)) {
				sl.setCount(sl.getCount() + count);
				return;
			}
		}
		mySolutionLines.add(new SolutionLines(line, count));

	}
	
	
	//--------------toString-----------------------------------
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (SolutionLineData line : myAgents) {
			sb.append("%init: " + line + "\n");
		}
		return sb.toString();
	}
}
