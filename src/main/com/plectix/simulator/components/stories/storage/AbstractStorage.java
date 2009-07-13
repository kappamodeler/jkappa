package com.plectix.simulator.components.stories.storage;

import java.awt.Container;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.stories.compressions.Compressor;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;

public class AbstractStorage implements IWireStorage {

	// wireHashKey - wireId
	// Long - number of Event
	private HashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWires;
	private HashMap<CEvent, Map<WireHashKey, AtomicEvent<?>>> wiresByEvent;
	private HashMap<WireHashKey, HashSet<Integer>> internalStatesByWire;
	
	 private HashMap<WireHashKey,Integer> numberOfUnresolvedEventOnWire;

	// initial solution we interpret as initial event. It has Id= -1
	private CEvent initialEvent;

	// goal of the story. We interests about cause for its occurrence
	private CEvent observableEvent;
	private double averageTime;

	// At the beginning of process we keep all events.
	// isEnd means that observable event happened
	private boolean isEnd;

	// may be non-compression, weak compression, strong compression
	private SimulationArguments.StoryCompressionMode compressionMode;

	public AbstractStorage(StoryCompressionMode storifyMode) {
		this.compressionMode = storifyMode;
		storageWires = new HashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>>();
		wiresByEvent = new HashMap<CEvent, Map<WireHashKey, AtomicEvent<?>>>();
		internalStatesByWire = new HashMap<WireHashKey, HashSet<Integer>>();
		numberOfUnresolvedEventOnWire = new HashMap<WireHashKey,Integer> ();

		observableEvent = null;
		initialEvent = null;
		isEnd = false;
	}

	public IEventIterator eventIterator(WireHashKey wkey, Long first,
			boolean reverse) throws StoryStorageException {

		return new CEventIteratorOnWire(storageWires.get(wkey), wkey, first,
				reverse);
	}

	public AtomicEvent<?> getAtomicEvent(WireHashKey wkey, Long event) {
		return storageWires.get(wkey).get(event);
	}

	public CEvent getEvent(WireHashKey wkey, Long event) {
		return storageWires.get(wkey).get(event).getContainer();
	}

	public int getUnresolvedModifyCount(WireHashKey wkey) {
		return numberOfUnresolvedEventOnWire.get(wkey);
		
	}

	public void markAllUnresolved() throws StoryStorageException {
		for (CEvent event : wiresByEvent.keySet()) {
			if (event.getStepId() != -1) {
				event.setMarkUnresolved(this);
			}
		}
	}

	public void markAllUnresolvedAsDeleted() throws StoryStorageException {
		for (CEvent event : wiresByEvent.keySet()) {
			if (event.getMark() == EMarkOfEvent.UNRESOLVED) {
				event.setMark(EMarkOfEvent.DELETED,this);
			}
		}
	}

	// changes number of unresolved events on wires
	public void markEvent(CEvent event, EMarkOfEvent mark)
			throws StoryStorageException {
		event.setMark(mark,this);
	}

	public CEvent initialEvent() throws StoryStorageException {
		if (initialEvent == null) {
			throw new StoryStorageException("initial event is null!");
		}
		return initialEvent;
	}
	
	public CEvent observableEvent() throws StoryStorageException {
		if (observableEvent == null) {
			throw new StoryStorageException("observable event is null!");
		}
		return observableEvent;
	}


	public IAtomicEventIterator wireAtomicEventIterator(CEvent event) {
		return new CIteratorAEventWithinEvent(wiresByEvent.get(event));
	}

	public Iterator<Integer> wireInternalStateIterator(WireHashKey wkey)
			throws StoryStorageException {

		if (wkey.getTypeOfWire() == ETypeOfWire.INTERNAL_STATE) {
			if (internalStatesByWire.get(wkey) == null) {
				throw new StoryStorageException("empty internal states ^(");
			}
			return internalStatesByWire.get(wkey).iterator();
		} else {
			throw new StoryStorageException(
					"wireInternalStateIterator : prohibit type of wire");
		}
	}

	public IEventIterator eventIterator(WireHashKey wkey, boolean reverse)
			throws StoryStorageException {
		if (reverse) {
			return new CEventIteratorOnWire(storageWires.get(wkey), wkey,
					storageWires.get(wkey).lastKey(), reverse);
		} else {
			return new CEventIteratorOnWire(storageWires.get(wkey), wkey, Long
					.valueOf(-1), reverse);

		}

	}

	public void setAverageTime(double averageTime) {
		this.averageTime = averageTime;
	}

	public void addLastEventContainer(CEvent eventContainer, double currentTime)
			throws StoryStorageException {
		addEventContainer(eventContainer, false);
		observableEvent = eventContainer;
		averageTime = currentTime;
		isEnd = true;
	}

	public void addEventContainer(CEvent eventContainer, boolean putToSecondMap)
			throws StoryStorageException {
		// TODO add check opposite
		// eventContainer.clearsLinkStates();
		if (putToSecondMap) {
			Map<WireHashKey, AtomicEvent<?>> wiresMap = new HashMap<WireHashKey, AtomicEvent<?>>();
			if (eventContainer.getStepId() != -1) {
				addInitialState(eventContainer);
			}
			// if (!isOpposite(eventContainer))
			for (WireHashKey key : eventContainer.getAtomicEvents().keySet()) {
				TreeMap<Long, AtomicEvent<?>> tree = storageWires.get(key);
				if (tree == null) {
					tree = new TreeMap<Long, AtomicEvent<?>>();
					storageWires.put(key, tree);
				}
				tree.put(eventContainer.getStepId(), eventContainer
						.getAtomicEvent(key));
				wiresMap.put(key, eventContainer.getAtomicEvent(key));
				wiresByEvent.put(eventContainer, wiresMap);
				// wiresByEvent.put(eventContainer,
				// eventContainer.getAtomicEvents());

				if (key.getTypeOfWire() == ETypeOfWire.INTERNAL_STATE) {
					if (internalStatesByWire.get(key) == null) {
						HashSet<Integer> internalStates = new HashSet<Integer>();
						internalStatesByWire.put(key, internalStates);
					}
					if (eventContainer.getAtomicEvent(key).getType() == EActionOfAEvent.TEST_AND_MODIFICATION) {
						internalStatesByWire.get(key).add(
								(Integer) (eventContainer.getAtomicEvent(key)
										.getState().getAfterState()));
						internalStatesByWire.get(key).add(
								(Integer) (eventContainer.getAtomicEvent(key)
										.getState().getBeforeState()));
					}
					if (eventContainer.getAtomicEvent(key).getType() == EActionOfAEvent.MODIFICATION) {
						internalStatesByWire.get(key).add(
								(Integer) (eventContainer.getAtomicEvent(key)
										.getState().getAfterState()));
					}
				}
			}
			// addInitialState(eventContainer);

		} else {

			//if (!tryToRemoveOppositeBlock(eventContainer)) {
			for (WireHashKey key : eventContainer.getAtomicEvents().keySet()) {
				TreeMap<Long, AtomicEvent<?>> tree = storageWires.get(key);
				if (tree == null) {
					tree = new TreeMap<Long, AtomicEvent<?>>();
					storageWires.put(key, tree);

				}
				tree.put(eventContainer.getStepId(), eventContainer
						.getAtomicEvent(key));
		     //}
			}

		}
	}

	private boolean tryToRemoveOppositeBlock(CEvent eventIn) {
		if(isEnd){
			return false;
		}
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
			if (aEventCheck.getState().getBeforeState() == null
					|| !aEventCheck.getState().getBeforeState().equals(
							aEvent.getState().getAfterState()))// aEventCheck.
				// getState().
				return false;
			set.addAll(getModificationAction(aEventCheck.getContainer())
					.keySet());

			listForDel.add(aEventCheck.getContainer());
		}

		if (mapIn.size() != set.size())
			return false;

		for (CEvent e : listForDel) {
			for (WireHashKey key : e.getAtomicEvents().keySet()) {
				storageWires.get(key).remove(e.getStepId());
				if (storageWires.get(key).size() == 0) {
					storageWires.remove(key);
				}
			}
		}
		return true;
	}

	private HashMap<WireHashKey, AtomicEvent<?>> getModificationAction(
			CEvent eventIn) {
		HashMap<WireHashKey, AtomicEvent<?>> mapIn = new HashMap<WireHashKey, AtomicEvent<?>>();
		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : eventIn
				.getAtomicEvents().entrySet()) {
			AtomicEvent<?> aEvent = entry.getValue();
			if (aEvent.getType() != EActionOfAEvent.TEST)
				mapIn.put(entry.getKey(), aEvent);

		}
		return mapIn;
	}

	private AtomicEvent<?> getAtomicLastModificationAtomicEvent(
			WireHashKey key, AtomicEvent<?> aEventIn, Long stepId) {
		TreeMap<Long, AtomicEvent<?>> wire = storageWires.get(key);

		if (wire == null)
			return null;

		stepId = wire.lowerKey(stepId);

		if (stepId != null) {
			AtomicEvent<?> nextAevent = wire.get(stepId);

			if (nextAevent == null
					|| nextAevent.getType() != EActionOfAEvent.TEST_AND_MODIFICATION) {
				return null;
			} else {
				return nextAevent;
			}
		}
		return null;
	}

	// return true if there is finished story ^)
	public boolean isImportantStory() {
		return isEnd;
	}

	// It needs change HashSet<CEvent> -> HashSet<Long>
	private void handling(CEvent event, HashSet<CEvent> needEvents)
			throws StoryStorageException {
		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : event
				.getAtomicEvents().entrySet()) {
			WireHashKey key = entry.getKey();
			AtomicEvent<?> aEvent = entry.getValue();

			if (!storageWires.get(key).containsValue(aEvent)) {
				throw new StoryStorageException("");
			}

			CEvent foundEvent = findCausing(key, aEvent, event.getStepId());
			if (foundEvent != null && !needEvents.contains(foundEvent)) {
				needEvents.add(foundEvent);
				handling(foundEvent, needEvents);
			}
		}
	}

	private void addInitialState(CEvent event) throws StoryStorageException {
		if (initialEvent == null) {
			initialEvent = new CEvent(-1, -1);
		}
		for (WireHashKey key : event.getAtomicEvents().keySet()) {
			AtomicEvent<?> initialAEvent;
			//
			if (event.getAtomicEvent(key).getState().getBeforeState() == null) {
				continue;
			}
			if (initialEvent.getAtomicEvents().get(key) == null) {
				initialAEvent = event.getAtomicEvent(key).cloneWithBefore(
						initialEvent);
				if (key.getTypeOfWire() != ETypeOfWire.BOUND_FREE)
					initialEvent.addToFilter(key);
				initialEvent.getAtomicEvents().put(key, initialAEvent);
				if (storageWires.get(key) == null) {
					TreeMap<Long, AtomicEvent<?>> newMap = new TreeMap<Long, AtomicEvent<?>>();
					storageWires.put(key, newMap);
				}
				storageWires.get(key).put(Long.valueOf(-1), initialAEvent);
				continue;
			}

			Long x = storageWires.get(key).higherKey(Long.valueOf(-1));
			if (x < event.getStepId())
				continue;
			if (x == event.getStepId())
				throw new StoryStorageException("initial states");

			if (storageWires.get(key).get(x).getState().getBeforeState() != null) {
				initialAEvent = event.getAtomicEvent(key).cloneWithBefore(
						initialEvent);
				initialEvent.getAtomicEvents().put(key, initialAEvent);
				storageWires.get(key).put(Long.valueOf(-1), initialAEvent);
			} else {
				storageWires.get(key).remove(Long.valueOf(-1));
				initialEvent.getAtomicEvents().remove(key);
			}

		}
	}

	// // hren kakaja-to TODO
	//
	// private void addInitialState(CEvent event) {
	// if (initialEvent == null) {
	// initialEvent = new CEvent(-1, -1);
	// // try {
	// // initialEvent.setMark(EMarkOfEvent.KEPT);
	// // } catch (StoryStorageException e) {
	// // e.printStackTrace();
	// // }
	// }
	// for (WireHashKey key : event.getAtomicEvents().keySet()) {
	// if (initialEvent.getAtomicEvents().get(key) != null)
	// continue;
	// // CEvent firstEvent =
	// // storageWires.get(key).firstEntry().getValue();
	// AtomicEvent<?> firstAEvent = storageWires.get(key).firstEntry()
	// .getValue();
	// // AtomicEvent<?> firstAEvent =
	// // firstEvent.getAtomicEvents().get(key);
	// if (firstAEvent.getState().getBeforeState() != null) {
	// AtomicEvent<?> initialAEvent = firstAEvent
	// .cloneWithBefore(initialEvent);
	// initialEvent.getAtomicEvents().put(key, initialAEvent);
	// storageWires.get(key).put(Long.valueOf(-1), initialAEvent);
	// // wiresByEvent.put(initialEvent,
	// // initialEvent.getAtomicEvents());
	// }
	// }
	// }

	private CEvent findCausing(WireHashKey key, AtomicEvent<?> event,
			Long stepId) throws StoryStorageException {
		// TODO
		TreeMap<Long, AtomicEvent<?>> wire = storageWires.get(key);
		if (wire == null) {
			throw new StoryStorageException(" Wire = null!!!! and key = " + key);
		}
		stepId = wire.lowerKey(stepId);
		if (stepId == null)
			return event.getContainer();

		AtomicEvent<?> nextAevent = wire.get(stepId);
		if (stepId != null) {
			while (nextAevent != null
					&& nextAevent.getType() == EActionOfAEvent.TEST) {
				stepId = wire.lowerKey(stepId);
				if (stepId == null)
					return event.getContainer();
				nextAevent = wire.get(stepId);
			}
		}
		return nextAevent.getContainer();

	}

	/**
	 * not implemented
	 */
	public void clearList() {
		storageWires.clear();
	}

	/**
	 * not implemented
	 * 
	 * @return
	 */
	public void setEndOfStory() {
		// TODO Auto-generated method stub

	}

	public void handling() throws StoryStorageException {
		HashSet<CEvent> needEvents = new HashSet<CEvent>();
		needEvents.add(observableEvent);
		handling(observableEvent, needEvents);
		clearStorage(needEvents);
		if (observableEvent != null && initialEvent != null) {
			Compressor compressor = new Compressor(this);
			compressor.execute(compressionMode);
		}
	}

	private void clearStorage(HashSet<CEvent> needEvents)
			throws StoryStorageException {
		storageWires = new HashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>>();
		wiresByEvent = new HashMap<CEvent, Map<WireHashKey, AtomicEvent<?>>>();
		for (CEvent event : needEvents) {

			addEventContainer(event, true);
		}

		addEventContainer(initialEvent, true);
		concordWires();
		//initialEvent.setMark(EMarkOfEvent.KEPT);
		
	}

	private void concordWires() {
		initialEvent.onlySetMark(EMarkOfEvent.KEPT);
		for(WireHashKey wKey : storageWires.keySet()){
			putUnresolvedModifyEvent(wKey,0);
		}
	}

	public Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> getStorageWires() {
		return storageWires;
	}


	@Override
	public Iterator<Long> agentIterator(int typeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Integer> agentTypeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEventIterator eventIterator(boolean reverse)
			throws StoryStorageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWireStorage swapAgents(Long[] agents1, Long[] agents2,
			Long firstEventId, boolean swapTop) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<CEvent, Map<WireHashKey, AtomicEvent<?>>> getWiresByEvent() {
		return wiresByEvent;
	}

	@Override
	public void putUnresolvedModifyEvent(WireHashKey wireHashKey, Integer valueOf) {
		numberOfUnresolvedEventOnWire.put(wireHashKey, valueOf);
		// TODO Auto-generated method stub
		
	}


}
