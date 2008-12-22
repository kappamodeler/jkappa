package com.plectix.simulator.interfaces;

import java.util.*;

public interface IConnectedComponent {

	public boolean unify(IAgent agent);
	
	public List<IAgent> getAgents();

	public void removeInjection(IInjection injection);

	public void addAgentFromSolutionForRHS(IAgent agent);

	public IInjection getFirstInjection();

	public Collection<IInjection> getInjectionsList();

	public void setRule(IRule rule);

	public boolean isAutomorphism(IAgent agent);

	public void initSpanningTreeMap();

	public IInjection getRandomInjection(IRandom random);

	public void clearAgentsFromSolutionForRHS();

	public IAgent getAgentByIdFromSolution(int agentIdInCC, IInjection injection);

	public IInjection getInjection(IAgent agent);

	public void doPositiveUpdate(List<IConnectedComponent> rightHandSide);

	public void setInjection(IInjection inj);
	
	public List<IAgent> getAgentFromSolutionForRHS();

	public List<IAgent> getAgentsSortedByIdInRule();
	
}
