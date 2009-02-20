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

	@Override
	public void addAgent(IAgent agent) {
		mySolution.addAgent(agent);
	}

	@Override
	public void addConnectedComponent(IConnectedComponent component) {
		mySolution.addConnectedComponent(component);
	}

	@Override
	public void addConnectedComponents(List<IConnectedComponent> list) {
		mySolution.addConnectedComponents(list);
	}

	@Override
	public void clearAgents() {
		mySolution.clearAgents();
	}

	@Override
	public void clearSolutionLines() {
		mySolution.clearSolutionLines();
	}

	@Override
	public Collection<IAgent> getAgents() {
		return mySolution.getAgents();
	}

	@Override
	public void removeAgent(IAgent agent) {
		mySolution.removeAgent(agent);
	}

	@Override
	public List<IConnectedComponent> split() {
		return mySolution.split();
	}

	@Override
	public void checkSolutionLinesAndAdd(String line, long count) {
		mySolution.checkSolutionLinesAndAdd(line, count);
	}
}
