package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;
import com.plectix.simulator.staticanalysis.stories.MarkOfEvent;
import com.plectix.simulator.staticanalysis.stories.TypeOfWire;
import com.plectix.simulator.staticanalysis.stories.compressions.CompressionPassport;
import com.plectix.simulator.staticanalysis.stories.graphs.StoriesGraphs;

public final class StoragePassport implements CompressionPassport {
	private final WireStorageInterface storage;
	private final Map<String, Set<Long>> iDsByType;
	private final Map<Long, ArrayList<WireHashKey>> wiresByIdAgent;
	private final TreeMap<Long, AtomicEvent<?>> allEventsByNumber;
	private final LinkedHashMap<Long, String> typeById;
	private SwapRecord swap;

	public StoragePassport(WireStorageInterface abstractStorage) {
		storage = abstractStorage;
		iDsByType = new LinkedHashMap<String, Set<Long>>();
		wiresByIdAgent = new LinkedHashMap<Long, ArrayList<WireHashKey>>();
		allEventsByNumber = new TreeMap<Long, AtomicEvent<?>>();

		typeById = new LinkedHashMap<Long, String>();
		swap = null;
		prepareForStrong();
	}

	public final void prepareForStrong() {
		for (WireHashKey wk : storage.getStorageWires().keySet()) {
			long id = wk.getAgentId();
			String type = storage.getStoriesAgentTypesStorage().getType(
					storage.getIteration(), id);

			if (iDsByType.get(type) == null) {
				iDsByType.put(type, new LinkedHashSet<Long>());
			}
			iDsByType.get(type).add(id);

			typeById.put(id, type);

			if (wiresByIdAgent.get(id) == null) {
				ArrayList<WireHashKey> wires = new ArrayList<WireHashKey>();
				wiresByIdAgent.put(id, wires);
			}
			wiresByIdAgent.get(id).add(wk);
			for (Entry<Long, AtomicEvent<?>> entry : storage.getStorageWires()
					.get(wk).entrySet()) {
				if (allEventsByNumber.get(entry.getKey()) == null) {
					allEventsByNumber.put(entry.getKey(), entry.getValue());
				}

			}
		}
		// trouble with -1 initial event TODO

		storage.getStoriesAgentTypesStorage().update(storage.getIteration(),
				typeById);
	}

	public final void removeEventWithMarkDelete() throws StoryStorageException {
		LinkedHashSet<Long> mayBeEmptyAgents = new LinkedHashSet<Long>();

		LinkedList<Long> deletedEvents = new LinkedList<Long>();
		// fill deletedAgents and mayBeEmptyWires
		// correct storageWires
		for (Event event : storage.getEvents()) {
			if (event.getMark() == MarkOfEvent.DELETED) {
				deletedEvents.add(event.getStepId());
				for (WireHashKey wk : event.getAtomicEvents().keySet()) {
					storage.getStorageWires().get(wk).remove(event.getStepId());
					mayBeEmptyAgents.add(wk.getAgentId());

				}
			}

		}
		// TODO ?
		if (deletedEvents.isEmpty()) {
			return;
		}
		for (Long i : deletedEvents) {
			storage.getEvents().remove(allEventsByNumber.get(i).getContainer());
			allEventsByNumber.remove(i);
		}
		clearEmptyWires(mayBeEmptyAgents);

	}

	private void clearEmptyWires(LinkedHashSet<Long> mayBeEmptyAgents)
			throws StoryStorageException {
		boolean tryRemove;
		for (Long id : mayBeEmptyAgents) {
			tryRemove = true;

			for (WireHashKey wk : wiresByIdAgent.get(id)) {
				if (storage.getInformationAboutWires()
						.getUnresolvedModifyCount(wk) != 0) {

					tryRemove = false;
					break;
				}
			}
			if (tryRemove) {
				if (storage.removeWire(wiresByIdAgent.get(id))) {
					wiresByIdAgent.remove(id);
					String type = typeById.remove(id);

					iDsByType.get(type).remove(id);
					if (iDsByType.get(type).isEmpty()) {
						iDsByType.remove(type);
					}
				}
				if (storage.initialEvent().getAtomicEvents().isEmpty()) {
					storage.getEvents().remove(storage.initialEvent());
					allEventsByNumber.remove(Long.valueOf(-1));
					storage.initialEvent().onlySetMark(null);
				}
			}

		}
	}

	public final EventInterface swapAgents(List<Long> agents1,
			List<Long> agents2, Long firstEventId, boolean swapTop)
			throws StoryStorageException {
		SwapRecord sw = new SwapRecord();
		sw.setAgents1(agents1);
		sw.setAgents2(agents2);
		sw.setFirstEventId(firstEventId);
		sw.setSwapTop(swapTop);
		// System.out.println("swap ++++++++++++++++++++++++++");
		// print(sw);
		// StoryCorrectness.testOfStates(this.getStorage());
		doSwap(sw);
		swap = sw;
		// System.out.println("after");
		// StoryCorrectness.testLinks(this.getStorage());

		return sw.getOtherSide();
	}

	private void print(SwapRecord sw) {
		System.out.println(sw.getFirstEventId() + " " + sw.isSwapTop());
		for (int i = 0; i < sw.getAgents1().size(); i++) {

			System.out.println(sw.getAgents1().get(i) + " "
					+ sw.getAgents2().get(i));
		}
	}

	public final void undoSwap() throws StoryStorageException {
		// System.out.println("undo ++++++++++++++++++++++++++");
		// print(swap);
		doSwap(swap);
		// StoryCorrectness.testLinks(this.getStorage());
	}

	public final String getAgentType(long agentId) throws StoryStorageException {
		if (typeById.get(agentId) == null) {
			throw new StoryStorageException("no exist this agent " + agentId);
		}
		return typeById.get(agentId);
	}

	public final boolean isAbleToSwap(long agentId1, long agentId2) {
		LinkedHashSet<Integer> ag1 = new LinkedHashSet<Integer>();
		LinkedHashSet<Integer> ag2 = new LinkedHashSet<Integer>();

		for (WireHashKey wk : wiresByIdAgent.get(agentId1)) {
			ag1.add(wk.getSmallHash());

			if (!storage.tryToSwap(agentId2, wk)) {

				return false;
			}
		}

		for (WireHashKey wk : wiresByIdAgent.get(agentId2)) {
			ag2.add(wk.getSmallHash());
			if (!storage.tryToSwap(agentId1, wk)) {
				return false;
			}

		}

		for (Integer t : ag1) {
			if (!ag2.contains(t)) {
				return false;
			}
		}

		for (Integer t : ag2) {
			if (!ag1.contains(t)) {
				return false;
			}
		}
		return true;

	}

	private final void doSwap(SwapRecord sw) throws StoryStorageException {
		int size = sw.getAgents1().size();

		if (size != sw.getAgents2().size()) {
			throw new StoryStorageException("different size of swapping list");
		}
		setOtherSide(sw);
		setMapWire(sw);
		// StoryCorrectness.testLinks(storage);
		for (int i = 0; i < size; i++) {
			storage.replaceWireToWire(sw.mapWire.get(i), sw.getFirstEventId(),
					sw.isSwapTop(), allEventsByNumber);

		}
		updateInformationAboutWires(sw, size);

	}

	private void updateInformationAboutWires(SwapRecord sw, int size)
			throws StoryStorageException {
		// StoryCorrectness.testOfStates(this.getStorage());
		correctLinkStates(sw, size);
		// StoryCorrectness.testOfStates(this.getStorage());
		Set<WireHashKey> sets = collectWires(sw, size, true);
		// StoryCorrectness.testOfStates(this.getStorage());
		storage.updateWires(sets);
	}

	private Set<WireHashKey> correctLinkStates(SwapRecord sw, int size)
			throws StoryStorageException {

		HashSet<WireHashKey> touchedWires = collectWires(sw, size, false);

		NavigableMap<Long, AtomicEvent<?>> map;
		Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWires = this.storage
				.getStorageWires();
		Map<Long, Long> numbers = new LinkedHashMap<Long, Long>();
		for (int i = 0; i < sw.getAgents1().size(); i++) {
			numbers.put(sw.getAgents2().get(i), sw.getAgents1().get(i));
			numbers.put(sw.getAgents1().get(i), sw.getAgents2().get(i));
		}
		for (WireHashKey wk : touchedWires) {
			if (storageWires.get(wk) == null) {
				throw new StoryStorageException("empty wire");
			}
			if (!sw.isSwapTop()) {
				map = storageWires.get(wk).tailMap(sw.getFirstEventId(), true);
			} else {
				map = storageWires.get(wk).headMap(sw.getFirstEventId(), true);
			}
			if (map != null) {
				correctWire(wk, map, numbers);
			}
			// StoryCorrectness.testOfStates(storage);
		}
		// StoryCorrectness.testLinks(storage);
		return touchedWires;

	}

	private void correctWire(WireHashKey wk,
			NavigableMap<Long, AtomicEvent<?>> map, Map<Long, Long> numbers)
			throws StoryStorageException {

		Entry<Long, AtomicEvent<?>> runner = map.firstEntry();

		while (runner != null) {
			AtomicEvent<?> value = runner.getValue();
			Long key = runner.getKey();

			Object beforeState = value.getState().getBeforeState();
			Object afterState = value.getState().getAfterState();
			StateOfLink sl;
			Long long1;
			if (beforeState != null) {
				sl = (StateOfLink) beforeState;
				long1 = numbers.get(sl.getAgentId());
				if (long1 != null) {
					String siteName = sl.getSiteName();
					((AbstractState<StateOfLink>) value.getState())
							.setBeforeStateOver(new StateOfLink(long1, siteName));

				}
			}
			if (afterState != null) {
				sl = (StateOfLink) afterState;
				long1 = numbers.get(sl.getAgentId());
				if (long1 != null) {
					String siteName = sl.getSiteName();
					((AbstractState<StateOfLink>) value.getState())
							.setAfterState(new StateOfLink(long1, siteName));

				}
			}

			// if (value.getType() == ActionOfAEvent.TEST) {
			// runner = map.lowerEntry(key);
			// continue;
			// }
			// Object afterState = value.getState().getAfterState();
			// if (afterState == null) {
			// throw new StoryStorageException("empty state in mod point");
			// }
			// StateOfLink state = (StateOfLink) afterState;
			// if (state.isFree()) {
			// // for debag
			// checkFree(map, key);
			// } else {
			//
			// if (value.getType() == ActionOfAEvent.MODIFICATION
			// && afterState == null) {
			//
			// } else {
			// boolean b = true;
			// WireHashKey w = createWireByStateOfLink(state);
			// AtomicEvent<?> atomicEvent = this.storage.getStorageWires()
			// .get(w).get(key);
			// if (w.equals(wk)||atomicEvent == null) {
			// b = false;
			// } else {
			// Object afterState2 = atomicEvent.getState()
			// .getAfterState();
			// if (afterState2 == null) {
			// b = false;
			// } else {
			// StateOfLink state2 = (StateOfLink) afterState2;
			// if (state2.isFree()||!createWireByStateOfLink(state2).equals(wk))
			// {
			// b = false;
			// }
			// }
			//
			// }
			// if (!b) {
			// String siteName = state.getSiteName();
			// long agentId = state.getAgentId();
			// int size = sw.getAgents1().size();
			// long newAgentId = -1;
			// for (int i = 0; i < size; i++) {
			// if (sw.getAgents1().get(i).equals(agentId)) {
			// newAgentId = sw.getAgents2().get(i);
			// }
			// if (sw.getAgents2().get(i).equals(agentId)) {
			// newAgentId = sw.getAgents1().get(i);
			// }
			// }
			// if (newAgentId != -1) {
			// // throw new StoryStorageException();
			// StateOfLink newState = new StateOfLink(newAgentId,
			// siteName);
			// changeState(map, key, newState,state);
			// state.setState(newState);
			// }
			// }
			// }
			// }

			runner = map.higherEntry(key);
		}

	}

	private void changeState(NavigableMap<Long, AtomicEvent<?>> map, Long key,
			StateOfLink newState, StateOfLink oldState)
			throws StoryStorageException {
		Entry<Long, AtomicEvent<?>> runner = map.higherEntry(key);

		while (runner != null) {
			AtomicEvent<?> value = runner.getValue();
			Object beforeState = value.getState().getBeforeState();
			StateOfLink link;
			if (value.getType() == ActionOfAEvent.TEST) {
				if (beforeState == null) {
					throw new StoryStorageException(
							"empty before state in TEST event");
				}
				link = (StateOfLink) beforeState;
				if (!link.equals(oldState)) {
					throw new StoryStorageException();
				}
				((AbstractState<StateOfLink>) value.getState())
						.setBeforeStateOver(newState);

			}
			if (value.getType() == ActionOfAEvent.TEST_AND_MODIFICATION) {
				if (beforeState == null) {
					throw new StoryStorageException(
							"empty before state in TEST event");
				}
				link = (StateOfLink) beforeState;
				if (!link.equals(oldState)) {
					throw new StoryStorageException();
				}
				((AbstractState<StateOfLink>) value.getState())
						.setBeforeStateOver(newState);
				return;
			}

			if (value.getType() == ActionOfAEvent.MODIFICATION) {
				if (beforeState != null) {
					link = (StateOfLink) beforeState;
					if (!link.equals(oldState)) {
						throw new StoryStorageException();
					}
					((AbstractState<StateOfLink>) value.getState())
							.setBeforeStateOver(newState);
				}
				return;
			}

			runner = map.lowerEntry(key);
		}

	}

	private void checkFree(NavigableMap<Long, AtomicEvent<?>> map, Long key)
			throws StoryStorageException {
		Entry<Long, AtomicEvent<?>> runner = map.ceilingEntry(key);

		while (runner != null) {
			AtomicEvent<?> value = runner.getValue();
			Object beforeState = value.getState().getBeforeState();
			StateOfLink link;
			if (value.getType() == ActionOfAEvent.TEST) {
				if (beforeState == null) {
					throw new StoryStorageException(
							"empty before state in TEST event");
				}
				link = (StateOfLink) beforeState;
				if (!link.isFree()) {
					throw new StoryStorageException("busy state without free!");
				}

			}
			if (value.getType() == ActionOfAEvent.TEST_AND_MODIFICATION) {
				if (beforeState == null) {
					throw new StoryStorageException(
							"empty before state in TEST event");
				}
				link = (StateOfLink) beforeState;
				if (!link.isFree()) {
					throw new StoryStorageException("busy state without free!");
				}
				return;
			}

			if (value.getType() == ActionOfAEvent.MODIFICATION) {
				if (beforeState != null) {
					link = (StateOfLink) beforeState;
					if (!link.isFree()) {
						throw new StoryStorageException(
								"busy state without free!");
					}
				}
				return;
			}

			runner = map.lowerEntry(key);
		}

	}

	private HashSet<WireHashKey> collectWires(SwapRecord sw, int size, boolean b)
			throws StoryStorageException {
		HashSet<WireHashKey> set = new LinkedHashSet<WireHashKey>();
		HashSet<WireHashKey> set2 = new LinkedHashSet<WireHashKey>();

		for (int i = 0; i < size; i++) {
			if (b) {
				set.addAll(sw.mapWire.get(i).values());
				set.addAll(sw.mapWire.get(i).keySet());
			} else {
				for (WireHashKey wk : sw.mapWire.get(i).values()) {
					if (wk.getTypeOfWire() == TypeOfWire.LINK_STATE) {
						set.add(wk);
					}
				}
				for (WireHashKey wk : sw.mapWire.get(i).keySet()) {
					if (wk.getTypeOfWire() == TypeOfWire.LINK_STATE) {
						set.add(wk);
					}
				}
			}
		}

		Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWires = this.storage
				.getStorageWires();
		HashSet<AtomicEvent<?>> bigset = new LinkedHashSet<AtomicEvent<?>>();
		if (sw.isSwapTop()) {
			for (WireHashKey wk : set) {
				if (!b && wk.getTypeOfWire() != TypeOfWire.LINK_STATE)
					continue;
				Map<Long, AtomicEvent<?>> map = storageWires.get(wk).headMap(
						sw.getFirstEventId());
				bigset.addAll(map.values());
			}
		} else {
			for (WireHashKey wk : set) {
				if (!b && wk.getTypeOfWire() != TypeOfWire.LINK_STATE)
					continue;
				Map<Long, AtomicEvent<?>> map = storageWires.get(wk).tailMap(
						sw.getFirstEventId(), true);
				bigset.addAll(map.values());
			}
		}

		for (AtomicEvent<?> ae : bigset) {
			Object beforeState = ae.getState().getBeforeState();
			Object afterState = ae.getState().getAfterState();
			if (beforeState != null) {
				if (!b && !(beforeState instanceof StateOfLink)) {
					throw new StoryStorageException();
				}
				if (beforeState instanceof StateOfLink) {
					StateOfLink sl = (StateOfLink) beforeState;
					if (!sl.isFree()) {
						WireHashKey createWireByStateOfLink = createWireByStateOfLink(sl);
						set2.add(createWireByStateOfLink);
						if (storageWires.get(createWireByStateOfLink) == null) {
							throw new StoryStorageException("miss wire!");
						}
					}
				}
			}
			if (afterState != null) {
				if (!b && !(afterState instanceof StateOfLink)) {
					throw new StoryStorageException();
				}
				if (afterState instanceof StateOfLink) {
					StateOfLink sl = (StateOfLink) afterState;
					if (!sl.isFree()) {
						WireHashKey createWireByStateOfLink = createWireByStateOfLink(sl);
						set2.add(createWireByStateOfLink);
						if (storageWires.get(createWireByStateOfLink) == null) {
							throw new StoryStorageException("miss wire!");
						}
					}
				}
			}
		}
		set2.addAll(set);
		// TODO test
		for (WireHashKey wk : set2) {
			if (storageWires.get(wk) == null) {
				throw new StoryStorageException("miss wire!");
			}
		}
		return set2;

	}

	private static WireHashKey createWireByStateOfLink(StateOfLink sl) {
		if (sl.getAgentId() != -1) {
			return new WireHashKey(sl.getAgentId(), sl.getSiteName(),
					TypeOfWire.LINK_STATE);
		}
		return null;
	}

	private void setMapWire(SwapRecord sw) throws StoryStorageException {
		if (sw.mapWire.isEmpty()) {
			for (int i = 0; i < sw.getAgents1().size(); i++) {
				Map<WireHashKey, WireHashKey> newmap = new LinkedHashMap<WireHashKey, WireHashKey>();

				Utils.buildCorrespondence(newmap, wiresByIdAgent.get(sw
						.getAgents1().get(i)), wiresByIdAgent.get(sw
						.getAgents2().get(i)));

				sw.mapWire.add(newmap);

			}

		}
	}

	private void setOtherSide(SwapRecord sw) {
		if (sw.getOtherSide() == null) {
			if (sw.isSwapTop()) {
				sw.setOtherSide(allEventsByNumber.get(
						allEventsByNumber.higherKey(sw.getFirstEventId()))
						.getContainer());
			} else {
				if (allEventsByNumber.lowerKey(sw.getFirstEventId()) != null) {
					sw.setOtherSide(allEventsByNumber.get(
							allEventsByNumber.lowerKey(sw.getFirstEventId()))
							.getContainer());
				} else {
					sw.setOtherSide(null);
				}
			}

		}
	}

	// private final void correctLinkOneSwap(long agentId1, long agentId2,
	// Long firstEventId, boolean swapTop) throws StoryStorageException {
	//
	// for (WireHashKey wk : wiresByIdAgent.get(agentId1)) {
	// if (wk.getTypeOfWire() != TypeOfWire.LINK_STATE)
	// continue;
	//
	// StateOfLink oldState = new StateOfLink(agentId2, wk.getSiteName());
	// StateOfLink newState = new StateOfLink(agentId1, wk.getSiteName());
	// if (swapTop) {
	// ReLinker.top(firstEventId, oldState,
	// newState,wk,storage.getStorageWires());
	// } else {
	// ReLinker.bottom(firstEventId, oldState, newState,
	// wk,storage.getStorageWires());
	// }
	// }
	// }

	// =================================================================
	// getters
	public final int eventCount() {
		return allEventsByNumber.size();
	}

	public final Iterator<Long> agentIterator(String type) {
		return iDsByType.get(type).iterator();
	}

	public final Iterator<String> agentTypeIterator() {
		return iDsByType.keySet().iterator();
	}

	public final EventIteratorInterface eventIterator(boolean reverse)
			throws StoryStorageException {
		if (reverse) {
			return new EventIteratorOnWire(allEventsByNumber, allEventsByNumber
					.lastKey(), reverse);
		} else {
			return new EventIteratorOnWire(allEventsByNumber, allEventsByNumber
					.firstKey(), reverse);

		}
	}

	public final ArrayList<WireHashKey> getAgentWires(long agentId) {
		return wiresByIdAgent.get(agentId);
	}

	public final WireStorageInterface getStorage() {
		return storage;
	}

	public final TreeMap<Long, AtomicEvent<?>> getAllEventsByNumber() {
		return allEventsByNumber;
	}

	public final Map<Long, ArrayList<WireHashKey>> getWiresByIdAgent() {
		return wiresByIdAgent;
	}

	public final StoriesGraphs extractGraph() {
		return new StoriesGraphs(this);
	}
}
