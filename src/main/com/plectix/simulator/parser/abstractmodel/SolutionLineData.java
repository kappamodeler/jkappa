package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

public class SolutionLineData {
	private final List<AbstractAgent> myAgents;
	private final long myCount;
	
	public SolutionLineData(List<AbstractAgent> agents, long count) {
		myAgents = agents;
		myCount = count;
	}

	public List<AbstractAgent> getAgents() {
		return myAgents;
	}

	public long getCount() {
		return myCount;
	}
}
