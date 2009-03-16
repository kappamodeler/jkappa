package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;

public final class UniversalSolution implements ISolution {
	private final SolutionAdapter mySolution;
	
	public UniversalSolution(OperationMode mode, KappaSystem system) {
		switch(mode) {
		case FIRST: {
			mySolution = new CSolution(system);
			break;
		}
		case SECOND: {
			// TODO
			mySolution = new CSecondSolution(system);
			break;
		}
		case THIRD: {
			// TODO
			mySolution = new CSolution(system);
			break;
		}
		case FOURTH: {
			// TODO
			mySolution = new CSolution(system);
			break;
		}
		default : {
			// TODO
			mySolution = new CSolution(system);
		}
		}
	}

	public void addAgent(IAgent agent) {
//		mySolution.addAgent(agent);
	}

	public void addConnectedComponent(IConnectedComponent component) {
		mySolution.addConnectedComponent(component);
	}

	public void clear() {
		mySolution.clear();
	}

	public List<IConnectedComponent> split() {
		return mySolution.split();
	}

	public void addConnectedComponents(List<IConnectedComponent> list) {
		mySolution.addConnectedComponents(list);
	}

	public void checkSolutionLinesAndAdd(String line, long count) {
		mySolution.checkSolutionLinesAndAdd(line, count);
	}

	public void clearSolutionLines() {
		mySolution.clearSolutionLines();
	}

	public List<IAgent> cloneAgentsList(List<IAgent> agents) {
		return mySolution.cloneAgentsList(agents);
	}

	public KappaSystem getKappaSystem() {
		return mySolution.getKappaSystem();
	}

	public List<SolutionLines> getSolutionLines() {
		return mySolution.getSolutionLines();
	}

	public void applyRule(RuleApplicationPool pool) {
		mySolution.applyRule(pool);
	}

	public RuleApplicationPool prepareRuleApplicationPool(
			List<IInjection> injections) {
		return mySolution.prepareRuleApplicationPool(injections);
	}

	public Collection<IAgent> getSuperStorageAgents() {
		return mySolution.getSuperStorageAgents();
	}
	
	public Collection<IAgent> getStraightStorageAgents() {
		return mySolution.getStraightStorageAgents();
	}
}
