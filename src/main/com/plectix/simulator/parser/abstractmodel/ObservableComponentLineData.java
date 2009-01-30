package com.plectix.simulator.parser.abstractmodel;

import java.util.List;

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
}
