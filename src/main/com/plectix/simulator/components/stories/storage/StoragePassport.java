package com.plectix.simulator.components.stories.storage;

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

import com.plectix.simulator.components.stories.compressions.CompressionPassport;
import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.storage.graphs.StoriesGraphs;
import com.plectix.simulator.simulator.ThreadLocalData;

public class StoragePassport implements CompressionPassport {

	private IWireStorage storage;
	private Map<Integer, Set<Long>> iDsByType;
	private Map<Long, ArrayList<WireHashKey>> wiresByIdAgent;
	private TreeMap<Long, AtomicEvent<?>> allEventsByNumber;
	private LinkedHashMap<Long, Integer> typeById;
	private SwapRecord swap;

	public StoragePassport(AbstractStorage abstractStorage) {
		storage = abstractStorage;
		iDsByType = new LinkedHashMap<Integer, Set<Long>>();
		wiresByIdAgent = new LinkedHashMap<Long, ArrayList<WireHashKey>>();
		allEventsByNumber = new TreeMap<Long, AtomicEvent<?>>();

		typeById = new LinkedHashMap<Long, Integer>();
		swap = null;

		prepareForStrong();

	}

	public int eventCount () {
		return allEventsByNumber.size();
	}
	
	public Iterator<Long> agentIterator(int typeId) {
		return iDsByType.get(typeId).iterator();
	}

	public Iterator<Integer> agentTypeIterator() {
		return iDsByType.keySet().iterator();
	}

	public IEventIterator eventIterator(boolean reverse)
			throws StoryStorageException {
		if (reverse) {
			return new CEventIteratorOnWire(allEventsByNumber,
					allEventsByNumber.lastKey(), reverse);
		} else {
			return new CEventIteratorOnWire(allEventsByNumber,
					allEventsByNumber.firstKey(), reverse);

		}
	}

	public ArrayList<WireHashKey> getAgentWires(long agentId) {
		return wiresByIdAgent.get(agentId);
	}

	public IWireStorage getStorage() {
		return storage;
	}

	public void prepareForStrong() {
		for (WireHashKey wk : storage.getStorageWires().keySet()) {
			Long id = new Long(0);
			Integer type = new Integer(0);

			id = wk.getAgentId();
			type = ThreadLocalData.getTypeById().getType(
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

		ThreadLocalData.getTypeById().update(storage.getIteration(), typeById);
	}

	public void removeEventWithMarkDelete() throws StoryStorageException {
		LinkedHashSet<Long> mayBeEmptyAgents = new LinkedHashSet<Long>();

		LinkedList<Long> deletedEvents = new LinkedList<Long>();

		// fill deletedAgents and mayBeEmptyWires
		// correct sorageWires
		// for (Entry<Long, AtomicEvent<?>> entry :
		// allEventsByNumber.entrySet()) {
		for (CEvent event : storage.getEvents()) {
			if (event.getMark() == EMarkOfEvent.DELETED) {
				deletedEvents.add(event.getStepId());
				for (WireHashKey wk : event.getAtomicEvents().keySet()) {
					storage.getStorageWires().get(wk).remove(event.getStepId());
					mayBeEmptyAgents.add(wk.getAgentId());

				}
			}

		}

		for (Long i : deletedEvents) {
			storage.getEvents().remove(allEventsByNumber.get(i).getContainer());
			allEventsByNumber.remove(i);
		}

		boolean tryRemove;
		for (Long id : mayBeEmptyAgents) {
			tryRemove = true;

			for (WireHashKey wk : wiresByIdAgent.get(id)) {
				if (storage.getUnresolvedModifyCount(wk) != 0) {

					tryRemove = false;
					break;
				}
			}

			if (tryRemove) {
				if (storage.removeWire(wiresByIdAgent.get(id))) {
					wiresByIdAgent.remove(id);
					Integer type = typeById.remove(id);
					iDsByType.get(type).remove(id);
				}
				if (storage.initialEvent().getAtomicEvents().isEmpty()) {
					storage.getEvents().remove(storage.initialEvent());
					allEventsByNumber.remove(Long.valueOf(-1));
					// storage.nullInitial();
					storage.initialEvent().onlySetMark(null);
				}
			}

		}

	}

	public ICEvent swapAgents(List<Long> agents1, List<Long> agents2,
			Long firstEventId, boolean swapTop) throws StoryStorageException {
		SwapRecord sw = new SwapRecord();
		sw.setAgents1(agents1);
		sw.setAgents2(agents2);
		sw.setFirstEventId(firstEventId);
		sw.setSwapTop(swapTop);
		doSwap(sw);
		swap = sw;
		return sw.getOtherSide();
	}

	public void undoSwap() throws StoryStorageException {
		doSwap(swap);
	}

	public int getAgentType(long agentId) {
		return typeById.get(agentId);
	}

	public boolean isAbleToSwap(long agentId1, long agentId2) {
		LinkedHashSet<Integer> ag1 = new LinkedHashSet<Integer>();
		LinkedHashSet<Integer> ag2 = new LinkedHashSet<Integer>();

		for (WireHashKey wk : wiresByIdAgent.get(agentId1)) {
			ag1.add(wk.getSmallHash());
		}

		for (WireHashKey wk : wiresByIdAgent.get(agentId2)) {
			ag2.add(wk.getSmallHash());
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

	private void doSwap(SwapRecord sw) throws StoryStorageException {
		int size = sw.getAgents1().size();

		if (size != sw.getAgents2().size()) {
			throw new StoryStorageException("different size of swapping list");
		}
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

		if (sw.mapWire.isEmpty()) {
			for (int i = 0; i < sw.getAgents1().size(); i++) {
				Map<WireHashKey, WireHashKey> newmap = new LinkedHashMap<WireHashKey, WireHashKey>();

				buildCorrespondence(newmap, wiresByIdAgent.get(sw.getAgents1()
						.get(i)), wiresByIdAgent.get(sw.getAgents2().get(i)));

				sw.mapWire.add(newmap);

			}

		}

		for (int i = 0; i < size; i++) {
			storage.replaceWireToWire(sw.mapWire.get(i), sw.getFirstEventId(),
					sw.isSwapTop(), allEventsByNumber);

		}
	}

	private void buildCorrespondence(Map<WireHashKey, WireHashKey> mapWire,
			ArrayList<WireHashKey> arrayList1, ArrayList<WireHashKey> arrayList2)
			throws StoryStorageException {

		ArrayList<Integer> hashs1 = new ArrayList<Integer>();
		ArrayList<Integer> hashs2 = new ArrayList<Integer>();
		int k = arrayList1.size();
		for (int i = 0; i < k; i++) {
			hashs1.add(arrayList1.get(i).getSmallHash());
		}
		if (arrayList2.size() != k) {
			throw new StoryStorageException();
		}
		for (int i = 0; i < k; i++) {
			hashs2.add(arrayList2.get(i).getSmallHash());
		}

		for (int i = 0; i < k; i++) {
			for (int j = 0; j < k; j++) {
				if (hashs1.get(i).equals(hashs2.get(j))) {
					mapWire.put(arrayList1.get(i), arrayList2.get(j));
				}
			}
		}

	}

	public TreeMap<Long, AtomicEvent<?>> getAllEventsByNumber() {
		return allEventsByNumber;
	}

	public Map<Long, ArrayList<WireHashKey>> getWiresByIdAgent() {
		return wiresByIdAgent;
	}

	public StoriesGraphs extractGraph() {
		return new StoriesGraphs(this);
	}

}
