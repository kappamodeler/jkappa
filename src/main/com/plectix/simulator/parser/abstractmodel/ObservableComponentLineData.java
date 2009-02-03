package com.plectix.simulator.parser.abstractmodel;

import java.util.Collections;
import java.util.List;

import com.plectix.simulator.parser.util.StringUtil;

public class ObservableComponentLineData extends ObservablesLineData{
	private final List<AbstractAgent> myAgents;
	private final String myName;
	private final String myLine; 

	public ObservableComponentLineData(List<AbstractAgent> agents, String name, String line, int id) {
		super(id);
		myAgents = agents;
		myName = name;
		myLine = line;
	}
	
	public String getName() {
		return myName;
	}
	
	public List<AbstractAgent> getAgents() {
		return myAgents;
	}
	
	public String getLine() {
		return myLine;
	}
	
	//-----------------toString--------------
	
	public String toString() {
		Collections.sort(myAgents);
		StringBuffer sb = new StringBuffer();
		if (myName != null) {
			sb.append("'" + myName + "' ");
		}
		sb.append(StringUtil.listToString(myAgents));
		return sb.toString();
	}
}
