package com.plectix.simulator.parser.abstractmodel;

import java.util.List;

public class ObservableComponentLineData extends ObservablesLineData{
	private List<AbstractAgent> myAgents;
	private final String myName;

	public ObservableComponentLineData(List<AbstractAgent> agents, String name, int id) {
		super(id);
		myAgents = agents;
		myName = name;
	}
	
	public String getName() {
		return myName;
	}
	
	public List<AbstractAgent> getAgents() {
		return myAgents;
	}
}
