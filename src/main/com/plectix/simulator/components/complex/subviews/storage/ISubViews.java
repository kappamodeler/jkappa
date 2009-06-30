package com.plectix.simulator.components.complex.subviews.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.subviews.CSubViewClass;
import com.plectix.simulator.components.complex.subviews.base.AbstractAction;

public interface ISubViews {

	/**
	 * @param agent
	 *            given agent
	 * @return <tt>true</tt> if given agent is new, else <tt>false</tt>
	 * @throws SubViewsExeption 
	 */
	public boolean addAbstractAgent(CAbstractAgent agent) throws SubViewsExeption;

	/**
	 * @param view
	 *            view with some information
	 * @return SubViews which coherent with agent.<br>
	 *         If view = null (no property) then return all SubViews
	 */
	public List<CAbstractAgent> getAllSubViews(CAbstractAgent view);
	
	public List<CAbstractAgent> getAllSubViews();

	/**
	 * @param agent
	 *            agent is SubView from LHS rule.
	 * @return <tt>true</tt> if rule may be applied
	 * @throws SubViewsExeption 
	 */
	public boolean test(CAbstractAgent testView) throws SubViewsExeption;

	public boolean test(AbstractAction action) throws SubViewsExeption;

	public boolean burnRule(AbstractAction action) throws SubViewsExeption;

	/**
	 * 
	 * @param oldViews
	 * @param newViews
	 * @throws SubViewsExeption 
	 */
	public boolean burnRule(CAbstractAgent oldViews, CAbstractAgent newViews) throws SubViewsExeption;

	public void fillingInitialState(
			Map<Integer, CAbstractAgent> agentNameIdToAgent,
			Collection<CAgent> agents);

	public boolean isAgentFit(CAbstractAgent agent);
	
	/**
	 * This method needs for take into consideration "side effect" 
	 * @param deletedLinks
	 * @param b 
	 * @return
	 */
	public boolean burnBreakAllNeedLinkState(AbstractAction action);
	
	public CSubViewClass getSubViewClass();
}
