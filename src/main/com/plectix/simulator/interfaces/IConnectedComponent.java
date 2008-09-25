package com.plectix.simulator.interfaces;

import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;

public interface IConnectedComponent {

	
	//TODO creating spanning tree and stuff
	public void precompile();
	
	public void precompilationToString();
	
	//TODO ???
	public boolean unify(CAgent agent);
	public IInjection checkAndBuildInjection(ISolution solution, IAgent agent);
	
	public String getPrecompilationAsString();
	
	//TODO ???
	public List<IInjection> pushout();
	
	public List<CAgent> getAgents();
	
}
