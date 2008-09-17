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
	private IInternalState internalState;
	private boolean changed;
	private ISite linkSite = null;
	private IAgent linkAgent = null;
	private byte statusLink;
	
	public static final byte STATUS_LINK_CONNECTED = 0x01;
	public static final byte STATUS_LINK_MAY_BE = 0x02;
	public static final byte STATUS_LINK_FREE = 0x04;
	

	public CSite(String name) {
		this.name = name;
		statusLink = STATUS_LINK_FREE;
	}
	
	public void setStatusLink(byte statusLink){
		this.statusLink=statusLink;
	}
	
	public byte getStatusLink(){
		return statusLink;
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
			this.state = state;

	}

	@Override
	public IInternalState getInternalState() {
		return internalState;
	}

	@Override
	public ILinkState getLinkState() {
		return linkState;
	}

	@Override
	public IState getState() {
		return state;
	}

	@Override
	public boolean isChanged() {
		return changed;
	}

	@Override
	public void setLink(ISite site) {
		if(site==null)
			return;
		linkSite=site;
		statusLink=STATUS_LINK_CONNECTED;
//		site.setLink(link);
	}

	@Override
	public ISite getLink() {
		return linkSite;
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
