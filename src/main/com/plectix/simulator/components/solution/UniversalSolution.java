package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.interfaces.*;

public final class UniversalSolution implements ISolution {
	private final PhysicalSolution mySolution;
	
	public UniversalSolution(OperationMode mode) {
		switch(mode) {
		case FIRST: {
			mySolution = new CSolution();
			break;
		}
		case SECOND: {
			// TODO
			mySolution = new CSolution();
			break;
		}
		case THIRD: {
			// TODO
			mySolution = new CSolution();
			break;
		}
		case FOURTH: {
			// TODO
			mySolution = new CSolution();
			break;
		}
		default : {
			// TODO
			mySolution = new CSolution();
		}
		}
	}

	public void addAgent(IAgent agent) {
		mySolution.addAgent(agent);
	}

	public void addConnectedComponent(IConnectedComponent component) {
		mySolution.addConnectedComponent(component);
	}

	public void addConnectedComponents(List<IConnectedComponent> list) {
		mySolution.addConnectedComponents(list);
	}

	public void clearAgents() {
		mySolution.clearAgents();
	}

	public void clearSolutionLines() {
		mySolution.clearSolutionLines();
	}

	public Collection<IAgent> getAgents() {
		return mySolution.getAgents();
	}

	public void removeAgent(IAgent agent) {
		mySolution.removeAgent(agent);
	}

	public List<IConnectedComponent> split() {
		return mySolution.split();
	}

	public void checkSolutionLinesAndAdd(String line, long count) {
		mySolution.checkSolutionLinesAndAdd(line, count);
	}
}
