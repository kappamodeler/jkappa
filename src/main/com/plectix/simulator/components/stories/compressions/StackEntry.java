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
	private Integer frozenState = null;
	/**
	 * Iterator for all possible internal states 
	 */
	private Iterator<Integer> stateIterator = null;
	
	boolean hasNextState = true;
	
	public StackEntry (IWireStorage storage, ICEvent event, int wireIdx) throws StoryStorageException
	{
		this.wireIdx = wireIdx;
		
		// Check if it is frozen
		initEntry(storage, event);
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
	
	public void selectNonIterable ()
	{
		hasNextState = false;
	}
	
	public Integer nextState ()
	{
		if (frozenState != null)
		{
			hasNextState = false;
			return frozenState;
		}
			
		return stateIterator.next();
	}
	
	private void initEntry (IWireStorage storage, ICEvent event) throws StoryStorageException
	{
		ETypeOfWire type = event.getAtomicEventType(wireIdx);
		
		if (type != ETypeOfWire.INTERNAL_STATE)
			return;
		
		detectFrozenState(storage, event.getStepId(), event.getWireKey(wireIdx), false);
		if (frozenState == null)
			detectFrozenState(storage, event.getStepId(), event.getWireKey(wireIdx), true);
		
		if (frozenState == null && type == ETypeOfWire.INTERNAL_STATE)
			this.stateIterator = storage.wireInternalStateIterator(event.getWireKey(wireIdx));
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
			
			if (curMark == EMarkOfEvent.KEPT)
			{
				AtomicEvent<?> atomicEvent = curEvent.getAtomicEvent(wireKey);
				
				switch (atomicEvent.getType())
				{
				case TEST:
					frozenState = (Integer) atomicEvent.getState().getBeforeState();
					break;
				case TEST_AND_MODIFICATION:
					if (upwards)
						frozenState = (Integer) atomicEvent.getState().getAfterState();
					else
						frozenState = (Integer) atomicEvent.getState().getBeforeState();
					break;
				case MODIFICATION:
					if (upwards)
						frozenState = (Integer) atomicEvent.getState().getAfterState();
				}
			}
			
			break;
		}
	}
}
