package com.plectix.simulator.interfaces;

import java.util.*;

import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.solution.StandardRuleApplicationPool;
import com.plectix.simulator.components.solution.SolutionLines;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.KappaSystem;

public interface ISolution {

	public Collection<IAgent> getStraightStorageAgents();
	
	public Collection<IAgent> getSuperStorageAgents();
	
	public List<IConnectedComponent> split();
	
	public void addConnectedComponent(IConnectedComponent component);

//	public void removeAgent(IAgent agent);
//
//	public void addAgent(IAgent agent);

	public void clear();

	public void clearSolutionLines();

	public void addConnectedComponents(List<IConnectedComponent> list);
	
	public void checkSolutionLinesAndAdd(String line, long count);

	public List<SolutionLines> getSolutionLines();

	public List<IAgent> cloneAgentsList(List<IAgent> agents);

	public KappaSystem getKappaSystem();
	
	public RuleApplicationPool prepareRuleApplicationPool(List<IInjection> injections);
	
	public void applyRule(RuleApplicationPool pool);
}
