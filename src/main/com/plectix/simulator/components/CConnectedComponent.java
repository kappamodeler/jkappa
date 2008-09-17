package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISolution;

public class CConnectedComponent implements IConnectedComponent{
	
	private ArrayList<CAgentRule> agentList=new ArrayList<CAgentRule>();

	@Override
	public IInjection checkAndBuildInjection(ISolution solution, IAgent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrecompilationAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void precompilationToString() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void precompile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IInjection> pushout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IConnectedComponent> unify(ISolution solution,
			IAgent agent) {
		// TODO Auto-generated method stub
		return null;
	}

}
