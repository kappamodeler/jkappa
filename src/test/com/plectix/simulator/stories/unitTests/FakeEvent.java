package com.plectix.simulator.stories.unitTests;

import com.plectix.simulator.component.InternalState;
import com.plectix.simulator.component.Link;
import com.plectix.simulator.component.stories.ActionOfAEvent;
import com.plectix.simulator.component.stories.storage.Event;
import com.plectix.simulator.component.stories.storage.WireHashKey;

public class FakeEvent extends Event {

	public FakeEvent(long stepId, int ruleId) {
		super(stepId, ruleId);
	}
	
//	public void addEventInternalState(WireHashKey key, InternalState internalState,
//			ActionOfAEvent type, boolean isBefore) {
//		super.addEventInternalState(key, internalState, type, isBefore);
//	}
//	
//	public void addEventLinkState(WireHashKey key, Link linkState,
//			ActionOfAEvent type, boolean isBefore) {
//		super.addEventLinkState(key, linkState, type, isBefore);
//	}
//	
//	public  void addEventBoundFree(WireHashKey key, Link linkState,
//			ActionOfAEvent type, boolean isBefore) {
//		super.addEventBoundFree(key, linkState, type, isBefore);
//	}
//	public void addEventAgent(WireHashKey key, ActionOfAEvent type,
//			boolean existsBefore) {
//		super.addEventAgent(key, type, existsBefore);
//	}

}
