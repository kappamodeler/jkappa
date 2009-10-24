package com.plectix.simulator.staticanalysis.stories.compressions;

import com.plectix.simulator.staticanalysis.stories.TypeOfWire;
import com.plectix.simulator.staticanalysis.stories.storage.EventInterface;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;

/*package*/ final class QueueEntry {
	/**
	 * Event in queue
	 */
	private final EventInterface event;
	/**
	 * Information to backtrack
	 * 
	 * @queueSize size queue reduce to
	 * @stackSize size stack reduce to
	 * @queuePosIdx position in queue set to
	 */
	private final int queueSize;
	private final int stackSize;
	private final int queuePosIdx;

	/**
	 * For current event in queue - current wire index in event
	 */
	public int currentWireIdx = 0;

	public QueueEntry(EventInterface event, int queueSize, int stackSize,
			int queuePosIdx) {
		this.event = event;
		this.queueSize = queueSize;
		this.stackSize = stackSize;
		this.queuePosIdx = queuePosIdx;
	}

	public EventInterface getEvent() {
		return event;
	}

	public final int getWireCount() throws StoryStorageException {
		return event.getAtomicEventCount();
	}

	public final TypeOfWire getWireType() throws StoryStorageException {
		return event.getWireKey(currentWireIdx).getTypeOfWire();
	}

	public final int getQueueSize() {
		return queueSize;
	}

	public final int getQueuePosIdx() {
		return queuePosIdx;
	}

	public final int getStackSize() {
		return stackSize;
	}
}
