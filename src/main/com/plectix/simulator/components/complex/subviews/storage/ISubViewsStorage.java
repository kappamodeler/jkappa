package com.plectix.simulator.components.complex.subviews.storage;

import java.util.List;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;

public interface ISubViewsStorage{
	
	/**
	 * @param agent given agent
	 * @return <tt>true</tt> if given agent is new, else <tt>false</tt>
	 */
	public boolean addAbstractAgent(CAbstractAgent agent);

	/**
	 * @param view view with some information  
	 * @return SubViews which coherent with agent.<br>
	 * If view = null (no property) then return all SubViews
	 */
	public List<CAbstractAgent> getAllSubViews(CAbstractAgent view);
	
	/**
	 * @param agent agent is SubView from LHS rule. 
	 * @return <tt>true</tt> if rule may be applied
	 */
	public boolean test(CAbstractAgent testView);
	
	
	/**
	 * 
	 * @param oldViews
	 * @param newViews
	 */
	public void burnRule(CAbstractAgent oldView, CAbstractAgent newView);
}
