package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.*;

public final class CStoriesSiteStates implements IStoriesSiteStates {
	public enum StateType {
		LAST,
		CURRENT;
	}
	
	private IStates afterState;
	private IStates beforeState;

	// TODO separate

	public CStoriesSiteStates() {
		afterState = new CStoryState();
		beforeState = new CStoryState();
	}

	public CStoriesSiteStates(StateType index, long idLinkAgent, int idLinkSite) {
		switch (index) {
		case CURRENT:
			afterState = new CStoryState(idLinkAgent, idLinkSite);
			break;
		case LAST:
			beforeState = new CStoryState(idLinkAgent, idLinkSite);
			break;
		}
	}

	public CStoriesSiteStates(StateType index, int idInternalState) {
		switch (index) {
		case CURRENT:
			afterState = new CStoryState(idInternalState);
			break;
		case LAST:
			beforeState = new CStoryState(idInternalState);
			break;
		}
	}

	public CStoriesSiteStates(StateType index, int idInternalState, long idLinkAgent,
			int idLinkSite) {
		switch (index) {
		case CURRENT:
			afterState = new CStoryState(idInternalState, idLinkAgent,
					idLinkSite);
			break;
		case LAST:
			beforeState = new CStoryState(idInternalState, idLinkAgent,
					idLinkSite);
			break;
		}
	}

	public final IStates getAfterState() {
		return afterState;
	}

	public final void setAfterState(IStates currentState) {
		this.afterState = currentState;
	}

	public final IStates getBeforeState() {
		return beforeState;
	}

	public final void setBeforeState(IStates lastState) {
		this.beforeState = lastState;
	}

	public final void addInformation(StateType index, IStoriesSiteStates siteStates) {
		switch (index) {
		case CURRENT:
			afterState = siteStates.getAfterState();
			break;
		case LAST:
			if (beforeState != null)
				beforeState.addInformation(siteStates.getBeforeState()
						.getIdInternalState(), siteStates.getBeforeState()
						.getIdLinkAgent(), siteStates.getBeforeState()
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

		if (states.getIdInternalState() != CSite.NO_INDEX
				&& states2.getIdInternalState() != CSite.NO_INDEX
				&& states.getIdInternalState() != states2.getIdInternalState())
			return false;

		return true;
	}
}
