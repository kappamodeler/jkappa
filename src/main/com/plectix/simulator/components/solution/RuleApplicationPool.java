package com.plectix.simulator.components.solution;

import com.plectix.simulator.components.CAgent;

/**
 * <p>This is application pool, temporary storage for the one rule application.
 * When rule is ready to be applied with set of injections S, we retrieve all the substances
 * which should react (i.e. where injections from S point to) from solution and place them here.</p>
 * <p>Then we apply rule to the substances within this pool and finally someone calls solution's method
 * ISolution.flushPoolContent(), which adds the result of the reaction 
 * (which is temporary placed here, in pool) back to the solution.</p>
 */
public interface RuleApplicationPool {
	/**
	 * Adds agent to this pool
	 * @param agent agent to be added
	 */
	public void addAgent(CAgent agent);

	/**
	 * Removes agent from this pool
	 * @param agent agent to be removed
	 */
	public void removeAgent(CAgent agent);
	
	/**
	 * @return this pool's storage 
	 */
	public StraightStorage getStorage();

	/**
	 * Clears this pool's storage
	 */
	public void clear();
}
