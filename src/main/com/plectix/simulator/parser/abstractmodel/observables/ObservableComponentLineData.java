package com.plectix.simulator.parser.abstractmodel.observables;

import java.util.Collections;
import java.util.List;

import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.util.StringUtil;

public final class ObservableComponentLineData extends ObservablesLineData{
	private final List<ModelAgent> agents;
	private final String name;
	private final String line; 

	public ObservableComponentLineData(List<ModelAgent> agents, String name, String line, int id) {
		super(id);
		this.agents = agents;
		this.name = name;
		this.line = line;
	}
	
	public final String getName() {
		return name;
	}
	
	public final List<ModelAgent> getAgents() {
		return agents;
	}
	
	public final String getLine() {
		return line;
	}
	
	@Override
	public final String toString() {
		Collections.sort(agents);
		StringBuffer sb = new StringBuffer();
		if (name != null) {
			sb.append("'" + name + "' ");
		}
		sb.append(StringUtil.listToString(agents));
		return sb.toString();
	}
}
