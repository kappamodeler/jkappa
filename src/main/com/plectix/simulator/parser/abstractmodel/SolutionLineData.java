package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

import com.plectix.simulator.parser.util.StringUtil;

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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(myCount + " * (");
		Collections.sort(myAgents);
		sb.append(StringUtil.listToString(myAgents) + ")");
		return sb.toString();
	}
}
