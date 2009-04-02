package com.plectix.simulator.components.stories;

import com.plectix.simulator.components.injections.CInjection;

import com.plectix.simulator.interfaces.IStates;

class CStoryState implements IStates {
	
	private int idInternalState = -1;
	private long idLinkAgent = -1;
	private int idLinkSite = -1;

	public static final IStates EMPTY_STATE = new CStoryState();
	
	public CStoryState() {
		idInternalState = -1;
		idLinkAgent = -1;
		idLinkSite = -1;
	}

	public CStoryState(int idInternalState, long idLinkAgent, int idLinkSite) {
		this.idInternalState = idInternalState;
		this.idLinkAgent = idLinkAgent;
		this.idLinkSite = idLinkSite;
	}

	public CStoryState(int idInternalState) {
		this.idInternalState = idInternalState;
	}

	public CStoryState(long idLinkAgent, int idLinkSite) {
		this.idLinkAgent = idLinkAgent;
		this.idLinkSite = idLinkSite;
	}

	public final int getIdInternalState() {
		return idInternalState;
	}

	public final long getIdLinkAgent() {
		return idLinkAgent;
	}

	public void setIdLinkAgent(long idLinkAgent) {
		this.idLinkAgent = idLinkAgent;
	}

	public final int getIdLinkSite() {
		return idLinkSite;
	}

	public final void addInformation(int idInternalState, long idLinkAgent,
			int idLinkSite) {
		if (this.idInternalState == -1)
			this.idInternalState = idInternalState;
		if (this.idLinkAgent == -1) {
			this.idLinkAgent = idLinkAgent;
			this.idLinkSite = idLinkSite;
		}
	}

	public final boolean equalz(IStates states) {
		if ((idInternalState == states.getIdInternalState())
				&& (idLinkAgent == states.getIdLinkAgent())
				&& (idLinkSite == states.getIdLinkSite()))
			return true;
		return false;
	}
}
