package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;

public class StoryBuilder {
	private final LinkedHashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWires = new LinkedHashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>>();
	private boolean endFlag;

	public LinkedHashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>> getStorageWires() {
		return storageWires;
	}

	public final void addEventContainer(Event eventContainer)
			throws StoryStorageException {

		if (!tryToRemoveOppositeBlock(eventContainer)) {
			for (WireHashKey key : eventContainer.getAtomicEvents().keySet()) {
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

	public void setFlagTrue() {
		endFlag = true;
	}

	protected final boolean tryToRemoveOppositeBlock(Event eventIn) {
		// may be need for all non-observable events
		if (endFlag) {
			return false;
		}
		LinkedHashMap<WireHashKey, AtomicEvent<?>> mapIn = getModificationAction(eventIn);
		Long stepId = eventIn.getStepId();
		LinkedHashSet<WireHashKey> set = new LinkedHashSet<WireHashKey>();
		LinkedHashSet<Event> listForDel = new LinkedHashSet<Event>();
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

		for (Event e : listForDel) {
			for (WireHashKey wk : e.getAtomicEvents().keySet()) {
				storageWires.get(wk).remove(e.getStepId());
				if (storageWires.get(wk).size() == 0) {
					storageWires.remove(wk);
				}
			}
		}
		return true;
	}

	protected final LinkedHashMap<WireHashKey, AtomicEvent<?>> getModificationAction(
			Event eventIn) {
		LinkedHashMap<WireHashKey, AtomicEvent<?>> mapIn = new LinkedHashMap<WireHashKey, AtomicEvent<?>>();
		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : eventIn
				.getAtomicEvents().entrySet()) {
			AtomicEvent<?> aEvent = entry.getValue();
			if (aEvent.getType() != ActionOfAEvent.TEST)
				mapIn.put(entry.getKey(), aEvent);

		}
		return mapIn;
	}

	protected final AtomicEvent<?> getAtomicLastModificationAtomicEvent(
			WireHashKey key, AtomicEvent<?> aEventIn, Long stepId) {
		TreeMap<Long, AtomicEvent<?>> wire = storageWires.get(key);

		if (wire == null)
			return null;

		stepId = wire.lowerKey(stepId);

		if (stepId != null) {
			AtomicEvent<?> nextAevent = wire.get(stepId);

			if (nextAevent == null
					|| nextAevent.getType() != ActionOfAEvent.TEST_AND_MODIFICATION) {
				return null;
			} else {
				return nextAevent;
			}
		}
		return null;
	}

	// It needs change LinkedHashSet<Event> -> LinkedHashSet<Long>
	protected final void handling(Event event, LinkedHashSet<Event> needEvents)
			throws StoryStorageException {
		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : event
				.getAtomicEvents().entrySet()) {
			WireHashKey key = entry.getKey();
			AtomicEvent<?> aEvent = entry.getValue();

			if (!storageWires.get(key).containsValue(aEvent)) {
				throw new StoryStorageException("");
			}

			Event foundEvent = findCausing(key, aEvent, event.getStepId());
			if (foundEvent != null && !needEvents.contains(foundEvent)) {
				needEvents.add(foundEvent);
				handling(foundEvent, needEvents);
			}
		}
	}

	protected final Event findCausing(WireHashKey key, AtomicEvent<?> event,
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
					&& nextAevent.getType() == ActionOfAEvent.TEST) {
				stepId = wire.lowerKey(stepId);
				if (stepId == null)
					return event.getContainer();
				nextAevent = wire.get(stepId);
			}
		}
		return nextAevent.getContainer();

	}

}
