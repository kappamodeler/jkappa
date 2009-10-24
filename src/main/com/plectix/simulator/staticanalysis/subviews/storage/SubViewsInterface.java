package com.plectix.simulator.staticanalysis.subviews.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.subviews.SubViewClass;
import com.plectix.simulator.staticanalysis.subviews.base.AbstractAction;

public interface SubViewsInterface {

	/**
	 * @param agent
	 *            given agent
	 * @return <tt>true</tt> if given agent is new, else <tt>false</tt>
	 * @throws SubViewsExeption
	 */
	public boolean addAbstractAgent(AbstractAgent agent)
			throws SubViewsExeption;

	/**
	 * @param view
	 *            view with some information
	 * @return SubViews which coherent with agent at least one site .<br>
	 *         If view = null (no property) then return null. Use
	 *         getAllSubViews();
	 */
	public List<AbstractAgent> getAllSubViews(AbstractAgent view);

	public List<AbstractAgent> getAllSubViews();

	public List<AbstractAgent> getAllSubViewsCoherent(AbstractAgent view);

	/**
	 * @param agent
	 *            agent is SubView from LHS rule.
	 * @return <tt>true</tt> if rule may be applied
	 * @throws SubViewsExeption
	 */
	public boolean test(AbstractAgent testView) throws SubViewsExeption;

	public boolean test(AbstractAction action) throws SubViewsExeption;

	public boolean burnRule(AbstractAction action) throws SubViewsExeption;

	/**
	 * 
	 * @param oldAgent
	 * @param newAgent
	 * @throws SubViewsExeption
	 */
	public boolean burnRule(AbstractAgent oldAgent, AbstractAgent newAgent)
			throws SubViewsExeption;

	public void fillingInitialState(
			Map<String, AbstractAgent> agentNameToAgent,
			Collection<Agent> agents);

	public boolean isAgentFit(AbstractAgent agent);

	/**
	 * This method needs for take into consideration "side effect"
	 * 
	 * @param deletedLinks
	 * @param b
	 * @return
	 */
	public boolean burnBreakAllNeedLinkState(AbstractAction action);

	public SubViewClass getSubViewClass();

	public boolean isEmpty();
}
