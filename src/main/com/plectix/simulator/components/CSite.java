package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.IState;

public class CSite implements ISite {
	public static final int NO_INDEX = -1;
	private IState state = null;
	private String name;
	private ILinkState linkState;
	private IInternalState internalState=null;
	private boolean changed;
	private CAgent linkAgent = null;
	private int linkIndex = NO_INDEX;
	

	public CSite(String name) {
		this.name = name;
		linkState=new CLinkState(CLinkState.STATUS_LINK_FREE);
	}
	
	@Override
	public ILinkState getLinkState() {
		return linkState;
	}
	
	public void setAgentLink(CAgent agent){
		if(agent == null)
			return;
		this.linkAgent = agent;
	}
	
	public CAgent getAgentLink(){
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

	public void setLinkIndex(int index) {
		this.linkIndex  = index;
	}

	public int getLinkIndex() {
		return linkIndex;
	}

}
