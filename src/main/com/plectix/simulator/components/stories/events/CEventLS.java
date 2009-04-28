package com.plectix.simulator.components.stories.events;

import com.plectix.simulator.interfaces.IStates;
import com.plectix.simulator.interfaces.IStoriesSiteStates;

class CEventLS extends AEvent {
	private final CStateOfLinkState before;
	private final CStateOfLinkState after;
	
	public CEventLS(long step,IStoriesSiteStates states){
		super(step);
		IStates beforeState = states.getBeforeState();
		IStates afterState = states.getAfterState();
		if(beforeState != null)
			this.before = new CStateOfLinkState(beforeState.getIdLinkAgent(),beforeState.getIdLinkSite());
		else
			this.before = null;
		if(afterState != null)
			this.after = new CStateOfLinkState(afterState.getIdLinkAgent(),afterState.getIdLinkSite());
		else
			this.after = null;
	}

	public boolean isCausing() {
		return false;
	}

	public CStateOfLinkState getBefore() {
		return before;
	}

	public CStateOfLinkState getAfter() {
		return after;
	}
}
