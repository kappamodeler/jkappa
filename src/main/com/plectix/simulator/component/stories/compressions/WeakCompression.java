package com.plectix.simulator.component.stories.compressions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import com.plectix.simulator.component.stories.ActionOfAEvent;
import com.plectix.simulator.component.stories.MarkOfEvent;
import com.plectix.simulator.component.stories.State;
import com.plectix.simulator.component.stories.storage.AtomicEvent;
import com.plectix.simulator.component.stories.storage.Event;
import com.plectix.simulator.component.stories.storage.EventInterface;
import com.plectix.simulator.component.stories.storage.EventIteratorInterface;
import com.plectix.simulator.component.stories.storage.MasterInformationAboutWires;
import com.plectix.simulator.component.stories.storage.StateOfLink;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.component.stories.storage.WireHashKey;
import com.plectix.simulator.component.stories.storage.WireStorageInterface;

enum WalkResult {
	DESIRED, FREE, NULL, FAILED
}

/*package*/ final class WeakCompression {
	private final WireStorageInterface storage;
	private final MasterInformationAboutWires information;

	// For strong compression
	public final static long ghostEventId = -100;
	private int maxQueueSize;
	private boolean maxQueueSizeReached;
	private Long upperGhostId = null;
	private Long lowerGhostId = null;
	private ArrayList<QueueEntry> uninvestigatedQueue = new ArrayList<QueueEntry>();
	private Stack<StackEntry> wireStack = new Stack<StackEntry>();
	private ArrayList<EventInterface> candidatesToDelete = new ArrayList<EventInterface>();
	private int currentNodeIdx;
	private QueueEntry currentNode = null;
	private StackEntry topEntry = null;

	
	public WeakCompression(WireStorageInterface storage) {
		this.storage = storage;
		information = storage.getInformationAboutWires();
	}

	public final boolean process() throws StoryStorageException {
		return doProcess(null);
	}

	public final boolean processInconsistent(EventInterface boundaryEvent,
			EventInterface neiEvent) throws StoryStorageException {
		return doProcess(createGhostEvent(boundaryEvent, neiEvent));
	}

	private final boolean doProcess(EventInterface ghostEvent)
			throws StoryStorageException {
		Stack<EventInterface> searchStack = new Stack<EventInterface>();
		EventInterface initialEvent = storage.initialEvent();
		EventInterface observableEvent = storage.observableEvent();
		boolean compressed = false;
		if (storage.extractPassport().eventCount() < 256)
			maxQueueSize = 256;
		else
			maxQueueSize = 16;

		do {
			maxQueueSize *= 2;
			maxQueueSizeReached = false;

			storage.markAllNull();
			storage.markAllUnresolved();

			searchStack.clear();

			// Add initial event to stack
			if (initialEvent.getMark() != null)
				searchStack.add(initialEvent);

			// Add observable event to stack
			observableEvent.setMark(MarkOfEvent.KEPT, information);
			searchStack.add(observableEvent);

			if (ghostEvent != null) {
				if (!propagate(ghostEvent)) {
					if (maxQueueSizeReached)
						continue;
					return false;
				}
				ghostEvent = null;
				if (uninvestigatedQueue.size() > 1)
					compressed = true;
			}

			while (!searchStack.empty()) {
				EventInterface top = searchStack.peek();

				EventInterface nextEvent = selectEventToBranch(top);

				if (nextEvent == null) {
					searchStack.pop();
					continue;
				}

				nextEvent.setMark(MarkOfEvent.DELETED, information);

				if (!propagate(nextEvent)) {
					nextEvent.setMark(MarkOfEvent.KEPT, information);
					searchStack.push(nextEvent);
				} else if (!compressed)
					compressed = true;
			}

			if (storage.markAllUnresolvedAsDeleted() && !compressed)
				compressed = true;
			storage.extractPassport().removeEventWithMarkDelete();
		} while (maxQueueSize < storage.extractPassport().eventCount());

		return compressed;
	}

	@SuppressWarnings("unchecked")
	private final EventInterface createGhostEvent(EventInterface boundaryEvent,
			EventInterface neiEvent) throws StoryStorageException {
		Event event = new Event(ghostEventId, 0, MarkOfEvent.DELETED);

		Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wiresMap = storage
				.getStorageWires();

		if (boundaryEvent.getStepId() > neiEvent.getStepId()) {
			upperGhostId = neiEvent.getStepId();
			lowerGhostId = boundaryEvent.getStepId();
		} else {
			upperGhostId = boundaryEvent.getStepId();
			lowerGhostId = neiEvent.getStepId();
		}

		Map.Entry<Long, AtomicEvent<?>> upperEvent, lowerEvent;

		// TODO: iterate only on swapped agents
		for (Map.Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : wiresMap
				.entrySet()) {
			upperEvent = wire.getValue().floorEntry(upperGhostId);
			lowerEvent = wire.getValue().ceilingEntry(lowerGhostId);

			if (lowerEvent == null || upperEvent == null)
				continue;

			Object upperValue = null, lowerValue = null;

			switch (upperEvent.getValue().getType()) {
			case MODIFICATION:
			case TEST_AND_MODIFICATION:
				upperValue = upperEvent.getValue().getState().getAfterState();
				break;
			case TEST:
				upperValue = upperEvent.getValue().getState().getBeforeState();
				break;
			}

			lowerValue = lowerEvent.getValue().getState().getBeforeState();

			if ((lowerValue == null && upperValue == null)
					|| (upperValue != null && upperValue.equals(lowerValue)))
				continue;

			AtomicEvent<?> newAEvent = null;

			switch (wire.getKey().getTypeOfWire()) {
			case AGENT:
				newAEvent = new AtomicEvent<State>(
						event,
						upperValue != null ? ActionOfAEvent.TEST_AND_MODIFICATION
								: ActionOfAEvent.MODIFICATION);
				if (upperEvent.getValue().getType() != ActionOfAEvent.TEST
						&& lowerEvent.getValue().getType() != ActionOfAEvent.TEST
						&& upperValue == null
						&& lowerEvent.getValue().getState().getAfterState() != null) {
					((AtomicEvent<State>) newAEvent).getState().setBeforeState(
							State.CHECK_AGENT);
					((AtomicEvent<State>) newAEvent).getState().setAfterState(
							State.CHECK_AGENT);
				} else {
					((AtomicEvent<State>) newAEvent).getState().setBeforeState(
							(State) upperValue);
					((AtomicEvent<State>) newAEvent).getState().setAfterState(
							(State) lowerValue);
				}
				break;
			case BOUND_FREE:
				newAEvent = new AtomicEvent<State>(
						event,
						upperValue != null ? ActionOfAEvent.TEST_AND_MODIFICATION
								: ActionOfAEvent.MODIFICATION);
				((AtomicEvent<State>) newAEvent).getState().setBeforeState(
						(State) upperValue);
				((AtomicEvent<State>) newAEvent).getState().setAfterState(
						(State) lowerValue);
				break;

			case INTERNAL_STATE:
				newAEvent = new AtomicEvent<String>(
						event,
						upperValue != null ? ActionOfAEvent.TEST_AND_MODIFICATION
								: ActionOfAEvent.MODIFICATION);
				((AtomicEvent<String>) newAEvent).getState().setBeforeState(
						(String) upperValue);
				((AtomicEvent<String>) newAEvent).getState().setAfterState(
						(String) lowerValue);
				break;
			case LINK_STATE:
				newAEvent = new AtomicEvent<StateOfLink>(
						event,
						upperValue != null ? ActionOfAEvent.TEST_AND_MODIFICATION
								: ActionOfAEvent.MODIFICATION);
				((AtomicEvent<StateOfLink>) newAEvent).getState()
						.setBeforeState((StateOfLink) upperValue);
				((AtomicEvent<StateOfLink>) newAEvent).getState()
						.setAfterState((StateOfLink) lowerValue);
				break;

			}

			event.getAtomicEvents().put(wire.getKey(), newAEvent);
		}

		if (event.getAtomicEventCount() == 0)
			return null;

		return event;
	}

	private final EventInterface selectEventToBranch(EventInterface keptEvent)
			throws StoryStorageException {
		WireHashKey selectedWire = keptEvent
				.getWireWithMinimumUresolvedEvent(information);

		if (selectedWire == null)
			return null;

		EventIteratorInterface eventIterator = storage.eventIterator(
				selectedWire, true);

		while (eventIterator.hasNext()) {
			eventIterator.next();

			EventInterface nextEvent = eventIterator.value();

			if (nextEvent.getMark() == MarkOfEvent.UNRESOLVED)
				return nextEvent;
		}

		throw new StoryStorageException(
				"selectEventToBranch(): no UNRESOLVED event");
	}

	private final boolean propagate(EventInterface event)
			throws StoryStorageException {
		uninvestigatedQueue.clear();
		wireStack.clear();

		uninvestigatedQueue.add(new QueueEntry(event, 0, 0, 0));

		currentNodeIdx = 0;
		currentNode = uninvestigatedQueue.get(currentNodeIdx);

		pushEntry();

		while (currentNodeIdx != uninvestigatedQueue.size()) {
			currentNode = uninvestigatedQueue.get(currentNodeIdx);

			// Shift to next event in queue
			if (currentNode.currentWireIdx == currentNode.getWireCount()) {
				currentNodeIdx++;

				// currentWireIdx may be not zero if we backtracked
				if (currentNodeIdx < uninvestigatedQueue.size())
					uninvestigatedQueue.get(currentNodeIdx).currentWireIdx = 0;

				continue;
			}

			topEntry = wireStack.peek();

			// Push next wire from event to stack
			if (currentNode.currentWireIdx != topEntry.getWireIdx()) {
				pushEntry();
				continue;
			}

			// Add new events to queue
			if (topEntry.hasNextState()) {
				// Find block of events on current wire to delete
				if (addDeletedEvents())
					currentNode.currentWireIdx++;

				// Go to next wire state
				continue;
			}

			// Restore stack and queue
			// Backtrack to previous wire
			if (currentNode.currentWireIdx > 0) {
				currentNode.currentWireIdx--;
				decreaseStack(wireStack.size() - 1);

				QueueEntry last = uninvestigatedQueue.get(uninvestigatedQueue
						.size() - 1);
				int previousPos = last.getQueuePosIdx();

				// If queue has events added from previous wire
				if (previousPos == currentNodeIdx
						&& uninvestigatedQueue.get(previousPos).getStackSize() == wireStack
								.size())
					decreaseQueue(last.getQueueSize());

				continue;
			}

			// Backtrack to source event and wire
			decreaseQueue(currentNode.getQueueSize());
			decreaseStack(currentNode.getStackSize());

			currentNodeIdx = currentNode.getQueuePosIdx();

			if (uninvestigatedQueue.size() > 0)
				uninvestigatedQueue.get(currentNodeIdx).currentWireIdx = wireStack
						.peek().getWireIdx();
		}

		if (currentNodeIdx > 0)
			return true;
		return false;
	}

	private final void pushEntry() throws StoryStorageException {
		if (getEvent().getAtomicEventCount() == 0)
			throw new StoryStorageException(
					"pushEntry(): event has no non-BOUND/FREE atomic events");

		topEntry = new StackEntry(getWireIdx());
		topEntry.checkFrozenState(this);
		wireStack.push(topEntry);
	}

	public final WireStorageInterface getStorage() {
		return storage;
	}

	public final EventInterface getEvent() {
		return currentNode.getEvent();
	}

	public final int getWireIdx() {
		return currentNode.currentWireIdx;
	}

	private final void decreaseQueue(int size) throws StoryStorageException {
		while (uninvestigatedQueue.size() != size) {
			QueueEntry last = uninvestigatedQueue.get(uninvestigatedQueue
					.size() - 1);
			if (last.getEvent().getStepId() != ghostEventId)
				last.getEvent().setMark(MarkOfEvent.UNRESOLVED, information);
			uninvestigatedQueue.remove(uninvestigatedQueue.size() - 1);
		}
	}

	private final void decreaseStack(int size) {
		while (wireStack.size() != size)
			wireStack.pop();
	}

	public final long getFirstEventId(boolean upwards) throws StoryStorageException {
		EventInterface event = currentNode.getEvent();

		if (event.getStepId() != ghostEventId)
			return event.getStepId();

		if (upwards)
			return storage.getStorageWires().get(
					event.getWireKey(topEntry.getWireIdx())).floorKey(
					upperGhostId);
		return storage.getStorageWires().get(
				event.getWireKey(topEntry.getWireIdx())).ceilingKey(
				lowerGhostId);
	}

	public static final ActionOfAEvent getRealType(AtomicEvent<?> event) {
		if (event.getType() == ActionOfAEvent.MODIFICATION
				&& event.getState().getBeforeState() != null)
			return ActionOfAEvent.TEST_AND_MODIFICATION;

		return event.getType();
	}

	@SuppressWarnings("unchecked")
	public final <E> WalkResult walk(E state, WalkResult tillValue)
			throws StoryStorageException {
		EventInterface event = currentNode.getEvent();
		WireHashKey wireKey = event.getWireKey(topEntry.getWireIdx());
		boolean upwards = (tillValue == null);

		Long firstId = getFirstEventId(upwards);

		if (firstId == null)
			return WalkResult.NULL;

		EventIteratorInterface eventIterator = storage.eventIterator(wireKey,
				firstId, upwards);

		while (eventIterator.hasNext()) {
			eventIterator.next();

			EventInterface curEvent = eventIterator.value();

			// DELETED
			if (curEvent.getMark() == MarkOfEvent.DELETED)
				continue;

			AtomicEvent<E> atomicEvent = (AtomicEvent<E>) curEvent
					.getAtomicEvent(wireKey);

			// KEPT and UNRESOLVED
			switch (getRealType(atomicEvent)) {
			case TEST:
				if (state != null
						&& state
								.equals(atomicEvent.getState().getBeforeState())) {
					return calcWalkResult(tillValue);
				}
				break;
			case TEST_AND_MODIFICATION:
				if (upwards) {
					if (state != null
							&& state.equals(atomicEvent.getState()
									.getAfterState())) {
						return calcWalkResult(tillValue);
					}
				} else if (state != null
						&& state
								.equals(atomicEvent.getState().getBeforeState())
						|| atomicEvent.getState().getBeforeState() == null) {
					return calcWalkResult(tillValue);
				}
				break;
			case MODIFICATION:
				if (!upwards)
					return WalkResult.DESIRED;

				if (state != null
						&& state.equals(atomicEvent.getState().getAfterState())) {
					return calcWalkResult(tillValue);
				}
				break;
			}

			if (!deleteEvent(curEvent))
				return WalkResult.FAILED;

		}

		return WalkResult.NULL;
	}

	@SuppressWarnings("unchecked")
	public final boolean walkOnAgentWire() throws StoryStorageException {
		EventInterface event = currentNode.getEvent();
		AtomicEvent<State> atomicEvent = (AtomicEvent<State>) event
				.getAtomicEvent(topEntry.getWireIdx());

		boolean upwards = false;

		if (atomicEvent.getState().getAfterState() == null)
			return true;

		if (atomicEvent.getState().getBeforeState() != null) {
			if (event.getStepId() != ghostEventId)
				throw new StoryStorageException(
						"walkOnAgentWire(): wire doesn't have null state before/after modification");

			WalkResult wr = walkOnAgentWireSingleDiretion(true, true, null);

			return wr != WalkResult.FAILED
					&& walkOnAgentWireSingleDiretion(false, true, wr) != WalkResult.FAILED;
		}

		return walkOnAgentWireSingleDiretion(upwards, false, null) != WalkResult.FAILED;
	}

	public final boolean isLastEvent() throws StoryStorageException {
		EventInterface event = currentNode.getEvent();
		WireHashKey wireKey = event.getWireKey(topEntry.getWireIdx());
		Long firstId = getFirstEventId(false);

		if (firstId == null)
			return true;

		EventIteratorInterface eventIterator = storage.eventIterator(wireKey,
				firstId, false);

		while (eventIterator.hasNext()) {
			eventIterator.next();

			EventInterface curEvent = eventIterator.value();

			// DELETED
			if (curEvent.getMark() == MarkOfEvent.DELETED)
				continue;

			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private final WalkResult walkOnAgentWireSingleDiretion(boolean upwards,
			boolean inconsistent, WalkResult tillValue)
			throws StoryStorageException {
		EventInterface event = currentNode.getEvent();
		WireHashKey wireKey = event.getWireKey(topEntry.getWireIdx());
		Long firstId = getFirstEventId(upwards);

		if (firstId == null)
			return WalkResult.NULL;

		EventIteratorInterface eventIterator = storage.eventIterator(wireKey,
				firstId, upwards);

		while (eventIterator.hasNext()) {
			eventIterator.next();

			EventInterface curEvent = eventIterator.value();

			// DELETED
			if (curEvent.getMark() == MarkOfEvent.DELETED)
				continue;

			AtomicEvent<State> curAtomicEvent = (AtomicEvent<State>) curEvent
					.getAtomicEvent(wireKey);

			// KEPT and UNRESOLVED
			State state = null;
			switch (curAtomicEvent.getType()) {
			case TEST:
				state = curAtomicEvent.getState().getBeforeState();
				break;
			case TEST_AND_MODIFICATION:
			case MODIFICATION:
				if (upwards) {
					state = curAtomicEvent.getState().getAfterState();
				} else {
					state = curAtomicEvent.getState().getBeforeState();
				}
				break;
			}
			if (!inconsistent) {
				if (state == null)
					return WalkResult.DESIRED;
			} else if (state != null) {
				return calcWalkResult(tillValue);
			}

			if (!deleteEvent(curEvent))
				return WalkResult.FAILED;
		}

		return WalkResult.NULL;
	}

	private final WalkResult calcWalkResult(WalkResult tillValue) {
		return tillValue == WalkResult.NULL ? WalkResult.FAILED
				: WalkResult.DESIRED;
	}

	private final boolean deleteEvent(EventInterface currentEvent)
			throws StoryStorageException {
		if (currentEvent.getMark() == MarkOfEvent.KEPT)
			return false;

		candidatesToDelete.add(currentEvent);
		return true;
	}

	private final boolean addDeletedEvents() throws StoryStorageException {
		candidatesToDelete.clear();

		if (currentNode.getEvent().getAtomicEvent(topEntry.getWireIdx())
				.getType() == ActionOfAEvent.TEST
				|| isLastEvent()) {
			topEntry.nextState();
			return true;
		}

		switch (currentNode.getWireType()) {
		case INTERNAL_STATE:
			String currentInternalState = (String) topEntry.nextState();

			if (!isWalkSucceeded(currentInternalState)) {
				return false;
			}
			break;
		case BOUND_FREE:
			State currentBoundState = (State) topEntry.nextState();

			if (!isWalkSucceeded(currentBoundState)) {
				return false;
			}
			break;
		case LINK_STATE:
			StateOfLink currentLinkState = (StateOfLink) topEntry.nextState();

			if (!isWalkSucceeded(currentLinkState)) {
				return false;
			}
			break;
		case AGENT:
			topEntry.nextState();
			if (!walkOnAgentWire())
				return false;
			break;
		}

		int srcQueueSize = uninvestigatedQueue.size();

		if (candidatesToDelete.size() + uninvestigatedQueue.size() > maxQueueSize) {
			maxQueueSizeReached = true;
			return false;
		}

		for (EventInterface e : candidatesToDelete) {
			e.setMark(MarkOfEvent.DELETED, information);
			uninvestigatedQueue.add(new QueueEntry(e, srcQueueSize, wireStack
					.size(), currentNodeIdx));
		}

		return true;
	}

	private final <E> boolean isWalkSucceeded(E state) throws StoryStorageException {
		WalkResult walkResult = walk(state, null);

		if (walkResult == WalkResult.FAILED
				|| walk(state, walkResult) == WalkResult.FAILED)
			return false;
		return true;
	}
}
