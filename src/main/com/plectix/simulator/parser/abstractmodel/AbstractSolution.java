package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.interfaces.IAgent;

public class AbstractSolution implements IAbstractComponent {
	private Map<Long, List<IAgent>> myAgents = new HashMap<Long, List<IAgent>>();
	private final List<SolutionLines> mySolutionLines = new ArrayList<SolutionLines>();
	
	public final void addAgents(long quant, List<IAgent> agents) {
		if (agents == null || agents.isEmpty())
			return;
		myAgents.put(quant, agents);
	}

	public Map<Long, List<IAgent>> getAgents() {
		return Collections.unmodifiableMap(myAgents);
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
