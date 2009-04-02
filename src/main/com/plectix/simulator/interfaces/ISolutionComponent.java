package com.plectix.simulator.interfaces;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;

public interface ISolutionComponent {
	public boolean unify(CAgent agent);
	
	public List<CAgent> getAgents();

	public void removeInjection(CInjection injection);

	public void addAgentFromSolutionForRHS(CAgent agent);

	public CInjection getFirstInjection();

	public Collection<CInjection> getInjectionsList();

	public void setRule(CRule rule);

	public boolean isAutomorphism(CAgent agent);

	public void initSpanningTreeMap();

	public CInjection getRandomInjection(IRandom random);

	public void clearAgentsFromSolutionForRHS();

	public CInjection createInjection(CAgent agent);

	public void doPositiveUpdate(List<IConnectedComponent> rightHandSide);

	public void setInjection(CInjection inj);
	
	public List<CAgent> getAgentFromSolutionForRHS();

	public List<CAgent> getAgentsSortedByIdInRule();

//	public String getHash();
	
}
