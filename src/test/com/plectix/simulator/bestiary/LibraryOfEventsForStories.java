package com.plectix.simulator.bestiary;

import java.util.List;
import java.util.Map.Entry;

import com.plectix.simulator.component.InternalState;
import com.plectix.simulator.component.Link;
import com.plectix.simulator.component.LinkStatus;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.component.stories.ActionOfAEvent;
import com.plectix.simulator.component.stories.TypeOfWire;
import com.plectix.simulator.component.stories.storage.AtomicEvent;
import com.plectix.simulator.component.stories.storage.Event;
import com.plectix.simulator.component.stories.storage.WireHashKey;
import com.plectix.simulator.stories.unitTests.FakeEvent;

public class LibraryOfEventsForStories {

	private static List<FakeEvent> library;

	static FakeEvent mergeEvents(List<FakeEvent > list) {
		FakeEvent  answer = new FakeEvent (0, 0);
		for (Event e : list) {
			for (Entry<WireHashKey, AtomicEvent<?>> entry : e.getAtomicEvents()
					.entrySet()) {

				answer.getAtomicEvents().put(entry.getKey(), entry.getValue());

			}

		}
		return answer;
	}

//	public void addmodifyInternalState(FakeEvent e, Long agentId, String sitename,
//			String newState) {
//		e.addEventInternalState(new WireHashKey(agentId, sitename,
//				TypeOfWire.INTERNAL_STATE), new InternalState(newState),
//				ActionOfAEvent.MODIFICATION, false);
//	}
//
//	public void addtestAndModifyInternalState(FakeEvent e, Long agentId,
//			String sitename, String oldState, String newState) {
//		e.addEventInternalState(new WireHashKey(agentId, sitename,
//				TypeOfWire.INTERNAL_STATE), new InternalState(newState),
//				ActionOfAEvent.TEST_AND_MODIFICATION, false);
//		e.addEventInternalState(new WireHashKey(agentId, sitename,
//				TypeOfWire.INTERNAL_STATE), new InternalState(oldState),
//				ActionOfAEvent.TEST_AND_MODIFICATION, true);
//
//	}
//
//	public void addtestInternalState(FakeEvent e, Long agentId, String sitename,
//			String state) {
//		e.addEventInternalState(new WireHashKey(agentId, sitename,
//				TypeOfWire.INTERNAL_STATE), new InternalState(state),
//				ActionOfAEvent.TEST, true);
//	}
//
//	public void addtestAndModifyBoundState(FakeEvent e, Long agentId, String sitename,
//			boolean linkedBefore) {
//
//		Link link = new Link(LinkStatus.BOUND);
//		Link free = new Link(LinkStatus.FREE);
//		link.connectSite(new Site(""));
//		WireHashKey wk = new WireHashKey(agentId, sitename,
//				TypeOfWire.BOUND_FREE);
//		if (linkedBefore) {
//			e.addEventBoundFree(wk, link, ActionOfAEvent.TEST_AND_MODIFICATION,
//					true);
//			e.addEventBoundFree(wk, free, ActionOfAEvent.TEST_AND_MODIFICATION,
//					false);
//		} else {
//			e.addEventBoundFree(wk, link, ActionOfAEvent.TEST_AND_MODIFICATION,
//					false);
//			e.addEventBoundFree(wk, free, ActionOfAEvent.TEST_AND_MODIFICATION,
//					true);
//		}
//
//	}
//
//	public void addtestBoundState(FakeEvent e, Long agentId, String sitename,
//			boolean linkedBefore) {
//
//		Link link = new Link(LinkStatus.BOUND);
//		Link free = new Link(LinkStatus.FREE);
//		link.connectSite(new Site(""));
//		WireHashKey wk = new WireHashKey(agentId, sitename,
//				TypeOfWire.BOUND_FREE);
//		if (linkedBefore) {
//			e.addEventBoundFree(wk, link, ActionOfAEvent.TEST, true);
//		} else {
//			e.addEventBoundFree(wk, free, ActionOfAEvent.TEST, true);
//		}
//
//	}
//
//	public void addmodifyBoundState(FakeEvent e, Long agentId, String sitename,
//			boolean linkedAfter) {
//
//		Link link = new Link(LinkStatus.BOUND);
//		Link free = new Link(LinkStatus.FREE);
//		link.connectSite(new Site(""));
//		WireHashKey wk = new WireHashKey(agentId, sitename,
//				TypeOfWire.BOUND_FREE);
//		if (!linkedAfter) {
//			e.addEventBoundFree(wk, free, ActionOfAEvent.MODIFICATION, false);
//		} else {
//			e.addEventBoundFree(wk, link, ActionOfAEvent.MODIFICATION, false);
//		}
//	}
//
//	public void addtestAndModifyAgentState(FakeEvent e, Long agentId) {
//		e.addEventAgent(new WireHashKey(agentId, TypeOfWire.AGENT),
//				ActionOfAEvent.TEST_AND_MODIFICATION, true);
//	}
//
//	public void addtestAgentState(FakeEvent e, Long agentId) {
//		e.addEventAgent(new WireHashKey(agentId, TypeOfWire.AGENT),
//				ActionOfAEvent.TEST, true);
//	}
//
//	public void addmodifyAgentState(FakeEvent e, Long agentId) {
//		e.addEventAgent(new WireHashKey(agentId, TypeOfWire.AGENT),
//				ActionOfAEvent.MODIFICATION, false);
//	}
//
//	public void addtestAndModifyLinkState(FakeEvent e, Long agentId, String sitename,
//			String linkedSiteBefore, String linkedSiteAfter) {
//		Link before;
//		if (linkedSiteBefore == "") {
//			before = new Link(LinkStatus.FREE);
//		} else {
//			before = new Link(LinkStatus.BOUND);
//			before.connectSite(new Site(linkedSiteBefore));
//		}
//
//		Link after;
//		if (linkedSiteAfter == "") {
//			after = new Link(LinkStatus.FREE);
//		} else {
//			after = new Link(LinkStatus.BOUND);
//			after.connectSite(new Site(linkedSiteAfter));
//		}
//
//		e.addEventLinkState(new WireHashKey(agentId, sitename,
//				TypeOfWire.LINK_STATE), before,
//				ActionOfAEvent.TEST_AND_MODIFICATION, true);
//		e.addEventLinkState(new WireHashKey(agentId, sitename,
//				TypeOfWire.LINK_STATE), after,
//				ActionOfAEvent.TEST_AND_MODIFICATION, false);
//
//	}
//
//	public void addtestLinkState(FakeEvent e, Long agentId, String sitename,
//			String linkedSiteBefore) {
//		Link before;
//		if (linkedSiteBefore == "") {
//			before = new Link(LinkStatus.FREE);
//		} else {
//			before = new Link(LinkStatus.BOUND);
//			before.connectSite(new Site(linkedSiteBefore));
//		}
//
//		e.addEventLinkState(new WireHashKey(agentId, sitename,
//				TypeOfWire.LINK_STATE), before, ActionOfAEvent.TEST, true);
//
//	}
//
//	public void addmodifyLinkState(FakeEvent e, Long agentId, String sitename,
//			String linkedSiteAfter) {
//
//		Link after;
//		if (linkedSiteAfter == "") {
//			after = new Link(LinkStatus.FREE);
//		} else {
//			after = new Link(LinkStatus.BOUND);
//			after.connectSite(new Site(linkedSiteAfter));
//		}
//
//		e.addEventLinkState(new WireHashKey(agentId, sitename,
//				TypeOfWire.LINK_STATE), after, ActionOfAEvent.MODIFICATION,
//				false);
//	}
}
