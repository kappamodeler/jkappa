package com.plectix.simulator.components.stories.newVersion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;

import javax.swing.event.CaretEvent;

import com.plectix.simulator.simulator.SimulationArguments;

public class CWiresStorage {
	private HashMap<WireHashKey, TreeMap<Long, CEvent>> storage;
	// private LinkedList<CEventChanges> stackOfChanges;
	private double averageTime;
	private CEvent observableEvent;
	private boolean isEnd = false;

	private SimulationArguments.StorifyMode compressionMode;

	public CWiresStorage(SimulationArguments.StorifyMode compressionMode) {
		this.storage = new HashMap<WireHashKey, TreeMap<Long, CEvent>>();
		this.compressionMode = compressionMode;
	}

	public void setAverageTime(double averageTime) {
		this.averageTime = averageTime;
	}

	private class MinMarkEvent {
		private CEvent minEvent;
		private int count = 0;

		public MinMarkEvent(CEvent e) {
			minEvent = e;
		}

		public void setMinEvent(CEvent minEvent) {
			this.minEvent = minEvent;
		}

		public CEvent getMinEvent() {
			return minEvent;
		}

		public int getCount() {
			return count;
		}

		public void upCount() {
			count++;
		}
	}
	
	public CEvent getUpEvent(WireHashKey key, CEvent eventIn){
		TreeMap<Long, CEvent> wire = storage.get(key);
		Long upId = wire.lowerKey(eventIn.getStepId());
		if(upId == null)
			return null;
		return wire.get(upId);
	}
	
	public CEvent getDownEvent(WireHashKey key, CEvent eventIn){
		TreeMap<Long, CEvent> wire = storage.get(key);
		Long downId = wire.higherKey(eventIn.getStepId());
		if(downId == null)
			return null;
		return wire.get(downId);
	}
	

	public List<WireHashKey> getLeastWires(long max){
		List<WireHashKey> outList = new LinkedList<WireHashKey>();
//		for(Map.Entry<WireHashKey, TreeMap<Long, CEvent>> )
		
		return null;
	}
	
	public void handling() {
		HashSet<CEvent> needEvents = new HashSet<CEvent>();
		handling(observableEvent, needEvents);
		clearStorage(needEvents);
		doCompression();
	}

	private void doCompression() {
		if (compressionMode == SimulationArguments.StorifyMode.NONE) {
			noneCompressStoryTrace();
		} else if (compressionMode == SimulationArguments.StorifyMode.WEAK) {
			weakCompression();
			// weakCompressStoryTrace();
		} else if (compressionMode == SimulationArguments.StorifyMode.STRONG) {
			strongCompressStoryTrace();
		} else {
			throw new IllegalArgumentException("Unknown StorifyMode: "
					+ compressionMode);
		}
	}

	private void strongCompressStoryTrace() {
		// TODO Auto-generated method stub

	}

	////////////////////////////////////////////////////////////////////////////
	// /////
	// ///////////// TANYA
	private void weakCompression() {
		CEvent keptDelEvent = markingWires(observableEvent);

		keptDelEvent.setState((myGOING(keptDelEvent)) ? EEvent.DELETED
				: EEvent.KEPT);
	}

	private boolean myGOING(CEvent event) {
		// event.setState(EEvent.DELETED);
		if (mmmBranch(event)) {
			// TODO
			// ////get data from stack
		}
		return false; // ////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	private boolean mmmBranch(CEvent event) {
		boolean flag = true;
		if (event.getState() == EEvent.KEPT) {
			return false;
		}
		event.setState(EEvent.DELETED);

		for (Entry<WireHashKey, AtomicEvent<?>> aEvent : event
				.getAtomicEvents().entrySet()) {
			CEvent nextEvent = getCausing(aEvent, event.getStepId());
			if (nextEvent != null) {
				// TODO
				return mmmBranch(nextEvent);
			} else {
				// TODO
				flag = (flag) ? checkit(aEvent) : flag;
			}
		}
		return flag;// ////////////////////////////
	}

	private boolean checkit(Entry<WireHashKey, AtomicEvent<?>> aevent) {
		// take from stack end check
		return false;
	}

	private CEvent getCausing(Entry<WireHashKey, AtomicEvent<?>> aevent,
			Long stepId) {
		// TODO Auto-generated method stub
		TreeMap<Long, CEvent> wire = storage.get(aevent.getKey());
		AtomicEvent<?> prevAevent = wire.get(stepId).getAtomicEvents().get(
				aevent.getKey());
		stepId = wire.lowerKey(stepId);

		if (stepId != null) {
			AtomicEvent<?> nextAevent = wire.get(stepId).getAtomicEvents().get(
					aevent.getKey());
			while (nextAevent != null) {// && nextAevent.getType() ==
				// ECheck.TEST) {
				prevAevent = nextAevent;
				stepId = wire.lowerKey(stepId);
				nextAevent = wire.get(stepId).getAtomicEvents().get(
						aevent.getKey());
			}
		}
		if (stepId != null) {
			return wire.get(stepId);
		}
		return null;
	}

	// ////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////

	// private void weakCompressStoryTrace() {
	// // TODO Auto-generated method stub
	//
	// CEvent keptDelEvent;
	// do {
	// keptDelEvent = markingWires(observableEvent);
	// createBranch(keptDelEvent);
	// } while (keptDelEvent != null);
	// }
	//
	// private void createBranch(CEvent event) {
	// // TODO Auto-generated method stub
	//
	// if (removeEvent(event) || keptEvent(event))
	// return;
	// event.setState(EEvent.KEPT);
	// }
	//
	// private boolean keptEvent(CEvent event) {
	// // TODO Auto-generated method stub
	// return false;
	// }

	// /**
	// * return <tt>true</tt> if we could to remove this event and events,
	// causing
	// * from this, else <tt>false</tt> Recursive void.
	// *
	// * @param keptDelEvent
	// * @return
	// */
	// private boolean removeEvent(CEvent event) {
	// // TODO Auto-generated method stub
	// if (event.getState() == EEvent.KEPT)
	// return false;
	// event.setState(EEvent.DELETED);
	// // HashSet<CEvent> checkedSet = new HashSet<CEvent>();
	// for (Entry<WireHashKey, AtomicEvent<?>> aEvent : event
	// .getAtomicEvents().entrySet()) {
	// CEvent nextEvent = getCausing(aEvent, event.getStepId());
	// if (nextEvent != null)
	// createBranch(nextEvent);
	// }
	// if (chechWire())
	// return true;
	// return false;
	// }

	private boolean chechWire() {
		for (Entry<WireHashKey, TreeMap<Long, CEvent>> entry : storage
				.entrySet()) {
			WireHashKey key = entry.getKey();
			TreeMap<Long, CEvent> wire = entry.getValue();
			AState<?> before = wire.get(wire.firstKey()).getAtomicEvents().get(
					key).getState();
			for (CEvent event : wire.values()) {
				if (event.getState() == EEvent.DELETED)
					continue;
				AtomicEvent<?> aEvent = event.getAtomicEvents().get(key);
				AState<?> state = aEvent.getState();
				if (!state.equalsBefore(before))
					return false;
				if (aEvent.getType() == ECheck.TEST)
					continue;
				before = aEvent.getState();
			}

		}
		// for (Map.Entry entry : event.getAtomicEvents().entrySet()) {
		// EKeyOfState key = (EKeyOfState) entry.getKey();
		// AtomicEvent aEvent = (AtomicEvent) entry.getValue();
		//
		// CEvent foundEvent = findCausing(key, aEvent, event.getStepId());
		// if (foundEvent != null && !checkEvents.contains(foundEvent)) {
		// checkEvents.add(foundEvent);
		// chechWire(foundEvent, checkEvents);
		// // foundEvent.initState();
		// }
		// }

		return true;
	}

	private boolean myBranch(CEvent event) {
		if (event.getState() != EEvent.KEPT) {
			event.setState(EEvent.DELETED);
			CEvent newEvent = getCausingEvent(event);
			myBranch(newEvent);
		} else {
			// ssdafd = event.
		}
		return false;
	}

	private CEvent getCausingEvent(CEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	private CEvent markingWires(CEvent event) {
		Map<WireHashKey, MinMarkEvent> markEvents = new HashMap<WireHashKey, MinMarkEvent>();
		for (WireHashKey key : event.getAtomicEvents().keySet()) {
			TreeMap<Long, CEvent> wire = storage.get(key);
			for (CEvent e : wire.values()) {
				if (e.getState() == EEvent.UNRESOLVED) {
					MinMarkEvent min = markEvents.get(key);
					if (min == null) {
						min = new MinMarkEvent(e);
						markEvents.put(key, min);
					}
					min.upCount();
					if (e.getStepId() > min.getMinEvent().getStepId())
						min.setMinEvent(e);
				}
			}
		}

		MinMarkEvent min = null;
		for (MinMarkEvent m : markEvents.values()) {
			if (min == null
					|| m.getCount() < min.getCount()
					|| (m.getCount() == min.getCount() && m.getMinEvent()
							.getStepId() > min.getMinEvent().getStepId())) {
				min = m;
			}
		}
		return min.getMinEvent();
	}

	private void noneCompressStoryTrace() {
		// TODO Auto-generated method stub

	}

	private void clearStorage(HashSet<CEvent> needEvents) {
		// TODO

		HashMap<WireHashKey, TreeMap<Long, CEvent>> newStorage = new HashMap<WireHashKey, TreeMap<Long, CEvent>>();
		for (CEvent event : needEvents) {
			// for(Map.Entry<WireHashKey, AtomicEvent<?>> entry :
			// event.getAtomicEvents().entrySet()){
			for (WireHashKey key : event.getAtomicEvents().keySet()) {
				TreeMap<Long, CEvent> wire = newStorage.get(key);
				if (wire == null) {
					wire = new TreeMap<Long, CEvent>();
					newStorage.put(key, wire);
				}
				wire.put(event.getStepId(), event);
			}
		}
		storage = newStorage;
		observableEvent.setState(EEvent.KEPT);
	}

	private void handling(CEvent event, HashSet<CEvent> needEvents) {
		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : event
				.getAtomicEvents().entrySet()) {
			WireHashKey key = entry.getKey();
			AtomicEvent<?> aEvent = entry.getValue();

			CEvent foundEvent = findCausing(key, aEvent, event.getStepId());
			if (foundEvent != null && !needEvents.contains(foundEvent)) {
				needEvents.add(foundEvent);
				handling(foundEvent, needEvents);
				foundEvent.initState();
			}
		}
	}

	private CEvent findCausing(WireHashKey key, AtomicEvent<?> event,
			Long stepId) {
		// TODO
		TreeMap<Long, CEvent> wire = storage.get(key);
		// AtomicEvent<?> prevAevent = event;
		// //wire.get(stepId).getAtomicEvents().get(key);
		stepId = wire.lowerKey(stepId);
		if (stepId == null)
			return event.getContainer();

		AtomicEvent<?> nextAevent = wire.get(stepId).getAtomicEvents().get(key);
		if (stepId != null) {
			while (nextAevent != null && nextAevent.getType() == ECheck.TEST) {
				stepId = wire.lowerKey(stepId);
				if (stepId == null)
					return event.getContainer();
				nextAevent = wire.get(stepId).getAtomicEvents().get(key);
			}
		}
		List<Integer> list = new ArrayList<Integer>();
		for(Integer i : list){
			list.add(5);
			
		}
		// if (stepId != null) {
		// return wire.get(stepId);
		// }
		// return null;
		return nextAevent.getContainer();

	}

	public boolean isEndOfStory() {
		return isEnd;
	}

	public void clearList() {

	}

	public void addEventContainer(CEvent eventContainer) {
		// TODO add check opposite
		eventContainer.clearsLinkStates();
		if (!isOpposite(eventContainer))
			for (WireHashKey key : eventContainer.getAtomicEvents().keySet()) {
				TreeMap<Long, CEvent> tree = storage.get(key);
				if (tree == null) {
					tree = new TreeMap<Long, CEvent>();
					storage.put(key, tree);
				}
				tree.put(eventContainer.getStepId(), eventContainer);
			}
	}

	private boolean isOpposite(CEvent eventIn) {
		HashMap<WireHashKey, AtomicEvent<?>> mapIn = getModificationAction(eventIn);
		Long stepId = eventIn.getStepId();
		HashSet<WireHashKey> set = new HashSet<WireHashKey>();
		HashSet<CEvent> listForDel = new HashSet<CEvent>();
		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : mapIn.entrySet()) {
			WireHashKey key = entry.getKey();
			AtomicEvent<?> aEvent = entry.getValue();

			AtomicEvent<?> aEventCheck = getAtomicLastModificationAtomicEvent(
					key, aEvent, stepId);
			if (aEventCheck == null)
				return false;
			if (!aEventCheck.getState().getBeforeState().equals(
					aEvent.getState().getAfterState()))//aEventCheck.getState().
														// equalsBefore
														// (aEvent.getState()))
				return false;
			set.addAll(aEventCheck.getContainer().getAtomicEvents().keySet());
			listForDel.add(aEventCheck.getContainer());
		}

		if (mapIn.size() != set.size())
			return false;

		for (CEvent e : listForDel) {
			for (WireHashKey key : e.getAtomicEvents().keySet())
				storage.get(key).remove(e.getStepId());
		}
		return true;
	}

	private HashMap<WireHashKey, AtomicEvent<?>> getModificationAction(
			CEvent eventIn) {
		HashMap<WireHashKey, AtomicEvent<?>> mapIn = new HashMap<WireHashKey, AtomicEvent<?>>();
		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : eventIn
				.getAtomicEvents().entrySet()) {
			AtomicEvent<?> aEvent = entry.getValue();
			if (aEvent.getType() != ECheck.TEST)
				mapIn.put(entry.getKey(), aEvent);

		}
		return mapIn;
	}

	private AtomicEvent<?> getAtomicLastModificationAtomicEvent(
			WireHashKey key, AtomicEvent<?> aEventIn, Long stepId) {
		TreeMap<Long, CEvent> wire = storage.get(key);

		if (wire == null)
			return null;

		stepId = wire.lowerKey(stepId);

		if (stepId != null) {
			AtomicEvent<?> nextAevent = wire.get(stepId).getAtomicEvents().get(
					key);
			while (nextAevent != null && nextAevent.getType() == ECheck.TEST) {
				stepId = wire.lowerKey(stepId);
				if (stepId == null)
					return null;

				nextAevent = wire.get(stepId).getAtomicEvents().get(key);
			}
			if (nextAevent != null && nextAevent.getType() != ECheck.TEST)
				return nextAevent;
		}
		return null;
	}

	public void addLastEventContainer(CEvent eventContainer, double currentTime) {
		// eventsContainerMap.put(eventContainer.getStepId(), eventContainer);
		addEventContainer(eventContainer);
		observableEvent = eventContainer;
		observableEvent.setState(EEvent.KEPT);
		averageTime = currentTime;
		isEnd = true;
	}

	public void setEndOfStory() {
		// TODO Auto-generated method stub

	}
}
