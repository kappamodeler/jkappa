package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IInjection;

public class CInjection implements IInjection{

	private List<CState> stateList = new ArrayList<CState>();
	
	private CConnectedComponent connectedComponent;
	
	public CInjection(CConnectedComponent connectedComponent, List<CState> stateList){
		this.connectedComponent = connectedComponent;
		this.stateList = stateList;
	}
	
	public CInjection(){
	}
	
	public List<CState> getStateList() {
		return stateList;
	}

	public void setStateList(List<CState> stateList) {
		this.stateList = stateList;
	}
	
	public CConnectedComponent getConnectedComponent(){
		return connectedComponent;
	}
	

	@Override
	public List<IAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAgents(List<IAgent> agents) {
		// TODO Auto-generated method stub
		
	}

	
	
}
