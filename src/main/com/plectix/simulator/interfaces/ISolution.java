package com.plectix.simulator.interfaces;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.solution.SolutionLines;
import com.plectix.simulator.components.solution.StraightStorage;
import com.plectix.simulator.components.solution.SuperStorage;
import com.plectix.simulator.simulator.KappaSystem;

public interface ISolution {

	/**
	 * This method returns all connected components from current solution
	 * @return all connected components from current solution
	 */
	public List<IConnectedComponent> split();

	/**
	 * This method adds given connected component to current solution.
	 * @param component given connected component
	 */
	public void addConnectedComponent(IConnectedComponent component);

	/**
	 * This method clears current solution.
	 */
	public void clear();

	/**
	 * This method clears "solution lines" (uses for "--compile" option)
	 * @see SolutionLines
	 */
	public void clearSolutionLines();

	/**
	 * This method adds given connected components to current solution.
	 * @param list given connected components
	 */
	public void addConnectedComponents(List<IConnectedComponent> list);

	/**
	 * This method checks given information and adds if no found.
	 * @param line given line for check
	 * @param count given count of components in line
	 * @see SolutionLines
	 */
	public void checkSolutionLinesAndAdd(String line, long count);

	/**
	 * This method returns "solution lines" (uses for "--compile" option)
	 * @return list of solution lines
	 * @see SolutionLines
	 */
	public List<SolutionLines> getSolutionLines();

	/**
	 * This method clones given agents list.
	 * @param agents given list with agents
	 * @return new list with clones agents
	 */
	public List<CAgent> cloneAgentsList(List<CAgent> agents);

	public KappaSystem getKappaSystem();
	
	public RuleApplicationPool prepareRuleApplicationPool(List<CInjection> injections);

	public void applyRule(RuleApplicationPool pool);

	/**
	 * This method clones given connected component.
	 * @param component given connected component
	 */
	public IConnectedComponent cloneConnectedComponent(IConnectedComponent component);
	
	public StraightStorage getStraightStorage();
	
	public SuperStorage getSuperStorage();

	public void addInitialConnectedComponents(long quant,
			List<CAgent> agents);
}
