package com.plectix.simulator.staticanalysis.stories.compressions;

import java.util.Iterator;

import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;
import com.plectix.simulator.staticanalysis.stories.MarkOfEvent;
import com.plectix.simulator.staticanalysis.stories.TypeOfWire;
import com.plectix.simulator.staticanalysis.stories.storage.AtomicEvent;
import com.plectix.simulator.staticanalysis.stories.storage.EventInterface;
import com.plectix.simulator.staticanalysis.stories.storage.EventIteratorInterface;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.staticanalysis.stories.storage.WireHashKey;
import com.plectix.simulator.staticanalysis.stories.storage.WireStorageInterface;

/*package*/final class StackEntry {
	/**
	 * Wire index in event
	 */
	private final int wireIdx;
	/**
	 * Frozen state for internal state wire
	 */
	private Object frozenState = null;
	/**
	 * Iterator for all possible internal states
	 */
	private Iterator<?> stateIterator = null;

	private boolean hasNextState = true;

	public StackEntry(int wireIdx) throws StoryStorageException {
		this.wireIdx = wireIdx;
	}

	public final int getWireIdx() {
		return wireIdx;
	}

	public final boolean hasNextState() {
		if (stateIterator != null)
			return stateIterator.hasNext();
		return hasNextState;
	}

	public final Object nextState() {
		if (frozenState != null) {
			hasNextState = false;
			return frozenState;
		}

		return stateIterator.next();
	}

	/**
	 * Check if it is frozen
	 * 
	 * @param weak
	 * @throws StoryStorageException
	 */
	public final void checkFrozenState(WeakCompression weak)
			throws StoryStorageException {
		WireStorageInterface storage = weak.getStorage();
		EventInterface event = weak.getEvent();
		TypeOfWire type = event.getAtomicEventType(wireIdx);

		if (type == TypeOfWire.AGENT
				|| weak.isLastEvent()
				|| event.getAtomicEvent(wireIdx).getType() == ActionOfAEvent.TEST) {
			frozenState = "doesn't matter";
			return;
		}

		if (event.getStepId() != WeakCompression.ghostEventId) { // TODO: frozen
			// state for
			// ghost
			// event
			detectFrozenState(storage, event.getStepId(), event
					.getWireKey(wireIdx), false);
			if (frozenState == null)
				detectFrozenState(storage, event.getStepId(), event
						.getWireKey(wireIdx), true);
		}

		if (frozenState == null) {
			switch (type) {
			case INTERNAL_STATE:
				this.stateIterator = storage.getInformationAboutWires()
						.wireInternalStateIterator(event.getWireKey(wireIdx));
				break;
			case BOUND_FREE:
				this.stateIterator = new BoundSateIterator();
				break;
			case LINK_STATE:
				this.stateIterator = new LinkStateIterator(weak);
				break;
			}
		}
	}

	private final void detectFrozenState(WireStorageInterface storage,
			long eventId, WireHashKey wireKey, boolean upwards)
			throws StoryStorageException {
		EventIteratorInterface eventIterator = storage.eventIterator(wireKey,
				eventId, upwards);

		while (eventIterator.hasNext()) {
			eventIterator.next();

			EventInterface curEvent = eventIterator.value();
			MarkOfEvent curMark = curEvent.getMark();

			if (curMark == MarkOfEvent.DELETED)
				continue;

			if (curMark != MarkOfEvent.KEPT)
				return;

			AtomicEvent<?> atomicEvent = curEvent.getAtomicEvent(wireKey);

			switch (WeakCompression.getRealType(atomicEvent)) {
			case TEST:
				frozenState = atomicEvent.getState().getBeforeState();
				break;
			case TEST_AND_MODIFICATION:
				if (upwards)
					frozenState = atomicEvent.getState().getAfterState();
				else
					frozenState = atomicEvent.getState().getBeforeState();
				break;
			case MODIFICATION:
				if (upwards)
					frozenState = atomicEvent.getState().getAfterState();
			}

			return;
		}
	}
}
