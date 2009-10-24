package com.plectix.simulator.interfaces;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Rule;

/**
 * Entity that can be contained in solution. It is obviously connected component
 */
public interface SolutionElementInterface {
	/**
	 * This method indicates if this connected component can be put into other 
	 * one which contains given agent. 
	 * @param agent agent from the second component
	 * @return this connected component represents the same substance
	 * as the second one, otherwise <tt>false</tt>
	 */
	public boolean unify(Agent agent);
	
	public List<Agent> getAgents();

	public void removeInjection(Injection injection);

	public void addAgentFromSolutionForRHS(Agent agent);

	public Injection getFirstInjection();

	public Collection<Injection> getInjectionsList();

	public void setRule(Rule rule);

	/**
	 * This method indicates if this connected component represents the same substance
	 * as the second one, which contains given agent. 
	 * @param agent agent from the second component
	 * @return this connected component represents the same substance
	 * as the second one, otherwise <tt>false</tt>
	 */
	public boolean isAutomorphicTo(Agent agent);

	public void initSpanningTreeMap();

	public Injection getRandomInjection();

	public void clearAgentsFromSolutionForRHS();

	public Injection createInjection(Agent agent);

	public void doPositiveUpdate(List<ConnectedComponentInterface> rightHandSide);

	public void setInjection(Injection inj);
	
	public List<Agent> getAgentFromSolutionForRHS();

	public List<Agent> getAgentsSortedByIdInRule();
	
}
