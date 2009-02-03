package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISolution;

public class AbstractSolution implements IAbstractComponent {
	//Long - counter, List - connectedcomponent
	private List<SolutionLineData> myAgents = new LinkedList<SolutionLineData>();
	private final List<SolutionLines> mySolutionLines = new ArrayList<SolutionLines>();
	
	public final void addAgents(long quant, List<AbstractAgent> agents) {
		if (agents == null || agents.isEmpty() || quant <= 0)
			return;
		myAgents.add(new SolutionLineData(agents, quant));
	}

//	public Map<Long, List<IAgent>> getAgents() {
//		return Collections.unmodifiableMap(myAgents);
//	}
	
	public List<SolutionLineData> getAgents() {
		return myAgents;
	}

	public List<SolutionLines> getSolutionLines() {
		return mySolutionLines;
	}
	
	public final void checkSolutionLinesAndAdd(String line, long count) {
		line = line.replaceAll("[ 	]", "");
		while (line.indexOf("(") == 0) {
			line = line.substring(1);
			line = line.substring(0, line.length() - 1);
		}
		for (SolutionLines sl : mySolutionLines) {
			if (sl.getLine().equals(line)) {
				sl.setCount(sl.getCount() + count);
				return;
			}
		}
		mySolutionLines.add(new SolutionLines(line, count));

	}
}