package com.plectix.simulator.components.stories.compressions;

import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.StoryStorageException;

class QueueEntry
{
	/**
	 * Event in queue 
	 */
	private ICEvent event;
	/**
	 * Information to backtrack
	 * @queueSize size queue reduce to
	 * @stackSize size stack reduce to
	 * @queuePosIdx position in queue set to
	 */
	private int queueSize; 
	private int stackSize; 
	private int queuePosIdx;
	
	/**
	 * For current event in queue - current wire index in event 
	 */
	public int currentWireIdx = 0;
	
	public QueueEntry (ICEvent event, int queueSize, int stackSize, int queuePosIdx)
	{
		this.event = event;
		this.queueSize = queueSize;
		this.stackSize = stackSize;
		this.queuePosIdx = queuePosIdx;
	}
	
	public ICEvent getEvent ()
	{
		return event;
	}

	public int getWireCount () throws StoryStorageException
	{
		return event.getAtomicEventCount();
	}

	public ETypeOfWire getWireType () throws StoryStorageException
	{
		return event.getWireKey(currentWireIdx).getTypeOfWire();
	}
	
	public int getQueueSize ()
	{
		return queueSize;
	}

	public int getQueuePosIdx ()
	{
		return queuePosIdx;
	}

	public int getStackSize ()
	{
		return stackSize;
	}
}

