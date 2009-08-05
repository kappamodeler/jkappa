package com.plectix.simulator.components.stories.compressions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.EState;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.CStateOfLink;
import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;

enum WalkResult {
	DESIRED, FREE, NULL, FAILED
}

class WeakCompression {
	private IWireStorage storage;

	// For strong compression
	public final static long ghostEventId = -100;
	private int maxQueueSize;
	private boolean maxQueueSizeReached;
	private Long upperGhostId = null;
	private Long lowerGhostId = null;

	public WeakCompression(IWireStorage storage) {
		this.storage = storage;
	}

	public boolean process() throws StoryStorageException {
		return doProcess(null);
	}

	public boolean processInconsistent(ICEvent boundaryEvent, ICEvent neiEvent)
			throws StoryStorageException {
		return doProcess(createGhostEvent(boundaryEvent, neiEvent));
	}

	private boolean doProcess(ICEvent ghostEvent) throws StoryStorageException {
		Stack<ICEvent> searchStack = new Stack<ICEvent>();
		ICEvent initialEvent = storage.initialEvent();
		ICEvent observableEvent = storage.observableEvent();
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
			observableEvent.setMark(EMarkOfEvent.KEPT, storage);
			searchStack.add(observableEvent);

			if (ghostEvent != null) {
				if (!propagate(ghostEvent))
				{
					if (maxQueueSizeReached)
						continue;
					return false;
				}
				ghostEvent = null;
				if (uninvestigatedQueue.size() > 1)
					compressed = true;
			}

			while (!searchStack.empty()) {
				ICEvent top = searchStack.peek();

				ICEvent nextEvent = selectEventToBranch(top);

				if (nextEvent == null) {
					searchStack.pop();
					continue;
				}

				nextEvent.setMark(EMarkOfEvent.DELETED, storage);

				if (!propagate(nextEvent)) {
					nextEvent.setMark(EMarkOfEvent.KEPT, storage);
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
	private ICEvent createGhostEvent(ICEvent boundaryEvent, ICEvent neiEvent)
			throws StoryStorageException {
		CEvent event = new CEvent(ghostEventId, 0, EMarkOfEvent.DELETED);

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
				newAEvent = new AtomicEvent<EState>(
						event,
						upperValue != null ? EActionOfAEvent.TEST_AND_MODIFICATION
								: EActionOfAEvent.MODIFICATION);
				if (upperEvent.getValue().getType() != EActionOfAEvent.TEST
						&& lowerEvent.getValue().getType() != EActionOfAEvent.TEST
						&& upperValue == null
						&& lowerEvent.getValue().getState().getAfterState() != null) {
					((AtomicEvent<EState>) newAEvent).getState()
							.setBeforeState(EState.CHECK_AGENT);
					((AtomicEvent<EState>) newAEvent).getState().setAfterState(
							EState.CHECK_AGENT);
				} else {
					((AtomicEvent<EState>) newAEvent).getState()
							.setBeforeState((EState) upperValue);
					((AtomicEvent<EState>) newAEvent).getState().setAfterState(
							(EState) lowerValue);
				}
				break;
			case BOUND_FREE:
				newAEvent = new AtomicEvent<EState>(
						event,
						upperValue != null ? EActionOfAEvent.TEST_AND_MODIFICATION
								: EActionOfAEvent.MODIFICATION);
				((AtomicEvent<EState>) newAEvent).getState().setBeforeState(
						(EState) upperValue);
				((AtomicEvent<EState>) newAEvent).getState().setAfterState(
						(EState) lowerValue);
				break;

			case INTERNAL_STATE:
				newAEvent = new AtomicEvent<Integer>(
						event,
						upperValue != null ? EActionOfAEvent.TEST_AND_MODIFICATION
								: EActionOfAEvent.MODIFICATION);
				((AtomicEvent<Integer>) newAEvent).getState().setBeforeState(
						(Integer) upperValue);
				((AtomicEvent<Integer>) newAEvent).getState().setAfterState(
						(Integer) lowerValue);
				break;
			case LINK_STATE:
				newAEvent = new AtomicEvent<CStateOfLink>(
						event,
						upperValue != null ? EActionOfAEvent.TEST_AND_MODIFICATION
								: EActionOfAEvent.MODIFICATION);
				((AtomicEvent<CStateOfLink>) newAEvent).getState()
						.setBeforeState((CStateOfLink) upperValue);
				((AtomicEvent<CStateOfLink>) newAEvent).getState()
						.setAfterState((CStateOfLink) lowerValue);
				break;

			}

			event.getAtomicEvents().put(wire.getKey(), newAEvent);
		}

		if (event.getAtomicEventCount() == 0)
			return null;

		return event;
	}

	private ICEvent selectEventToBranch(ICEvent keptEvent)
			throws StoryStorageException {
		WireHashKey selectedWire = keptEvent
				.getWireWithMinimumUresolvedEvent(storage);

		if (selectedWire == null)
			return null;

		IEventIterator eventIterator = storage
				.eventIterator(selectedWire, true);

		while (eventIterator.hasNext()) {
			eventIterator.next();

			ICEvent nextEvent = eventIterator.value();

			if (nextEvent.getMark() == EMarkOfEvent.UNRESOLVED)
				return nextEvent;
		}

		throw new StoryStorageException(
				"selectEventToBranch(): no UNRESOLVED event");
	}

	private ArrayList<QueueEntry> uninvestigatedQueue = new ArrayList<QueueEntry>();
	private Stack<StackEntry> wireStack = new Stack<StackEntry>();
	private ArrayList<ICEvent> candidatesToDelete = new ArrayList<ICEvent>();

	int currentNodeIdx;
	QueueEntry currentNode = null;
	StackEntry topEntry = null;

	private boolean propagate(ICEvent event) throws StoryStorageException {
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

	private void pushEntry() throws StoryStorageException {
		if (getEvent().getAtomicEventCount() == 0)
			throw new StoryStorageException(
					"pushEntry(): event has no non-BOUND/FREE atomic events");

		topEntry = new StackEntry(getWireIdx());
		topEntry.checkFrozenState(this);
		wireStack.push(topEntry);
	}

	public IWireStorage getStorage() {
		return storage;
	}

	public ICEvent getEvent() {
		return currentNode.getEvent();
	}

	public int getWireIdx() {
		return currentNode.currentWireIdx;
	}

	private void decreaseQueue(int size) throws StoryStorageException {
		while (uninvestigatedQueue.size() != size) {
			QueueEntry last = uninvestigatedQueue.get(uninvestigatedQueue
					.size() - 1);
			if (last.getEvent().getStepId() != ghostEventId)
				last.getEvent().setMark(EMarkOfEvent.UNRESOLVED, storage);
			uninvestigatedQueue.remove(uninvestigatedQueue.size() - 1);
		}
	}

	private void decreaseStack(int size) {
		while (wireStack.size() != size)
			wireStack.pop();
	}

	public Long getFirstEventId(boolean upwards) throws StoryStorageException {
		ICEvent event = currentNode.getEvent();

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

	public static EActionOfAEvent getRealType (AtomicEvent<?> event)
	{
		if (event.getType() == EActionOfAEvent.MODIFICATION && event.getState().getBeforeState() != null)
			return EActionOfAEvent.TEST_AND_MODIFICATION;
		
		return event.getType();
	}
	
	@SuppressWarnings("unchecked")
	public <E> WalkResult walk(E state, WalkResult tillValue)
			throws StoryStorageException {
		ICEvent event = currentNode.getEvent();
		WireHashKey wireKey = event.getWireKey(topEntry.getWireIdx());
		boolean upwards = (tillValue == null);
		
		Long firstId = getFirstEventId(upwards);

		if (firstId == null)
			return WalkResult.NULL;

		IEventIterator eventIterator = storage.eventIterator(wireKey, firstId,
				upwards);

		while (eventIterator.hasNext()) {
			eventIterator.next();

			ICEvent curEvent = eventIterator.value();

			// DELETED
			if (curEvent.getMark() == EMarkOfEvent.DELETED)
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
					
				if (state != null && state.equals(atomicEvent.getState().getAfterState())) {
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
	public boolean walkOnAgentWire() throws StoryStorageException {
		ICEvent event = currentNode.getEvent();
		AtomicEvent<EState> atomicEvent = (AtomicEvent<EState>) event
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

	public boolean isLastEvent() throws StoryStorageException {
		ICEvent event = currentNode.getEvent();
		WireHashKey wireKey = event.getWireKey(topEntry.getWireIdx());
		Long firstId = getFirstEventId(false);

		if (firstId == null)
			return true;

		IEventIterator eventIterator = storage.eventIterator(wireKey, firstId,
				false);

		while (eventIterator.hasNext()) {
			eventIterator.next();

			ICEvent curEvent = eventIterator.value();

			// DELETED
			if (curEvent.getMark() == EMarkOfEvent.DELETED)
				continue;

			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private WalkResult walkOnAgentWireSingleDiretion(boolean upwards,
			boolean inconsistent, WalkResult tillValue)
			throws StoryStorageException {
		ICEvent event = currentNode.getEvent();
		WireHashKey wireKey = event.getWireKey(topEntry.getWireIdx());
		Long firstId = getFirstEventId(upwards);

		if (firstId == null)
			return WalkResult.NULL;

		IEventIterator eventIterator = storage.eventIterator(wireKey, firstId,
				upwards);

		while (eventIterator.hasNext()) {
			eventIterator.next();

			ICEvent curEvent = eventIterator.value();

			// DELETED
			if (curEvent.getMark() == EMarkOfEvent.DELETED)
				continue;

			AtomicEvent<EState> curAtomicEvent = (AtomicEvent<EState>) curEvent
					.getAtomicEvent(wireKey);

			// KEPT and UNRESOLVED
			EState state = null;
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

	private WalkResult calcWalkResult(WalkResult tillValue) {
		return tillValue == WalkResult.NULL ? WalkResult.FAILED
				: WalkResult.DESIRED;
	}

	private boolean deleteEvent(ICEvent curEvent) throws StoryStorageException {
		if (curEvent.getMark() == EMarkOfEvent.KEPT)
			return false;

		candidatesToDelete.add(curEvent);
		return true;
	}

	private boolean addDeletedEvents() throws StoryStorageException {
		candidatesToDelete.clear();

		if (currentNode.getEvent().getAtomicEvent(topEntry.getWireIdx()).getType() == EActionOfAEvent.TEST
				|| isLastEvent()) {
			topEntry.nextState();
			return true;
		}

		switch (currentNode.getWireType()) {
		case INTERNAL_STATE:
			Integer currentInternalState = (Integer) topEntry.nextState();

			if (!isWalkSucceeded(currentInternalState)) {
				return false;
			}
			break;
		case BOUND_FREE:
			EState currentBoundState = (EState) topEntry.nextState();

			if (!isWalkSucceeded(currentBoundState)) {
				return false;
			}
			break;
		case LINK_STATE:
			CStateOfLink currentLinkState = (CStateOfLink) topEntry.nextState();

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

		if (candidatesToDelete.size() + uninvestigatedQueue.size() > maxQueueSize)
		{
			maxQueueSizeReached = true;
			return false;
		}
		
		for (ICEvent e : candidatesToDelete) {
			e.setMark(EMarkOfEvent.DELETED, storage);
			uninvestigatedQueue.add(new QueueEntry(e, srcQueueSize, wireStack
					.size(), currentNodeIdx));
		}

		return true;
	}

	private <E> boolean isWalkSucceeded(E state) throws StoryStorageException {
		WalkResult walkResult = walk(state, null);

		if (walkResult == WalkResult.FAILED
				|| walk(state, walkResult) == WalkResult.FAILED)
			return false;
		return true;
	}

}
