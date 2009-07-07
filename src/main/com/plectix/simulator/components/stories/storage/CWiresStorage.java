package com.plectix.simulator.components.stories.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;

import javax.swing.event.CaretEvent;

import com.plectix.simulator.components.stories.compressions.Compressor;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CWiresStorage {
	// wireHashKey - wireId
	// Long - number of Event
	private HashMap<WireHashKey, TreeMap<Long, CEvent>> storage;
	private HashMap<Integer, HashSet<Integer>> internalStates;
	// private LinkedList<CEventChanges> stackOfChanges;
	private double averageTime;
	private CEvent observableEvent;
	private boolean isEnd = false;

	private SimulationArguments.StorifyMode compressionMode;

	public CWiresStorage(SimulationArguments.StorifyMode compressionMode) {
		this.storage = new HashMap<WireHashKey, TreeMap<Long, CEvent>>();
		this.internalStates = new HashMap<Integer, HashSet<Integer>>();
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

	public CEvent getUpEvent(WireHashKey key, CEvent eventIn) {
		TreeMap<Long, CEvent> wire = storage.get(key);
		Long upId = wire.lowerKey(eventIn.getStepId());
		if (upId == null)
			return null;
		return wire.get(upId);
	}

	public CEvent getDownEvent(WireHashKey key, CEvent eventIn) {
		TreeMap<Long, CEvent> wire = storage.get(key);
		Long downId = wire.higherKey(eventIn.getStepId());
		if (downId == null)
			return null;
		return wire.get(downId);
	}

	public List<WireHashKey> getLeastWires(long max) {
		List<WireHashKey> outList = new LinkedList<WireHashKey>();
		// for(Map.Entry<WireHashKey, TreeMap<Long, CEvent>> )

		return null;
	}

	public void handling() {
		HashSet<CEvent> needEvents = new HashSet<CEvent>();
		handling(observableEvent, needEvents);
		clearStorage(needEvents);
		AbstractStorage aStorage = extractStorage();
		Compressor compressor = new Compressor(aStorage, compressionMode);
		doCompression();
	}

	private void doCompression() {
		if (compressionMode == SimulationArguments.StorifyMode.NONE) {
			noneCompressStoryTrace();
		} else if (compressionMode == SimulationArguments.StorifyMode.WEAK) {
			// weakCompression();
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

	private CEvent getCausingEvent(CEvent event) {
		// TODO Auto-generated method stub
		return null;
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
		observableEvent.setMark(EMarkOfEvent.KEPT);
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
				foundEvent.initMark();
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
		for (Integer i : list) {
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
			if (aEventCheck.getState().getBeforeState()==null||!aEventCheck.getState().getBeforeState().equals(
					aEvent.getState().getAfterState()))// aEventCheck.getState().
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
		observableEvent.setMark(EMarkOfEvent.KEPT);
		averageTime = currentTime;
		isEnd = true;
	}

	public void setEndOfStory() {
		// TODO Auto-generated method stub

	}

	public AbstractStorage extractStorage() {
		HashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWiresHashMap = new HashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>>();
		HashMap<CEvent, HashMap<WireHashKey, AtomicEvent<?>>> wiresByEvent = new HashMap<CEvent, HashMap<WireHashKey, AtomicEvent<?>>>();

		for (Entry<WireHashKey, TreeMap<Long, CEvent>> entry : storage
				.entrySet()) {
			WireHashKey wHK = entry.getKey();
			if (wHK.getKeyOfState() == ETypeOfWire.INTERNAL_STATE) {
				if(wHK.getSiteId()==null){
					System.out.println("gkjfadfh");
				}
				if (internalStates.get(wHK.getSiteId()) == null) {
					internalStates.put(wHK.getSiteId(), new HashSet<Integer>());
				}
			}
			TreeMap<Long, AtomicEvent<?>> trMap = new TreeMap<Long, AtomicEvent<?>>();
			for (Entry<Long, CEvent> en : entry.getValue().entrySet()) {
				CEvent cE = en.getValue();
				AtomicEvent<?> aE = cE.getAtomicEvents().get(wHK);
				if (wHK.getKeyOfState() == ETypeOfWire.INTERNAL_STATE) {
					internalStates.get(wHK.getSiteId()).add(
							(Integer) aE.getState().getBeforeState());
				}
				trMap.put(en.getKey(), aE);
				if (wiresByEvent.get(cE) == null) {
					wiresByEvent.put(cE,
							new HashMap<WireHashKey, AtomicEvent<?>>());
				}
				wiresByEvent.get(cE).put(wHK, aE);
			}
			storageWiresHashMap.put(wHK, trMap);
		}

		AbstractStorage aS = new AbstractStorage(storageWiresHashMap,
				wiresByEvent, observableEvent, internalStates);
		return aS;
	}

}
