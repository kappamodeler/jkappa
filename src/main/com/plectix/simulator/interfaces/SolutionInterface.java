package com.plectix.simulator.interfaces;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.injections.Injection;
import com.plectix.simulator.component.solution.RuleApplicationPoolInterface;
import com.plectix.simulator.component.solution.SolutionLine;
import com.plectix.simulator.component.solution.StraightStorage;
import com.plectix.simulator.component.solution.SuperStorage;
import com.plectix.simulator.simulator.KappaSystem;

/**
 * Solution is the place, where all the species, which are
 * taking part in the simulation, stored. Solution can have different structure, depending
 * on the operation mode we use for the simulation.   
 */
public interface SolutionInterface {

	/**
	 * This method returns all connected components from current solution
	 * @return all connected components from current solution
	 */
	public Collection<ConnectedComponentInterface> split();

	/**
	 * This method clears current solution.
	 */
	public void clear();

	/**
	 * This method clears "solution lines" (used with "--compile" option)
	 * @see SolutionLine
	 */
	public void clearSolutionLines();

	/**
	 * This method checks given information and adds if no found.
	 * @param line given line for check
	 * @param count given count of components in line
	 * @see SolutionLine
	 */
	public void checkSolutionLinesAndAdd(String line, long count);

	/**
	 * This method returns "solution lines" (used with "--compile" option)
	 * @return list of solution lines
	 * @see SolutionLine
	 */
	public List<SolutionLine> getSolutionLines();

	/**
	 * This method clones given agents list.
	 * @param agents given list with agents
	 * @return new list with clones agents
	 */
	public List<Agent> cloneAgentsList(List<Agent> agents);

	/**
	 * Returns KappaSystem object, which this solution belongs to.
	 * @return KappaSystem object
	 */
	public KappaSystem getKappaSystem();
	
	/**
	 * Creates RuleApplicationPool object for the further rule application.
	 * There are some different pool types, and we use different for different operation modes 
	 * @return rule application pool
	 */
	public RuleApplicationPoolInterface prepareRuleApplicationPool();

	/**
	 * Before any rule can be applied we should fill it's application pool.
	 * Pool must consist of substances we get by watching some list of injections.
	 * This method adds species to the pool, watching by the fixed injection.
	 * @param pool pool to be filled
	 * @param injection injection for being watched
	 */
	public void addInjectionToPool(RuleApplicationPoolInterface pool, Injection injection);
	
	/**
	 * We call this method after any rule application in order to
	 * add all the species, obtained by this application, to solution. 
	 * @param pool application pool we want to extract data from.
	 */
	public void flushPoolContent(RuleApplicationPoolInterface pool);

	/**
	 * This method clones given connected component.
	 * @param component given connected component
	 * @return clone of this component
	 */
	public ConnectedComponentInterface cloneConnectedComponent(ConnectedComponentInterface component);
	
	/**
	 * Returns StraightStorage of this solution. 
	 * <br>Note: It cannot be <tt>null</tt> even when using operation mode 4.
	 * In this case this storage is just empty
	 * @return straight storage of this solution
	 */
	public StraightStorage getStraightStorage();
	
	/**
	 * Returns SuperStorage of this solution.
	 * <br>Note: It cannot be <tt>null</tt> even when using operation mode 1.
	 * In this case this storage is just empty
	 * @return super storage of this solution
	 */
	public SuperStorage getSuperStorage();

	/**
	 * This method should be used in initialization stage only. 
	 * It builds and adds first connected components to the solution.
	 * <br>Example: '%init: 10 * A(x!1), B(y!1)'
	 * @param quantity multiplier (10 in the example above)
	 * @param agents list of agents we use to build connected components
	 */
	public void addInitialConnectedComponents(long quantity,
			List<Agent> agents);
}
