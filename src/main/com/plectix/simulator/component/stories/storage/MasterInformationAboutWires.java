package com.plectix.simulator.component.stories.storage;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.plectix.simulator.component.stories.ActionOfAEvent;
import com.plectix.simulator.component.stories.TypeOfWire;

public class MasterInformationAboutWires {
	private LinkedHashMap<WireHashKey, LinkedHashSet<String>> internalStatesByWire;
	private LinkedHashMap<WireHashKey, LinkedHashSet<StateOfLink>> linkStatesByWire;
	private LinkedHashMap<WireHashKey, Integer> numberOfUnresolvedEventOnWire;

	public MasterInformationAboutWires(
			LinkedHashMap<WireHashKey, LinkedHashSet<String>> internalStatesByWire,
			LinkedHashMap<WireHashKey, LinkedHashSet<StateOfLink>> linkStatesByWire,
			LinkedHashMap<WireHashKey, Integer> numberOfUnresolvedEventOnWire) {
		this.internalStatesByWire = internalStatesByWire;
		this.linkStatesByWire = linkStatesByWire;
		this.numberOfUnresolvedEventOnWire = numberOfUnresolvedEventOnWire;
	}

	public LinkedHashMap<WireHashKey, LinkedHashSet<String>> getInternalStatesByWire() {
		return internalStatesByWire;
	}

	public void setInternalStatesByWire(
			LinkedHashMap<WireHashKey, LinkedHashSet<String>> internalStatesByWire) {
		this.internalStatesByWire = internalStatesByWire;
	}

	public LinkedHashMap<WireHashKey, LinkedHashSet<StateOfLink>> getLinkStatesByWire() {
		return linkStatesByWire;
	}

	public void setLinkStatesByWire(
			LinkedHashMap<WireHashKey, LinkedHashSet<StateOfLink>> linkStatesByWire) {
		this.linkStatesByWire = linkStatesByWire;
	}

	public LinkedHashMap<WireHashKey, Integer> getNumberOfUnresolvedEventOnWire() {
		return numberOfUnresolvedEventOnWire;
	}

	public void setNumberOfUnresolvedEventOnWire(
			LinkedHashMap<WireHashKey, Integer> numberOfUnresolvedEventOnWire) {
		this.numberOfUnresolvedEventOnWire = numberOfUnresolvedEventOnWire;
	}

	public void fullInformationAboutWires(AtomicEvent<?> atomicEvent,
			WireHashKey key) throws StoryStorageException {
		ActionOfAEvent type = atomicEvent.getType();
		AbstractState<?> state = atomicEvent.getState();
		if (key.getTypeOfWire() == TypeOfWire.INTERNAL_STATE) {
			if (internalStatesByWire.get(key) == null) {
				LinkedHashSet<String> internalStates = new LinkedHashSet<String>();
				internalStatesByWire.put(key, internalStates);
			}
			if (type == ActionOfAEvent.TEST_AND_MODIFICATION) {
				internalStatesByWire.get(key).add(
						(String) (state.getAfterState()));
				internalStatesByWire.get(key).add(
						(String) (state.getBeforeState()));
			}
			if (atomicEvent.getType() == ActionOfAEvent.MODIFICATION) {
				internalStatesByWire.get(key).add(
						(String) (state.getAfterState()));
			}
		} else if (key.getTypeOfWire() == TypeOfWire.LINK_STATE) {
			if (linkStatesByWire.get(key) == null) {
				LinkedHashSet<StateOfLink> linkStates = new LinkedHashSet<StateOfLink>();
				linkStatesByWire.put(key, linkStates);
			}
			switch (type) {
			case TEST_AND_MODIFICATION:
				StateOfLink before = (StateOfLink) (state.getBeforeState());

				if (before != null && !before.isFree())
					linkStatesByWire.get(key).add(before);
			case MODIFICATION:
				StateOfLink after = (StateOfLink) (state.getAfterState());

				if (after != null && !after.isFree())
					linkStatesByWire.get(key).add(after);
				break;
			}
		}
	}

	public void updateWires(Set<WireHashKey> wires,
			Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> map)
			throws StoryStorageException {
		for (WireHashKey wk : wires) {

			LinkedHashSet<String> linkedHashSet = internalStatesByWire.get(wk);
			if (linkedHashSet != null) {
				linkedHashSet.clear();
			}
			LinkedHashSet<StateOfLink> linkedHashSet2 = linkStatesByWire
					.get(wk);
			if (linkedHashSet2 != null) {
				linkedHashSet2.clear();
			}
			for (AtomicEvent<?> atomicEvent : map.get(wk).values()) {
				fullInformationAboutWires(atomicEvent, wk);
			}
		}
	}

	public boolean tryToSwapLink(long agentId1, Map<Long, AtomicEvent<?>> wire) {

		for (AtomicEvent<?> ae : wire.values()) {
			if (ae.getState().getAfterState() != null) {
				if (((StateOfLink) (ae.getState().getAfterState()))
						.getAgentId() == agentId1)
					return false;
			}
			if (ae.getState().getBeforeState() != null) {
				if (((StateOfLink) (ae.getState().getBeforeState()))
						.getAgentId() == agentId1)
					return false;
			}
		}
		return true;
	}

	public final int getUnresolvedModifyCount(WireHashKey wkey)
			throws StoryStorageException {
		if (wkey == null || getNumberOfUnresolvedEventOnWire() == null
				|| getNumberOfUnresolvedEventOnWire().get(wkey) == null) {
			throw new StoryStorageException("wire = null");
		}
		return getNumberOfUnresolvedEventOnWire().get(wkey);

	}

	public final void upNumberOfUnresolvedModifyEvent(WireHashKey wireKey,
			boolean up) throws StoryStorageException {
		int x = getUnresolvedModifyCount(wireKey);

		if (up) {
			x++;
		} else {
			x--;
		}
		if (x < 0)
			throw new StoryStorageException(
					"negative number of unresolved events on wire");

		putUnresolvedModifyEvent(wireKey, Integer.valueOf(x));

	}

	public final void putUnresolvedModifyEvent(WireHashKey wireHashKey,
			int valueOf) {
		getNumberOfUnresolvedEventOnWire().put(wireHashKey, valueOf);

	}

	public final Iterator<String> wireInternalStateIterator(WireHashKey wkey)
			throws StoryStorageException {

		if (wkey.getTypeOfWire() == TypeOfWire.INTERNAL_STATE) {
			if (getInternalStatesByWire().get(wkey) == null) {
				throw new StoryStorageException("empty internal states ^(");
			}
			return getInternalStatesByWire().get(wkey).iterator();
		} else {
			throw new StoryStorageException(
					"wireInternalStateIterator : prohibit type of wire");
		}
	}

	public final Iterator<StateOfLink> wireLinkStateIterator(WireHashKey wkey)
			throws StoryStorageException {
		if (wkey.getTypeOfWire() == TypeOfWire.LINK_STATE) {
			if (getLinkStatesByWire().get(wkey) == null) {
				throw new StoryStorageException("empty link states ^(");
			}
			return getLinkStatesByWire().get(wkey)
					.iterator();
		} else {
			throw new StoryStorageException(
					"wireLinkStateIterator : prohibit type of wire");
		}
	}

	public void removeWire(WireHashKey wk) {
		internalStatesByWire.remove(wk);
		linkStatesByWire.remove(wk);
		numberOfUnresolvedEventOnWire.remove(wk);
	}
}