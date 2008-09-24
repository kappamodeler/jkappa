package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.ISite;

public class CSite implements ISite {
	public static final int NO_INDEX = -1;

	private static final CInternalState EMPTY_STATE = new CInternalState(NO_INDEX);
	
	private int nameId;
	private CLinkState linkState;
	private CInternalState internalState = EMPTY_STATE;
	private boolean changed;
	private CAgent linkAgent = null;
	private int linkIndex = NO_INDEX;
	
	private List<CConnectedComponent> lift = new ArrayList<CConnectedComponent>();
	
	public void setLift(List<CConnectedComponent> lift) {
		this.lift = lift;
	}

	public void addToLift(CConnectedComponent cc) {
		this.lift.add(cc);
	}
	
	public List<CConnectedComponent> getLift() {
		return lift;
	}


	public CSite(int id) {
		this.nameId = id;
		linkState=new CLinkState(CLinkState.STATUS_LINK_FREE);
	}
	
	@Override
	public final CLinkState getLinkState() {
		return linkState;
	}
	
	public final void setAgentLink(CAgent agent){
		if(agent == null)
			return;
		this.linkAgent = agent;
	}
	
	public final CAgent getAgentLink(){
		return linkAgent;
	}
	
	public final void setInternalState(CInternalState internalState) {
		this.internalState=internalState;
	}

	public final CInternalState getInternalState() {
		return internalState;
	}

	@Override
	public final boolean isChanged() {
		return changed;
	}

	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof CSite))
			return false;
		CSite site = (CSite) obj;
		if (!(nameId  == site.nameId))
			return false;
		if (internalState == null)
			return true;
//		return internalState.equals(site.internalState);
		return true;
	}

	public final void setLinkIndex(int index) {
		this.linkIndex  = index;
	}

	public final int getLinkIndex() {
		return linkIndex;
	}

	public final Integer getNameId() {
		return nameId;
	}

	
	
}
