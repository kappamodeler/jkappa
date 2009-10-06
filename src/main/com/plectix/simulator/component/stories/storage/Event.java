package com.plectix.simulator.component.stories.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.InternalState;
import com.plectix.simulator.component.Link;
import com.plectix.simulator.component.LinkRank;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.component.stories.ActionOfAEvent;
import com.plectix.simulator.component.stories.MarkOfEvent;
import com.plectix.simulator.component.stories.State;
import com.plectix.simulator.component.stories.TypeOfWire;
import com.plectix.simulator.simulator.SimulationData;

public class Event implements EventInterface {
	public final static boolean BEFORE_STATE = true;
	public final static boolean AFTER_STATE = false;

	private MarkOfEvent mark = null;

	private final ArrayList<WireHashKey> filter = new ArrayList<WireHashKey>();

	// map with all wires which touched by this event
	private final LinkedHashMap<WireHashKey, AtomicEvent<?>> eventsMap = new LinkedHashMap<WireHashKey, AtomicEvent<?>>();
	private final long stepId;

	// event symbolize applying this rule
	private final int ruleId;

	public Event(long stepId, int ruleId) {
		this.ruleId = ruleId;
		this.stepId = stepId;
	}

	public Event(long stepId, int ruleId, MarkOfEvent initialMark) {
		this.ruleId = ruleId;
		this.stepId = stepId;
		this.mark = initialMark;
	}

	
	public AtomicEvent<?> addAtomicEvent(WireHashKey key,
			ActionOfAEvent type, TypeOfWire typeOfWire) {
		AtomicEvent<?> event =  eventsMap.get(key);
		if (event == null) {
			addToFilter(key);
			switch (typeOfWire) {
				case AGENT :
					event = new AtomicEvent<State>(this, type);
				case BOUND_FREE :
					event = new AtomicEvent<State>(this, type);
				case INTERNAL_STATE :
					event = new AtomicEvent<String>(this, type);
				case LINK_STATE :
					event = new AtomicEvent<StateOfLink>(this, type);
				default :;	
			}
			
			eventsMap.put(key, event);
		} else
			event.correctingType(type);
		return event;
	}

	public final long getStepId() {
		return stepId;
	}

	public final int getRuleId() {
		return ruleId;
	}

	public final LinkedHashMap<WireHashKey, AtomicEvent<?>> getAtomicEvents() {
		return eventsMap;
	}

	public final void setMark(MarkOfEvent newMark,MasterInformationAboutWires information)
			throws StoryStorageException {
		if (newMark == mark)
			throw new StoryStorageException("same mark");

		// there is newMark != unresolved
		if (mark == MarkOfEvent.UNRESOLVED) {
			shiftNumberOfUnresolvedEventsOnWires(false,information);
		} else {
			if (newMark == MarkOfEvent.UNRESOLVED) {
				shiftNumberOfUnresolvedEventsOnWires(true, information);
			}
		}
		mark = newMark;
	}

	private final void shiftNumberOfUnresolvedEventsOnWires(boolean up,
			MasterInformationAboutWires information) throws StoryStorageException {
		for (WireHashKey w : eventsMap.keySet()) {
			if (eventsMap.get(w).getType() != ActionOfAEvent.TEST) {
				information.upNumberOfUnresolvedModifyEvent(w, up);
			}
		}
	}

	public final MarkOfEvent getMark() {
		return mark;
	}

	public final AtomicEvent<?> getAtomicEvent(int index)
			throws StoryStorageException {
		WireHashKey wk = filter.get(index);
		if (wk == null)
			throw new StoryStorageException("get atomic event =null", index);
		return eventsMap.get(wk);
	}

	public final int getAtomicEventCount() throws StoryStorageException {
		// TODO: refactor
		filter.clear();
		for (WireHashKey wk : eventsMap.keySet()) {
			addToFilter(wk);
		}
		return filter.size();
	}

	public final TypeOfWire getAtomicEventType(int index)
			throws StoryStorageException {
		WireHashKey wk = filter.get(index);
		if (wk == null)
			throw new StoryStorageException("get atomic event type", index);

		// TODO comment after good testing
		// if (eventsMap.get(wk).getType() ==
		// EActionOfAEvent.TEST_AND_MODIFICATION
		// && eventsMap.get(wk).getState().getAfterState() == eventsMap
		// .get(wk).getState().getBeforeState()) {
		// throw new StoryStorageException(
		// "states after and before equals on testAndModify event");
		// }
		// if (eventsMap.get(wk).getType() == EActionOfAEvent.TEST
		// && (eventsMap.get(wk).getState().getAfterState() != null || eventsMap
		// .get(wk).getState().getBeforeState() == null))
		// throw new StoryStorageException(
		// "states after!=null or before=null on test event");
		//
		// if (eventsMap.get(wk).getType() == EActionOfAEvent.MODIFICATION
		// && (eventsMap.get(wk).getState().getBeforeState() != null ||
		// eventsMap
		// .get(wk).getState().getAfterState() == null))
		// throw new StoryStorageException(
		// "states after=null or before!=null on onlyModify event  "
		// + this.stepId);

		return wk.getTypeOfWire();
	}

	public final WireHashKey getWireKey(int index) throws StoryStorageException {
		if (filter.isEmpty())
			throw new StoryStorageException("filter in CEvent is empty");
		WireHashKey wk = filter.get(index);
		if (wk == null)
			throw new StoryStorageException("get atomic event", index);
		return wk;
	}

	// optimize : LinkedList -> ArrayList
	public final Iterator<WireHashKey> wireEventIterator() {
		List<AtomicEvent<?>> list = new LinkedList<AtomicEvent<?>>();

		for (int i = 0; i < filter.size(); i++) {
			list.add(eventsMap.get(filter.get(i)));
		}
		return new IteratorAtomicEventWithinEvent(eventsMap);
	}

	public final boolean containsWire(WireHashKey wireKey) {
		return eventsMap.containsKey(wireKey);
	}

	public final AtomicEvent<?> getAtomicEvent(WireHashKey wireKey)
			throws StoryStorageException {
		AtomicEvent<?> event = eventsMap.get(wireKey);
		if (event == null) {
			throw new StoryStorageException("getAtomicEvent", wireKey
					.hashCode());
		}
		return event;
	}

	public final void removeWire(WireHashKey wKey) {
		eventsMap.remove(wKey);
		filter.remove(wKey);
	}

	/**
	 * return null if all wire in this event doesn't contain unresolved modify
	 * event
	 * 
	 * @throws StoryStorageException
	 * 
	 */
	public final WireHashKey getWireWithMinimumUresolvedEvent(
			MasterInformationAboutWires information) throws StoryStorageException {
		WireHashKey wKey = filter.get(0);
		int n = filter.size();
		int m = information.getUnresolvedModifyCount(wKey);
		int temp;
		for (int i = 1; i < n; i++) {
			temp = information.getUnresolvedModifyCount(filter.get(i));
			if ((temp != 0 && temp < m) || (m == 0 && temp > 0)) {
				wKey = filter.get(i);
				m = temp;
			}
		}

		if (m > 0) {
			return wKey;
		} else {
			return null;
		}
	}

	public final void setMarkUnresolved(MasterInformationAboutWires information)
			throws StoryStorageException {
		mark = MarkOfEvent.UNRESOLVED;
		shiftNumberOfUnresolvedEventsOnWires(true, information);
	}

	public final void addToFilter(WireHashKey key) {
		if (key.getTypeOfWire() == TypeOfWire.AGENT)
			filter.add(0, key);
		else
			filter.add(key);
	}


	public final void onlySetMark(MarkOfEvent newMark) {
		mark = newMark;

	}

	// rebuild event and numberOfUnresolvedevents on wires
	public final List<PointRound> exchangeWires(
			Map<WireHashKey, WireHashKey> map, MasterInformationAboutWires information)
			throws StoryStorageException {

		List<PointRound> list = new LinkedList<PointRound>();

		for (WireHashKey wk : map.keySet()) {
			WireHashKey wkTwo = map.get(wk);

			AtomicEvent<?> ae1 = eventsMap.remove(wk);
			AtomicEvent<?> ae2 = eventsMap.remove(wkTwo);

			if (ae1 == null && ae2 == null) {
				continue;
			}
			if (ae1 == null) {
				eventsMap.put(wk, ae2);

			} else {
				if (ae2 == null) {
					eventsMap.put(wkTwo, ae1);

				} else {
					eventsMap.put(wkTwo, ae1);
					eventsMap.put(wk, ae2);
				}
			}

			PointRound pr = new PointRound();
			pr.number = stepId;
			pr.wk1 = wk;
			pr.wk2 = wkTwo;
			list.add(pr);

			int dif = 0;
			if (mark == MarkOfEvent.UNRESOLVED) {
				if (eventsMap.get(wk) != null
						&& eventsMap.get(wk).getType() != ActionOfAEvent.TEST) {
					dif++;
				}
				if (eventsMap.get(wkTwo) != null
						&& eventsMap.get(wkTwo).getType() != ActionOfAEvent.TEST) {
					dif--;
				}
				if (dif > 0) {
					information.upNumberOfUnresolvedModifyEvent(wk, true);
					information.upNumberOfUnresolvedModifyEvent(wkTwo, false);
				}

				if (dif < 0) {
					information.upNumberOfUnresolvedModifyEvent(wk, false);
					information.upNumberOfUnresolvedModifyEvent(wkTwo, true);
				}

			}
		}
		filter.clear();
		for (WireHashKey wk : eventsMap.keySet()) {
			addToFilter(wk);
		}
		return list;

	}

	@Override
	public final String toString() {
		String string = new String("mark: " + mark + " stepId: " + stepId
				+ " ruleId: " + ruleId);
		for (Entry<WireHashKey, AtomicEvent<?>> wk : eventsMap.entrySet()) {
			string += "wire : (" + wk.getKey() + ") + AtomicEvent "
					+ wk.getValue();
		}
		return string;
	}


	








}
