package com.plectix.simulator.components.stories.storage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.plectix.simulator.components.stories.compressions.CompressionPassport;
import com.plectix.simulator.components.stories.compressions.Compressor;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.EState;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;
import com.plectix.simulator.stories.StoryCorrectness;

public class AbstractStorage implements IWireStorage {

	// wireHashKey - wireId
	// Long - number of Event
	private LinkedHashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWires;

	private Set<CEvent> events;

	private LinkedHashMap<WireHashKey, LinkedHashSet<Integer>> internalStatesByWire;

	private LinkedHashMap<WireHashKey, LinkedHashSet<CStateOfLink>> linkStatesByWire = new LinkedHashMap<WireHashKey, LinkedHashSet<CStateOfLink>>();

	private LinkedHashMap<WireHashKey, Integer> numberOfUnresolvedEventOnWire;

	// initial solution we interpret as initial event. It has Id= -1
	private CEvent initialEvent;

	// goal of the story. We interests about cause for its occurrence
	private CEvent observableEvent;
	private double averageTime;
	private final int iteration;

	// At the beginning of process we keep all events.
	// isEnd means that observable event happened
	private boolean isEnd;

	// may be non-compression, weak compression, strong compression
	private SimulationArguments.StoryCompressionMode compressionMode;

	// need for refactoring...
	private StoragePassport passport;

	public AbstractStorage(StoryCompressionMode storifyMode, int iteration) {
		this.compressionMode = storifyMode;
		this.iteration = iteration;

		// this.compressionMode = StoryCompressionMode;
		storageWires = new LinkedHashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>>();
		events = new LinkedHashSet<CEvent>();
		internalStatesByWire = new LinkedHashMap<WireHashKey, LinkedHashSet<Integer>>();
		numberOfUnresolvedEventOnWire = new LinkedHashMap<WireHashKey, Integer>();


		observableEvent = null;
		initialEvent = null;
		isEnd = false;
	}

	public IEventIterator eventIterator(WireHashKey wkey, Long first,
			boolean reverse) throws StoryStorageException {

		return new CEventIteratorOnWire(storageWires.get(wkey), first, reverse);
	}

	public AtomicEvent<?> getAtomicEvent(WireHashKey wkey, Long event) {
		return storageWires.get(wkey).get(event);
	}

	public CEvent getEvent(WireHashKey wkey, Long event) {
		return storageWires.get(wkey).get(event).getContainer();
	}

	public int getUnresolvedModifyCount(WireHashKey wkey)
			throws StoryStorageException {
		if (wkey == null || numberOfUnresolvedEventOnWire == null
				|| numberOfUnresolvedEventOnWire.get(wkey) == null) {
			throw new StoryStorageException("wire = null");
		}
		return numberOfUnresolvedEventOnWire.get(wkey);

	}

	/**
	 * mark all events (without initial event with stepId=-1)
	 */
	public void markAllUnresolved() throws StoryStorageException {
		concordWires();
		for (CEvent event : events) {
			if (event == null) {
				continue;
			}
			if (event.getStepId() != -1) {
				event.setMarkUnresolved(this);
			}
		}
	}

	public boolean markAllUnresolvedAsDeleted() throws StoryStorageException {
		boolean deleted = false;
		for (CEvent event : events) {
			if (event.getMark() == EMarkOfEvent.UNRESOLVED) {
				event.setMark(EMarkOfEvent.DELETED, this);
				deleted = true;
			}
		}
		return deleted;
	}

	/**
	 * 
	 * changes number of unresolved events on wires
	 */
	public void markEvent(CEvent event, EMarkOfEvent mark)
			throws StoryStorageException {
		event.setMark(mark, this);
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

	// public IAtomicEventIterator wireAtomicEventIterator(CEvent event) {
	// return new CIteratorAEventWithinEvent(event.getAtomicEvents());
	// }

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

	public Iterator<CStateOfLink> wireLinkStateIterator(WireHashKey wkey) throws StoryStorageException {
		if (wkey.getTypeOfWire() == ETypeOfWire.LINK_STATE) {
			if (linkStatesByWire.get(wkey) == null) {
				throw new StoryStorageException("empty link states ^(");
			}
			return linkStatesByWire.get(wkey).iterator();
		} else {
			throw new StoryStorageException(
			"wireLinkStateIterator : prohibit type of wire");
		}
	}

	public IEventIterator eventIterator(WireHashKey wkey, boolean reverse)
			throws StoryStorageException {
		if (reverse) {
			return new CEventIteratorOnWire(storageWires.get(wkey),
					storageWires.get(wkey).lastKey(), reverse);
		} else {
			return new CEventIteratorOnWire(storageWires.get(wkey),
					storageWires.get(wkey).firstKey(), reverse);

		}

	}

	public void setAverageTime(double averageTime) {
		this.averageTime = averageTime;
	}

	public void addLastEventContainer(CEvent eventContainer, double currentTime)
			throws StoryStorageException {
		observableEvent = eventContainer;
		averageTime = currentTime;
		isEnd = true;
		addEventContainer(eventContainer, false);
	}

	public void addEventContainer(CEvent eventContainer, boolean putToSecondMap)
			throws StoryStorageException {

		if (putToSecondMap) {
			// Map<WireHashKey, AtomicEvent<?>> wiresMap = new
			// LinkedHashMap<WireHashKey, AtomicEvent<?>>();

			// if (!isOpposite(eventContainer))
			for (WireHashKey key : eventContainer.getAtomicEvents().keySet()) {
				TreeMap<Long, AtomicEvent<?>> tree = storageWires.get(key);
				if (tree == null) {
					tree = new TreeMap<Long, AtomicEvent<?>>();
					storageWires.put(key, tree);
				}
				tree.put(eventContainer.getStepId(), eventContainer
						.getAtomicEvent(key));
				// wiresMap.put(key, eventContainer.getAtomicEvent(key));
				// wiresByEvent.put(eventContainer, wiresMap);
				events.add(eventContainer);
				// wiresByEvent.put(eventContainer,
				// eventContainer.getAtomicEvents());

				if (key.getTypeOfWire() == ETypeOfWire.INTERNAL_STATE) {
					if (internalStatesByWire.get(key) == null) {
						LinkedHashSet<Integer> internalStates = new LinkedHashSet<Integer>();
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
				} else if (key.getTypeOfWire() == ETypeOfWire.LINK_STATE) {
					if (linkStatesByWire.get(key) == null) {
						LinkedHashSet<CStateOfLink> linkStates = new LinkedHashSet<CStateOfLink>();
						linkStatesByWire.put(key, linkStates);
					}
					switch (eventContainer.getAtomicEvent(key).getType())
					{
					case TEST_AND_MODIFICATION:
						CStateOfLink before = (CStateOfLink) (eventContainer.getAtomicEvent(key)
								.getState().getBeforeState());
						
						if (before != null && !before.isFree())
							linkStatesByWire.get(key).add(before);
					case MODIFICATION:
						CStateOfLink after = (CStateOfLink) (eventContainer.getAtomicEvent(key)
								.getState().getAfterState());
						
						if (after != null && !after.isFree())
							linkStatesByWire.get(key).add(after);
						break;
					}
				} 
			}

		} else {

			if (!tryToRemoveOppositeBlock(eventContainer)) {
				for (WireHashKey key : eventContainer.getAtomicEvents()
						.keySet()) {
					TreeMap<Long, AtomicEvent<?>> tree = storageWires.get(key);
					if (tree == null) {
						tree = new TreeMap<Long, AtomicEvent<?>>();
						storageWires.put(key, tree);

					}
					tree.put(eventContainer.getStepId(), eventContainer
							.getAtomicEvent(key));
				}
			}

		}
	}

	private boolean tryToRemoveOppositeBlock(CEvent eventIn) {
		// may be need for all non-observable events
		if (isEnd) {
			return false;
		}
		LinkedHashMap<WireHashKey, AtomicEvent<?>> mapIn = getModificationAction(eventIn);
		Long stepId = eventIn.getStepId();
		LinkedHashSet<WireHashKey> set = new LinkedHashSet<WireHashKey>();
		LinkedHashSet<CEvent> listForDel = new LinkedHashSet<CEvent>();
		WireHashKey key;

		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : mapIn.entrySet()) {
			key = entry.getKey();
			AtomicEvent<?> aEvent = entry.getValue();

			AtomicEvent<?> aEventCheck = getAtomicLastModificationAtomicEvent(
					key, aEvent, stepId);
			if (aEventCheck == null)
				return false;
			if (aEventCheck.getState().getBeforeState() == null
					|| !aEventCheck.getState().getBeforeState().equals(
							aEvent.getState().getAfterState()))
				return false;

			set.addAll(getModificationAction(aEventCheck.getContainer())
					.keySet());

			listForDel.add(aEventCheck.getContainer());
		}

		if (mapIn.size() != set.size())
			return false;

		for (CEvent e : listForDel) {
			for (WireHashKey wk : e.getAtomicEvents().keySet()) {
				storageWires.get(wk).remove(e.getStepId());
				if (storageWires.get(wk).size() == 0) {
					storageWires.remove(wk);
				}
			}
		}
		return true;
	}

	private LinkedHashMap<WireHashKey, AtomicEvent<?>> getModificationAction(
			CEvent eventIn) {
		LinkedHashMap<WireHashKey, AtomicEvent<?>> mapIn = new LinkedHashMap<WireHashKey, AtomicEvent<?>>();
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

	/**
	 * return true if there is finished story ^)
	 */
	public boolean isImportantStory() {
		return isEnd;
	}

	// It needs change LinkedHashSet<CEvent> -> LinkedHashSet<Long>
	private void handling(CEvent event, LinkedHashSet<CEvent> needEvents)
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

	private CEvent findCausing(WireHashKey key, AtomicEvent<?> event,
			Long stepId) throws StoryStorageException {

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
		LinkedHashSet<CEvent> needEvents = new LinkedHashSet<CEvent>();
		needEvents.add(observableEvent);
		handling(observableEvent, needEvents);
		clearStorage(needEvents);
		extractPassportMock();
		if (observableEvent != null && initialEvent != null) {
			Compressor compressor = new Compressor(this);
			compressor.execute(compressionMode);

		}
	}

	private int numberOfKept() {
		if (observableEvent.getMark() != EMarkOfEvent.KEPT) {
			return events.size();
		} else {
			int i = 0;
			for (CEvent event : events) {
				if (event.getMark() == EMarkOfEvent.KEPT
						&& event.getStepId() != -1) {
					i++;
				}
			}
			return i;
		}
	}

	private void clearStorage(LinkedHashSet<CEvent> needEvents)
			throws StoryStorageException {

		storageWires = new LinkedHashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>>();
		events = new LinkedHashSet<CEvent>();
		for (CEvent event : needEvents) {
			addEventContainer(event, true);
		}

		initInitial();
		concordWires();
	}

	private void initInitial() throws StoryStorageException {

		initialEvent = new CEvent(-1, -1);
		AtomicEvent<?> initialAEvent;
		Long p;
		int temp = 0;

		Map<WireHashKey, AtomicEvent<?>> map = new LinkedHashMap<WireHashKey, AtomicEvent<?>>();
		for (WireHashKey key : storageWires.keySet()) {

			p = storageWires.get(key).firstKey();
			initialAEvent = storageWires.get(key).get(p);
			//
			if (initialAEvent.getState().getBeforeState() == null) {
				continue;
			}

			p = Long.valueOf(0);
			if (initialEvent.getAtomicEvents().get(key) == null) {
				AtomicEvent<?> initialAEvent2;
				initialAEvent2 = initialAEvent.cloneWithBefore(initialEvent);
				map.put(key, initialAEvent2);

				initialEvent.addToFilter(key);
				temp++;

				initialEvent.getAtomicEvents().put(key, initialAEvent2);
				if (storageWires.get(key) == null) {
					throw new StoryStorageException("");
				}
				storageWires.get(key).put(Long.valueOf(-1), initialAEvent2);
				if (initialAEvent.getType() == EActionOfAEvent.TEST
						&& key.getTypeOfWire() == ETypeOfWire.INTERNAL_STATE) {
					internalStatesByWire.get(key).add(
							(Integer) (initialAEvent.getState()
									.getBeforeState()));

				}
				if (initialAEvent.getType() == EActionOfAEvent.TEST
						&& key.getTypeOfWire() == ETypeOfWire.LINK_STATE) {
					linkStatesByWire.get(key).add(
							(CStateOfLink) (initialAEvent.getState()
									.getBeforeState()));

				}

			}
		}
		if (temp > 0) {
			initialEvent.setMark(EMarkOfEvent.KEPT, this);
		} else {
			// TODO remove initialEvent from storage
			// initialEvent.setMark(null, this);
		}

		events.add(initialEvent);
	}

	private void concordWires() {
		for (WireHashKey wKey : storageWires.keySet()) {
			putUnresolvedModifyEvent(wKey, 0);
		}
	}

	public Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> getStorageWires() {
		return storageWires;
	}

	// public Map<CEvent, Map<WireHashKey, AtomicEvent<?>>> getWiresByEvent() {
	// return wiresByEvent;
	// }
	public Set<CEvent> getEvents() {
		return events;
	}

	public void putUnresolvedModifyEvent(WireHashKey wireHashKey,
			Integer valueOf) {
		numberOfUnresolvedEventOnWire.put(wireHashKey, valueOf);

	}

	public void extractPassportMock() {
		passport = new StoragePassport(this);
	}

	/**
	 * return true if removed successfully else remove nothing i think that
	 * arraylist - wires from one agent
	 */
	public boolean removeWire(ArrayList<WireHashKey> arrayList) {
		boolean removed = true;
		boolean deleteInitial=false;
		for (WireHashKey wk : arrayList) {
			TreeMap<Long, AtomicEvent<?>> w = storageWires.get(wk);
			if (!w.isEmpty()) {
				if (w.size() > 1 || w.firstKey() != -1)
				{
					removed = false;
					break;
				}
				if(!deleteInitial){
					deleteInitial = true;
				}
			}
		}
		if(deleteInitial){
			for(WireHashKey wk : arrayList){
				if(wk.getTypeOfWire()==ETypeOfWire.BOUND_FREE){
					if((EState)(storageWires.get(wk).get(Long.valueOf(-1)).getState().getAfterState())!=EState.FREE_LINK_STATE){
						removed=false;
					}
				}
			}
		}
		
		if (removed) {
			for (WireHashKey wk : arrayList) {
				storageWires.remove(wk);
				internalStatesByWire.remove(wk);
				linkStatesByWire.remove(wk);
				numberOfUnresolvedEventOnWire.remove(wk);
				initialEvent.removeWire(wk);
			}
		}
		return removed;

	}

	public CompressionPassport extractPassport() {
		return passport;
	}

	// atomicEvents from wk1 to wk2, first event
	// change storageWires,internalStatesByWire,numberOfUnresolvedEventOnWire

	public void replaceWireToWire(Map<WireHashKey, WireHashKey> map,
			Long firstEventId, boolean swapTop,
			TreeMap<Long, AtomicEvent<?>> allEventsByNumber)
			throws StoryStorageException {

		LinkedHashSet<Long> stepIdOfEvents = new LinkedHashSet<Long>();

		for (WireHashKey wk : map.keySet()) {

			
			stepIdOfEvents.addAll(stepIdEventsOnWire(wk, firstEventId, swapTop));
			stepIdOfEvents.addAll(stepIdEventsOnWire(map.get(wk), firstEventId, swapTop));
			


		}

		//StoryCorrectness.testAll(this);
		for (Long number : stepIdOfEvents) {

			if(number==null){
				System.out.println("dsfaklsdfdfgdfgadfgasdfghsgh");
			}
			if(allEventsByNumber.get(number)==null){
				System.out.println(number);
				System.out.println(observableEvent.getStepId());
			}
			if(allEventsByNumber.get(number).getContainer()==null){
				System.out.println("adsfhkal");
			}
			//StoryCorrectness.testAll(this);
			// rebuild event and numberOfUnresolvedevents on wires
			List<PointRound> changes = allEventsByNumber.get(number)
					.getContainer().exchangeWires(map, this);
			correctChanges(changes);
			//StoryCorrectness.testAll(this);
		}

	}

	private Set<Long> stepIdEventsOnWire(WireHashKey wk, Long firstEventId,
			boolean swapTop) {

		if (swapTop) {
			return storageWires.get(wk).subMap(Long.valueOf(-1),
					firstEventId + 1).keySet();
		} else {
			return storageWires.get(wk).subMap(firstEventId,
					observableEvent.getStepId() + 1).keySet();
		}
	}

	// change storageWires,internalStatesByWire
	private void correctChanges(List<PointRound> changes)
			throws StoryStorageException {
		for (PointRound pr : changes) {
			Long stepIdOfEvent = pr.number;
			if (storageWires.get(pr.wk1).get(stepIdOfEvent) == null
					&& storageWires.get(pr.wk2).get(stepIdOfEvent) == null) {
				throw new StoryStorageException("empty change");
			}

			AtomicEvent<?> ae1 = storageWires.get(pr.wk1).remove(stepIdOfEvent);
			AtomicEvent<?> ae2 = storageWires.get(pr.wk2).remove(stepIdOfEvent);
			
			
			if (ae1 == null) {
				storageWires.get(pr.wk1).put(stepIdOfEvent, ae2);
				continue;

			}

			if (ae2 == null) {
				storageWires.get(pr.wk2).put(stepIdOfEvent, ae1);
				continue;
			}

			storageWires.get(pr.wk1).put(stepIdOfEvent, ae2);
			storageWires.get(pr.wk2).put(stepIdOfEvent, ae1);

		}

	}

	public void markAllNull() {
		for (WireHashKey wk : storageWires.keySet()) {
			numberOfUnresolvedEventOnWire.put(wk, 0);
		}
		for (CEvent event : events) {
			if (event.getStepId() != -1) {
				event.onlySetMark(null);
			}
		}

	}
	public double getAverageTime() {
		return averageTime;
	}
	
	public int getIteration(){
		return iteration;
	}

}
