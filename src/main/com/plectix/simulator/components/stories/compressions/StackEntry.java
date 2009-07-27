package com.plectix.simulator.components.stories.compressions;

import java.util.Iterator;

import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;

class StackEntry
{
	/**
	 * Wire index in event
	 */
	private int     wireIdx;
	/**
	 * Frozen state for internal state wire
	 */
	private Object frozenState = null;
	/**
	 * Iterator for all possible internal states 
	 */
	private Iterator<?> stateIterator = null;
	
	boolean hasNextState = true;
	
	public StackEntry (int wireIdx) throws StoryStorageException
	{
		this.wireIdx = wireIdx;
	}
	
	public int getWireIdx ()
	{
		return wireIdx;
	}
	
	public boolean hasNextState ()
	{
		if (stateIterator != null)
			return stateIterator.hasNext();
		return hasNextState;
	}
	
	public Object nextState ()
	{
		if (frozenState != null)
		{
			hasNextState = false;
			return frozenState;
		}
			
		return stateIterator.next();
	}
	
	/**
	 * Check if it is frozen
	 * @param weak
	 * @throws StoryStorageException
	 */
	public void checkFrozenState (WeakCompression weak) throws StoryStorageException
	{
		IWireStorage storage = weak.getStorage();
		ICEvent event = weak.getEvent();
		ETypeOfWire type = event.getAtomicEventType(wireIdx);
		
		if (type == ETypeOfWire.AGENT || weak.isLastEvent())
		{
			frozenState = "doesn't matter";
			return;
		}
		
		if (event.getStepId() != WeakCompression.ghostEventId)
		{ // TODO: frozen state for ghost event
			detectFrozenState(storage, event.getStepId(), event.getWireKey(wireIdx), false);
			if (frozenState == null)
				detectFrozenState(storage, event.getStepId(), event.getWireKey(wireIdx), true);
		}
		
		if (frozenState == null)
		{
			switch (type)
			{
			case INTERNAL_STATE:
				this.stateIterator = storage.wireInternalStateIterator(event.getWireKey(wireIdx));
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
	
	private void detectFrozenState (IWireStorage storage, long eventId, WireHashKey wireKey, boolean upwards) throws StoryStorageException
	{
		IEventIterator eventIterator = storage.eventIterator(wireKey, eventId, upwards);
		
		while (eventIterator.hasNext())
		{
			eventIterator.next();
			
			ICEvent curEvent = eventIterator.value();
			EMarkOfEvent curMark = curEvent.getMark();
			
			if (curMark == EMarkOfEvent.DELETED)
				continue;
			
			if (curMark != EMarkOfEvent.KEPT)
				return;
			
			AtomicEvent<?> atomicEvent = curEvent.getAtomicEvent(wireKey);

			switch (WeakCompression.getRealType(atomicEvent))
			{
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
