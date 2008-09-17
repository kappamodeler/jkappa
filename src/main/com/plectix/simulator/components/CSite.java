package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.IState;

public class CSite implements ISite {
	private IState state = null;
	private String name;
	private ILinkState linkState;
	private IInternalState internalState=null;
	private boolean changed;
	private IAgent linkAgent = null;
	

	public CSite(String name) {
		this.name = name;
		linkState=new CLinkState(CLinkState.STATUS_LINK_FREE);
	}
	
	public void setLinkState(ISite site){
		linkState.setSite(site);
	}
	
	@Override
	public ILinkState getLinkState() {
		return linkState;
	}
	
	public void setAgentLink(IAgent agent){
		if(agent == null)
			return;
		this.linkAgent = agent;
	}
	
	public IAgent getAgentLink(){
		return linkAgent;
	}
	
	@Override
	public void setState(IState state) {
		if (state != null)
			internalState=new CInternalState(state);
		

	}

	@Override
	public IInternalState getInternalState() {
		return internalState;
	}


	@Override
	public boolean isChanged() {
		return changed;
	}


	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CSite))
			return false;
		CSite site = (CSite) obj;
		if (!name.equals(site.name))
			return false;
		if (state == null)
			return true;
		return state.equals(site.state);
	}

}
