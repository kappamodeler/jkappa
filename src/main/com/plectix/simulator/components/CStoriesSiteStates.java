package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.*;

public final class CStoriesSiteStates implements IStoriesSiteStates {
	public enum StateType {
		LAST,
		CURRENT;
	}
	
	private IStates currentState;
	private IStates lastState;

	// TODO separate
	private class States implements IStates {
		private int idInternalState = -1;
		private long idLinkAgent = -1;
		private int idLinkSite = -1;

		public States() {
		}

		public States(int idInternalState, long idLinkAgent, int idLinkSite) {
			this.idInternalState = idInternalState;
			this.idLinkAgent = idLinkAgent;
			this.idLinkSite = idLinkSite;
		}

		public States(int idInternalState) {
			this.idInternalState = idInternalState;
		}

		public States(long idLinkAgent, int idLinkSite) {
			this.idLinkAgent = idLinkAgent;
			this.idLinkSite = idLinkSite;
		}

		public int getIdInternalState() {
			return idInternalState;
		}

		public long getIdLinkAgent() {
			return idLinkAgent;
		}

		public int getIdLinkSite() {
			return idLinkSite;
		}

		public void addInformation(int idInternalState, long idLinkAgent,
				int idLinkSite) {
			if (this.idInternalState == -1)
				this.idInternalState = idInternalState;
			if (this.idLinkAgent == -1) {
				this.idLinkAgent = idLinkAgent;
				this.idLinkSite = idLinkSite;
			}
		}
	}

	public CStoriesSiteStates() {
		currentState = new States();
		lastState = new States();
	}

	public CStoriesSiteStates(StateType index, long idLinkAgent, int idLinkSite) {
		switch (index) {
		case CURRENT:
			currentState = new States(idLinkAgent, idLinkSite);
			break;
		case LAST:
			lastState = new States(idLinkAgent, idLinkSite);
			break;
		}
	}

	public CStoriesSiteStates(StateType index, int idInternalState) {
		switch (index) {
		case CURRENT:
			currentState = new States(idInternalState);
			break;
		case LAST:
			lastState = new States(idInternalState);
			break;
		}
	}

	public CStoriesSiteStates(StateType index, int idInternalState, long idLinkAgent,
			int idLinkSite) {
		switch (index) {
		case CURRENT:
			currentState = new States(idInternalState, idLinkAgent, idLinkSite);
			break;
		case LAST:
			lastState = new States(idInternalState, idLinkAgent, idLinkSite);
			break;
		}
	}

	public final IStates getCurrentState() {
		return currentState;
	}

	public final void setCurrentState(IStates currentState) {
		this.currentState = currentState;
	}

	public final IStates getLastState() {
		return lastState;
	}

	public final void setLastState(IStates lastState) {
		this.lastState = lastState;
	}

	public final void addInformation(StateType index, IStoriesSiteStates siteStates) {
		switch (index) {
		case CURRENT:
			currentState = siteStates.getCurrentState();
			break;
		case LAST:
			if (lastState != null)
				lastState.addInformation(siteStates.getLastState()
						.getIdInternalState(), siteStates.getLastState()
						.getIdLinkAgent(), siteStates.getLastState()
						.getIdLinkSite());
			break;
		}
	}

	public final static boolean isEqual(IStates states, IStates states2) {
		if (states == null || states2 == null)
			return false;

		if (states.getIdLinkAgent() != states2.getIdLinkAgent())
			return false;
		if (states.getIdLinkSite() != states2.getIdLinkSite())
			return false;

		if (states.getIdInternalState() == CSite.NO_INDEX
				|| states2.getIdInternalState() == CSite.NO_INDEX)
			return true;

		if (states.getIdInternalState() != states2.getIdInternalState())
			return false;

		return true;
	}

}
