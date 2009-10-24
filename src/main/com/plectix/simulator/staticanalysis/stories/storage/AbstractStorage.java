package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;
import com.plectix.simulator.staticanalysis.stories.MarkOfEvent;
import com.plectix.simulator.staticanalysis.stories.State;
import com.plectix.simulator.staticanalysis.stories.TypeOfWire;
import com.plectix.simulator.staticanalysis.stories.compressions.CompressionPassport;
import com.plectix.simulator.staticanalysis.stories.compressions.ExtensionData;

public final class AbstractStorage implements WireStorageInterface {

	// wireHashKey - wireId
	// Long - number of Event
	private LinkedHashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWires;

	private Set<Event> events;

	private MasterInformationAboutWires informationAboutWires;

	// initial solution we interpret as initial event. It has Id= -1
	private Event initialEvent;
	// goal of the story. We interests about cause for its occurrence
	private Event observableEvent;
	private double averageTime;
	private final int iteration;
	// need for refactoring...
	private StoragePassport passport;
	private final StoriesAgentTypesStorage storiesAgentTypesStorage;

	private StoryBuilder builder;

	public AbstractStorage(int iteration, StoriesAgentTypesStorage typeById) {
		this.iteration = iteration;
		storageWires = new LinkedHashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>>();
		events = new LinkedHashSet<Event>();
		observableEvent = null;
		initialEvent = null;
		builder = new StoryBuilder();
		this.storiesAgentTypesStorage = typeById;
		informationAboutWires = new MasterInformationAboutWires(
				new LinkedHashMap<WireHashKey, LinkedHashSet<String>>(),
				new LinkedHashMap<WireHashKey, LinkedHashSet<StateOfLink>>(),
				new LinkedHashMap<WireHashKey, Integer>());
	}

	public final EventIteratorInterface eventIterator(WireHashKey wkey,
			Long first, boolean reverse) throws StoryStorageException {

		return new EventIteratorOnWire(storageWires.get(wkey), first, reverse);
	}

	public final AtomicEvent<?> getAtomicEvent(WireHashKey wkey, Long event) {
		return storageWires.get(wkey).get(event);
	}

	public final Event getEvent(WireHashKey wkey, Long event) {
		return storageWires.get(wkey).get(event).getContainer();
	}


	/**
	 * mark all events (without initial event with stepId=-1)
	 */
	public final void markAllUnresolved() throws StoryStorageException {
		concordWires();
		for (Event event : events) {
			if (event == null) {
				continue;
			}
			if (event.getStepId() != -1) {
				event.setMarkUnresolved(informationAboutWires);
			}
		}
	}

	public final boolean markAllUnresolvedAsDeleted()
			throws StoryStorageException {
		boolean deleted = false;
		for (Event event : events) {
			if (event.getMark() == MarkOfEvent.UNRESOLVED) {
				event.setMark(MarkOfEvent.DELETED, informationAboutWires);
				deleted = true;
			}
		}
		return deleted;
	}

	public final Event initialEvent() throws StoryStorageException {
		if (initialEvent == null) {
			throw new StoryStorageException("initial event is null!");
		}
		return initialEvent;
	}

	public final Event observableEvent() throws StoryStorageException {
		if (observableEvent == null) {
			throw new StoryStorageException("observable event is null!");
		}
		return observableEvent;
	}


	public final EventIteratorInterface eventIterator(WireHashKey wkey,
			boolean reverse) throws StoryStorageException {
		if (reverse) {
			return new EventIteratorOnWire(storageWires.get(wkey), storageWires
					.get(wkey).lastKey(), reverse);
		} else {
			return new EventIteratorOnWire(storageWires.get(wkey), storageWires
					.get(wkey).firstKey(), reverse);

		}

	}

	public final void addLastEventContainer(Event eventContainer,
			double currentTime) throws StoryStorageException {
		observableEvent = eventContainer;
		averageTime = currentTime;
		builder.setFlagTrue();
		addEventContainer(eventContainer);
		handling();
	}

	public final void addEventContainerAndFullOtherMaps(Event eventContainer)
			throws StoryStorageException {

		// if (!isOpposite(eventContainer))
		for (WireHashKey key : eventContainer.getAtomicEvents().keySet()) {
			TreeMap<Long, AtomicEvent<?>> tree = storageWires.get(key);
			if (tree == null) {
				tree = new TreeMap<Long, AtomicEvent<?>>();
				storageWires.put(key, tree);
			}
			tree.put(eventContainer.getStepId(), eventContainer
					.getAtomicEvent(key));
			events.add(eventContainer);

			if (key.getTypeOfWire() == TypeOfWire.INTERNAL_STATE) {
				if (informationAboutWires.getInternalStatesByWire().get(key) == null) {
					LinkedHashSet<String> internalStates = new LinkedHashSet<String>();
					informationAboutWires.getInternalStatesByWire().put(key,
							internalStates);
				}
				if (eventContainer.getAtomicEvent(key).getType() == ActionOfAEvent.TEST_AND_MODIFICATION) {
					informationAboutWires.getInternalStatesByWire().get(key)
							.add(
									(String) (eventContainer
											.getAtomicEvent(key).getState()
											.getAfterState()));
					informationAboutWires.getInternalStatesByWire().get(key)
							.add(
									(String) (eventContainer
											.getAtomicEvent(key).getState()
											.getBeforeState()));
				}
				if (eventContainer.getAtomicEvent(key).getType() == ActionOfAEvent.MODIFICATION) {
					informationAboutWires.getInternalStatesByWire().get(key)
							.add(
									(String) (eventContainer
											.getAtomicEvent(key).getState()
											.getAfterState()));
				}
			} else if (key.getTypeOfWire() == TypeOfWire.LINK_STATE) {
				if (informationAboutWires.getLinkStatesByWire().get(key) == null) {
					LinkedHashSet<StateOfLink> linkStates = new LinkedHashSet<StateOfLink>();
					informationAboutWires.getLinkStatesByWire().put(key,
							linkStates);
				}
				switch (eventContainer.getAtomicEvent(key).getType()) {
				case TEST_AND_MODIFICATION:
					StateOfLink before = (StateOfLink) (eventContainer
							.getAtomicEvent(key).getState().getBeforeState());

					if (before != null && !before.isFree())
						informationAboutWires.getLinkStatesByWire().get(key)
								.add(before);
				case MODIFICATION:
					StateOfLink after = (StateOfLink) (eventContainer
							.getAtomicEvent(key).getState().getAfterState());

					if (after != null && !after.isFree())
						informationAboutWires.getLinkStatesByWire().get(key)
								.add(after);
					break;
				}
			}
		}

	}

	public final void addEventContainer(Event eventContainer)
			throws StoryStorageException {

		builder.addEventContainer(eventContainer);

	}

	/**
	 * return true if there is finished story ^)
	 */
	public final boolean isImportantStory() {
		return observableEvent != null;
	}

	/**
	 * not implemented
	 */
	public final void clearList() {
		storageWires.clear();

	}


	public final void handling() throws StoryStorageException {
		LinkedHashSet<Event> needEvents = new LinkedHashSet<Event>();
		needEvents.add(observableEvent);
		builder.handling(observableEvent, needEvents);
		builder = null;
		clearStorage(needEvents);
		extractPassportMock();
	}

	/**
	 * full other maps and fields
	 * 
	 * @param needEvents
	 * @throws StoryStorageException
	 */
	protected final void clearStorage(LinkedHashSet<Event> needEvents)
			throws StoryStorageException {

		storageWires = new LinkedHashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>>();
		events = new LinkedHashSet<Event>();
		for (Event event : needEvents) {
			addEventContainerAndFullOtherMaps(event);
		}

		initialize();
		concordWires();
	}

	protected final void initialize() throws StoryStorageException {
		initialEvent = new Event(-1, -1);
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
				if (initialAEvent.getType() == ActionOfAEvent.TEST
						&& key.getTypeOfWire() == TypeOfWire.INTERNAL_STATE) {
					informationAboutWires.getInternalStatesByWire().get(key)
							.add(
									(String) (initialAEvent.getState()
											.getBeforeState()));

				}
				if (initialAEvent.getType() == ActionOfAEvent.TEST
						&& key.getTypeOfWire() == TypeOfWire.LINK_STATE) {
					informationAboutWires.getLinkStatesByWire().get(key).add(
							(StateOfLink) (initialAEvent.getState()
									.getBeforeState()));

				}

			}
		}
		if (temp > 0) {
			initialEvent.setMark(MarkOfEvent.KEPT, informationAboutWires);
			events.add(initialEvent);
		} else {

		}
	}

	private final void concordWires() {
		for (WireHashKey wKey : storageWires.keySet()) {
			informationAboutWires.putUnresolvedModifyEvent(wKey, 0);
		}
	}

	public final Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> getStorageWires() {
		return storageWires;
	}

	public final Set<Event> getEvents() {
		return events;
	}


	public final void extractPassportMock() {
		passport = new StoragePassport(this);
	}

	/**
	 * return true if removed successfully else remove nothing i think that
	 * arraylist - wires from one agent
	 */
	public final boolean removeWire(ArrayList<WireHashKey> wireKeys) {
		boolean removed = true;
		boolean deleteInitial = false;
		for (WireHashKey wk : wireKeys) {
			TreeMap<Long, AtomicEvent<?>> w = storageWires.get(wk);
			if (!w.isEmpty()) {
				if (w.size() > 1 || w.firstKey() != -1) {
					removed = false;
					break;
				}
				if (!deleteInitial) {
					deleteInitial = true;
				}
			}
		}
		if (deleteInitial) {
			for (WireHashKey wk : wireKeys) {
				if (wk.getTypeOfWire() == TypeOfWire.BOUND_FREE) {
					if ((State) (storageWires.get(wk).get(Long.valueOf(-1))
							.getState().getAfterState()) != State.FREE_LINK_STATE) {
						removed = false;
					}
				}
			}
		}
		if (removed) {
			for (WireHashKey wk : wireKeys) {
				storageWires.remove(wk);
				informationAboutWires.removeWire(wk);
				initialEvent.removeWire(wk);
			}
		}
		return removed;

	}

	public final CompressionPassport extractPassport() {
		return passport;
	}

	// atomicEvents from wk1 to wk2, first event
	// change storageWires,internalStatesByWire,numberOfUnresolvedEventOnWire

	public final void replaceWireToWire(Map<WireHashKey, WireHashKey> map,
			Long firstEventId, boolean swapTop,
			TreeMap<Long, AtomicEvent<?>> allEventsByNumber)
			throws StoryStorageException {

		LinkedHashSet<Long> stepIdOfEvents = new LinkedHashSet<Long>();

		for (WireHashKey wk : map.keySet()) {

			stepIdOfEvents
					.addAll(stepIdEventsOnWire(wk, firstEventId, swapTop));
			stepIdOfEvents.addAll(stepIdEventsOnWire(map.get(wk), firstEventId,
					swapTop));

		}

		for (Long number : stepIdOfEvents) {

			if (number == null) {
				System.out.println("dsfaklsdfdfgdfgadfgasdfghsgh");
			}
			if (allEventsByNumber.get(number) == null) {
				System.out.println(number);
				System.out.println(observableEvent.getStepId());
			}
			if (allEventsByNumber.get(number).getContainer() == null) {
				System.out.println("adsfhkal");
			}
			// rebuild event and numberOfUnresolvedevents on wires
			List<PointRound> changes = allEventsByNumber.get(number)
					.getContainer().exchangeWires(map, informationAboutWires);
			correctChanges(changes);
		}

	}

	protected final Set<Long> stepIdEventsOnWire(WireHashKey wk,
			Long firstEventId, boolean swapTop) {

		if (swapTop) {
			return storageWires.get(wk).subMap(Long.valueOf(-1),
					firstEventId + 1).keySet();
		} else {
			return storageWires.get(wk).subMap(firstEventId,
					observableEvent.getStepId() + 1).keySet();
		}
	}

	// change storageWires,internalStatesByWire
	protected final void correctChanges(List<PointRound> changes)
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

	public final void markAllNull() {
		for (WireHashKey wk : storageWires.keySet()) {
			informationAboutWires.getNumberOfUnresolvedEventOnWire().put(wk, 0);
		}
		for (Event event : events) {
			if (event.getStepId() != -1) {
				event.onlySetMark(null);
			}
		}

	}

	public final double getAverageTime() {
		return averageTime;
	}

	public final int getIteration() {
		return iteration;
	}

	@Override
	public final void correctLinkStates(ExtensionData extensionData,
			long first, boolean top) throws StoryStorageException {
		if (top) {
			ReLinker.reLinkTop(new StateOfLink(extensionData.wk2
					.getAgentId(), extensionData.wk2.getSiteName()),
					new StateOfLink(extensionData.wk4.getAgentId(),
							extensionData.wk4.getSiteName()), first,storageWires.get(extensionData.wk1));
			ReLinker.reLinkTop(new StateOfLink(extensionData.wk4
					.getAgentId(), extensionData.wk4.getSiteName()),
					new StateOfLink(extensionData.wk2.getAgentId(),
							extensionData.wk2.getSiteName()), first,storageWires.get(extensionData.wk3));
			ReLinker.reLinkTop(new StateOfLink(extensionData.wk1
					.getAgentId(), extensionData.wk1.getSiteName()),
					new StateOfLink(extensionData.wk3.getAgentId(),
							extensionData.wk3.getSiteName()), first,storageWires.get(extensionData.wk2));
			ReLinker.reLinkTop( new StateOfLink(extensionData.wk3
					.getAgentId(), extensionData.wk3.getSiteName()),
					new StateOfLink(extensionData.wk1.getAgentId(),
							extensionData.wk1.getSiteName()), first,storageWires.get(extensionData.wk4));
		} else {
			ReLinker.reLinkBottom(new StateOfLink(extensionData.wk2
					.getAgentId(), extensionData.wk2.getSiteName()),
					new StateOfLink(extensionData.wk4.getAgentId(),
							extensionData.wk4.getSiteName()), first,storageWires.get(extensionData.wk1));
			ReLinker.reLinkBottom(new StateOfLink(extensionData.wk4
					.getAgentId(), extensionData.wk4.getSiteName()),
					new StateOfLink(extensionData.wk2.getAgentId(),
							extensionData.wk2.getSiteName()), first,storageWires.get(extensionData.wk3));
			ReLinker.reLinkBottom(new StateOfLink(extensionData.wk1
					.getAgentId(), extensionData.wk1.getSiteName()),
					new StateOfLink(extensionData.wk3.getAgentId(),
							extensionData.wk3.getSiteName()), first,storageWires.get(extensionData.wk2));
			ReLinker.reLinkBottom(new StateOfLink(extensionData.wk3
					.getAgentId(), extensionData.wk3.getSiteName()),
					new StateOfLink(extensionData.wk1.getAgentId(),
							extensionData.wk1.getSiteName()), first,storageWires.get(extensionData.wk4));

		}

	}

	public final StoriesAgentTypesStorage getStoriesAgentTypesStorage() {
		return storiesAgentTypesStorage;
	}


	@Override
	public void updateWires(Set<WireHashKey> sets) throws StoryStorageException {
		informationAboutWires.updateWires(sets, storageWires);

	}

	@Override
	public boolean tryToSwap(long agentId1, WireHashKey wk) {
		if (wk.getTypeOfWire() == TypeOfWire.LINK_STATE) {

			return informationAboutWires.tryToSwapLink(agentId1, storageWires
					.get(wk));
		}
		return true;
	}



	public MasterInformationAboutWires getInformationAboutWires() {
		return informationAboutWires;
	}



}
