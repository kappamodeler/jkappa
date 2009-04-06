package com.plectix.simulator.interfaces;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;

//TODO should we leave this interface or delete it?
public interface ISolutionComponent {
	/**
	 * This method indicates if this connected component can be put into other 
	 * one which contains given agent. 
	 * @param agent agent from the second component
	 * @return this connected component represents the same substance
	 * as the second one, otherwise <tt>false</tt>
	 */
	public boolean unify(CAgent agent);
	
	public List<CAgent> getAgents();

	public void removeInjection(CInjection injection);

	public void addAgentFromSolutionForRHS(CAgent agent);

	public CInjection getFirstInjection();

	public Collection<CInjection> getInjectionsList();

	public void setRule(CRule rule);

	/**
	 * This method indicates if this connected component represents the same substance
	 * as the second one, which contains given agent. 
	 * @param agent agent from the second component
	 * @return this connected component represents the same substance
	 * as the second one, otherwise <tt>false</tt>
	 */
	public boolean isAutomorphicTo(CAgent agent);

	public void initSpanningTreeMap();

	public CInjection getRandomInjection(IRandom random);

	public void clearAgentsFromSolutionForRHS();

	public CInjection createInjection(CAgent agent);

	public void doPositiveUpdate(List<IConnectedComponent> rightHandSide);

	public void setInjection(CInjection inj);
	
	public List<CAgent> getAgentFromSolutionForRHS();

	public List<CAgent> getAgentsSortedByIdInRule();
	
}
