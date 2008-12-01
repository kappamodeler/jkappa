package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.*;

public class CStoriesSiteStates implements IStoriesSiteStates {
	public final static byte LAST_STATE = 0;
	public final static byte CURRENT_STATE = 1;

	private IStates currentState;
	private IStates lastState;
	
	public IStates getCurrentState() {
		return currentState;
	}

	public void setCurrentState(IStates currentState) {
		this.currentState = currentState;
	}

	public IStates getLastState() {
		return lastState;
	}

	public void setLastState(IStates lastState) {
		this.lastState = lastState;
	}

	public CStoriesSiteStates(int index, long idLinkAgent, int idLinkSite) {
		switch (index) {
		case CURRENT_STATE:
			currentState = new States(idLinkAgent, idLinkSite);
			break;
		case LAST_STATE:
			lastState = new States(idLinkAgent, idLinkSite);
			break;
		}
	}

	public CStoriesSiteStates(int index, int idInternalState) {
		switch (index) {
		case CURRENT_STATE:
			currentState = new States(idInternalState);
			break;
		case LAST_STATE:
			lastState = new States(idInternalState);
			break;
		}
	}

	public CStoriesSiteStates(int index, int idInternalState, long idLinkAgent,
			int idLinkSite) {
		switch (index) {
		case CURRENT_STATE:
			currentState = new States(idInternalState, idLinkAgent, idLinkSite);
			break;
		case LAST_STATE:
			lastState = new States(idInternalState, idLinkAgent, idLinkSite);
			break;
		}
	}

	public void addInformation(int index, IStoriesSiteStates siteStates) {
		switch (index) {
		case CURRENT_STATE:
			currentState = siteStates.getCurrentState();
			break;
		case LAST_STATE:
			if (lastState != null)
				lastState.addInformation(
						siteStates.getLastState().getIdInternalState(), siteStates
								.getLastState().getIdLinkAgent(), siteStates
								.getLastState().getIdLinkSite());
			break;
		}
	}

	class States implements IStates {
		private int idInternalState = -1;

		public int getIdInternalState() {
			return idInternalState;
		}

		public long getIdLinkAgent() {
			return idLinkAgent;
		}

		public int getIdLinkSite() {
			return idLinkSite;
		}

		private long idLinkAgent = -1;

		private int idLinkSite = -1;

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

	public final static boolean isEqual(IStates states, IStates states2) {
		if (states==null || states2==null)
			return false;
		
		if (states.getIdInternalState() != states2.getIdInternalState())
			return false;
		if (states.getIdLinkAgent() != states2.getIdLinkAgent())
			return false;
		if (states.getIdLinkSite() != states2.getIdLinkSite())
			return false;

		return true;
	}

}
