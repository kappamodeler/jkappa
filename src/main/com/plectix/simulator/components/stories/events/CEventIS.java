package com.plectix.simulator.components.stories.events;

import com.plectix.simulator.interfaces.IStoriesSiteStates;

class CEventIS extends AEvent {
	private final int idInternalStateBefore;

	private final int idInternalStateAfter;

	public CEventIS(long step, IStoriesSiteStates states){
		super(step);
		if(states.getBeforeState() != null)
			this.idInternalStateBefore = states.getBeforeState().getIdInternalState();
		else 
			this.idInternalStateBefore = -1;
		if(states.getAfterState() != null)
			this.idInternalStateAfter = states.getAfterState().getIdInternalState();
		else
			this.idInternalStateAfter = -1;
	}

	public boolean isCausing() {
		return false;
	}

	public int getIdInternalStateBefore() {
		return idInternalStateBefore;
	}

	public int getIdInternalStateAfter() {
		return idInternalStateAfter;
	}
}
