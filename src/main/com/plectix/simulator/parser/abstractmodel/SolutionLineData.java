package com.plectix.simulator.parser.abstractmodel;

import java.util.Collections;
import java.util.List;

import com.plectix.simulator.parser.util.StringUtil;

public final class SolutionLineData {
	private final List<ModelAgent> agents;
	private final long counter;
	
	public SolutionLineData(List<ModelAgent> agents, long count) {
		this.agents = agents;
		this.counter = count;
	}

	public final List<ModelAgent> getAgents() {
		return agents;
	}

	public final long getCount() {
		return counter;
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(counter + " * (");
		Collections.sort(agents);
		sb.append(StringUtil.listToString(agents) + ")");
		return sb.toString();
	}
}
