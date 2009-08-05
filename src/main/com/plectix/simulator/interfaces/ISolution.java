package com.plectix.simulator.interfaces;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.*;
import com.plectix.simulator.simulator.KappaSystem;

/**
 * Solution is the place, where all the species, which are
 * taking part in the simulation, stored. Solution can have different structure, depending
 * on the operation mode we use for the simulation.   
 */
public interface ISolution {

	/**
	 * This method returns all connected components from current solution
	 * @return all connected components from current solution
	 */
	public Collection<IConnectedComponent> split();

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
	public List<CAgent> cloneAgentsList(List<CAgent> agents);

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
	public RuleApplicationPool prepareRuleApplicationPool();

	/**
	 * Before any rule can be applied we should fill it's application pool.
	 * Pool must consist of substances we get by watching some list of injections.
	 * This method adds species to the pool, watching by the fixed injection.
	 * @param pool pool to be filled
	 * @param injection injection for being watched
	 */
	public void addInjectionToPool(RuleApplicationPool pool, CInjection injection);
	
	/**
	 * We call this method after any rule application in order to
	 * add all the species, obtained by this application, to solution. 
	 * @param pool application pool we want to extract data from.
	 */
	public void flushPoolContent(RuleApplicationPool pool);

	/**
	 * This method clones given connected component.
	 * @param component given connected component
	 * @return clone of this component
	 */
	public IConnectedComponent cloneConnectedComponent(IConnectedComponent component);
	
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
			List<CAgent> agents);
}
