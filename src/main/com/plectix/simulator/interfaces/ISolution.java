package com.plectix.simulator.interfaces;

import java.util.*;

import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.solution.StandardRuleApplicationPool;
import com.plectix.simulator.components.solution.SolutionLines;
import com.plectix.simulator.components.solution.StraightStorage;
import com.plectix.simulator.components.solution.SuperStorage;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.KappaSystem;

public interface ISolution {

	public List<IConnectedComponent> split();
	
	public void addConnectedComponent(IConnectedComponent component);

	public void clear();

	public void clearSolutionLines();

	public void addConnectedComponents(List<IConnectedComponent> list);
	
	public void checkSolutionLinesAndAdd(String line, long count);

	public List<SolutionLines> getSolutionLines();

	public List<IAgent> cloneAgentsList(List<IAgent> agents);

	public KappaSystem getKappaSystem();
	
	public RuleApplicationPool prepareRuleApplicationPool(List<IInjection> injections);
	
	public void applyRule(RuleApplicationPool pool);

	public IConnectedComponent cloneConnectedComponent(IConnectedComponent component);
	
	public StraightStorage getStraightStorage();
	
	public SuperStorage getSuperStorage();

	public void addInitialConnectedComponents(long quant,
			List<IAgent> agents);
}
