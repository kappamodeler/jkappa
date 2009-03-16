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

	@Override
	public void addConnectedComponents(List<IConnectedComponent> list) {
		mySolution.addConnectedComponents(list);
	}

	@Override
	public void checkSolutionLinesAndAdd(String line, long count) {
		mySolution.checkSolutionLinesAndAdd(line, count);
	}

	@Override
	public void clearSolutionLines() {
		mySolution.clearSolutionLines();
	}

	@Override
	public List<IAgent> cloneAgentsList(List<IAgent> agents) {
		return mySolution.cloneAgentsList(agents);
	}

	@Override
	public KappaSystem getKappaSystem() {
		return mySolution.getKappaSystem();
	}

	@Override
	public List<SolutionLines> getSolutionLines() {
		return mySolution.getSolutionLines();
	}

	@Override
	public void applyRule(RuleApplicationPool pool) {
		mySolution.applyRule(pool);
	}

	@Override
	public RuleApplicationPool prepareRuleApplicationPool(
			List<IInjection> injections) {
		return mySolution.prepareRuleApplicationPool(injections);
	}

	@Override
	public Collection<IAgent> getSuperStorageAgents() {
		return mySolution.getSuperStorageAgents();
	}
	
	@Override
	public Collection<IAgent> getStraightStorageAgents() {
		return mySolution.getStraightStorageAgents();
	}
}
