package com.plectix.simulator.component.stories.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.component.stories.MarkOfEvent;
import com.plectix.simulator.component.stories.TypeOfWire;
import com.plectix.simulator.component.stories.compressions.CompressionPassport;
import com.plectix.simulator.component.stories.compressions.ExtensionData;
import com.plectix.simulator.component.stories.storage.graphs.StoriesGraphs;

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
				if (storage.getInformationAboutWires().getUnresolvedModifyCount(wk) != 0) {

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
			List<Long> agents2, ArrayList<ExtensionData> extensionLinks,
			Long firstEventId, boolean swapTop) throws StoryStorageException {
		SwapRecord sw = new SwapRecord();
		sw.setExtensionLinks(extensionLinks);
		sw.setAgents1(agents1);
		sw.setAgents2(agents2);
		sw.setFirstEventId(firstEventId);
		sw.setSwapTop(swapTop);
		// System.out.println("swap ++++++++++++++++++++++++++");
		// print(sw);
		doSwap(sw);
		swap = sw;
		
		return sw.getOtherSide();
	}

	public final void undoSwap() throws StoryStorageException {
		// System.out.println("undo ++++++++++++++++++++++++++");
		doSwap(swap);
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
			if(!storage.tryToSwap(agentId1, wk)){
				return false;
			}
		}

		for (WireHashKey wk : wiresByIdAgent.get(agentId2)) {
			ag2.add(wk.getSmallHash());
			if(!storage.tryToSwap(agentId1, wk)){
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
		for (int i = 0; i < size; i++) {
			storage.replaceWireToWire(sw.mapWire.get(i), sw.getFirstEventId(),
					sw.isSwapTop(), allEventsByNumber);

		}
		updateInformationAboutWires(sw, size);

	}

	private void updateInformationAboutWires(SwapRecord sw, int size)
			throws StoryStorageException {

		correctLinkStates(sw, size);
		
		Set<WireHashKey> sets = new LinkedHashSet<WireHashKey>();
		for(int i =0;i<size;i++){
			sets.addAll(sw.mapWire.get(i).values());
			sets.addAll(sw.mapWire.get(i).keySet());
		}
		storage.updateWires(sets);
	}

	private void correctLinkStates(SwapRecord sw, int size)
			throws StoryStorageException {
		for (ExtensionData extensionData : sw.getExtensionLinks()) {
			storage.correctLinkStates(extensionData, sw.getFirstEventId(), sw
					.isSwapTop());
		}
		for (int i = 0; i < size; i++) {
			correctLinkOneSwap(sw.getAgents1().get(i), sw.getAgents2().get(i),
					sw.getFirstEventId(), sw.isSwapTop());
			correctLinkOneSwap(sw.getAgents2().get(i), sw.getAgents1().get(i),
					sw.getFirstEventId(), sw.isSwapTop());
		}
	}

	private void setMapWire(SwapRecord sw) throws StoryStorageException {
		if (sw.mapWire.isEmpty()) {
			for (int i = 0; i < sw.getAgents1().size(); i++) {
				Map<WireHashKey, WireHashKey> newmap = new LinkedHashMap<WireHashKey, WireHashKey>();

				Utils.buildCorrespondence(newmap, wiresByIdAgent.get(sw.getAgents1()
						.get(i)), wiresByIdAgent.get(sw.getAgents2().get(i)));

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

	private final void correctLinkOneSwap(long agentId1, long agentId2,
			Long firstEventId, boolean swapTop) throws StoryStorageException {

		for (WireHashKey wk : wiresByIdAgent.get(agentId1)) {
			if (wk.getTypeOfWire() != TypeOfWire.LINK_STATE)
				continue;

			StateOfLink oldState = new StateOfLink(agentId2, wk.getSiteName());
			StateOfLink newState = new StateOfLink(agentId1, wk.getSiteName());
			if (swapTop) {
				ReLinker.top(firstEventId, oldState, newState,wk,storage.getStorageWires());
			} else {
				ReLinker.bottom(firstEventId, oldState, newState, wk,storage.getStorageWires());
			}
		}
	}




	//=================================================================
	//getters
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
