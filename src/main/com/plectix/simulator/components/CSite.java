package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.IState;

public class CSite implements ISite {
	public static final int NO_INDEX = -1;
	private String name;
	private CLinkState linkState;
	private CInternalState internalState=null;
	private boolean changed;
	private CAgent linkAgent = null;
	private int linkIndex = NO_INDEX;
	

	public CSite(String name) {
		this.name = name;
		linkState=new CLinkState(CLinkState.STATUS_LINK_FREE);
	}
	
	@Override
	public CLinkState getLinkState() {
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
	
	public void setInternalState(CInternalState internalState) {
		this.internalState=internalState;
	}

	public CInternalState getInternalState() {
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
		if (internalState == null)
			return true;
		return internalState.equals(site.internalState);
	}

	public void setLinkIndex(int index) {
		this.linkIndex  = index;
	}

	public int getLinkIndex() {
		return linkIndex;
	}

	
	
}
