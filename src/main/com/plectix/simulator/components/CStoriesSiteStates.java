package com.plectix.simulator.components;

class CStoriesSiteStates {
	public final static byte LAST_STATE = 0;
	public final static byte CURRENT_STATE = 1;

	States currentState;

	public States getCurrentState() {
		return currentState;
	}

	public void setCurrentState(States currentState) {
		this.currentState = currentState;
	}

	public States getLastState() {
		return lastState;
	}

	public void setLastState(States lastState) {
		this.lastState = lastState;
	}

	States lastState;

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

	public void addInformation(int index, CStoriesSiteStates siteStates) {
		switch (index) {
		case CURRENT_STATE:
			currentState = siteStates.currentState;
			break;
		case LAST_STATE:
			if (lastState != null)
				lastState.addInformation(
						siteStates.getLastState().idInternalState, siteStates
								.getLastState().idLinkAgent, siteStates
								.getLastState().idLinkSite);
			break;
		}
	}

	class States {
		int idInternalState = -1;

		long idLinkAgent = -1;

		int idLinkSite = -1;

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

}
