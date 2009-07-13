package com.plectix.simulator.components.stories.compressions;

import java.util.ArrayList;
import java.util.Stack;

import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.EState;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CStateOfLink;
import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;

public class WeakCompression 
{
	private IWireStorage storage;
	private ICEvent boundaryEvent = null;
	private boolean upperBound = true;
	
	public WeakCompression (IWireStorage storage) 
	{
		this.storage = storage;
	}
	
	public void setBoundaryEvent (ICEvent event, boolean upperBound)
	{
		this.boundaryEvent = event;
		this.upperBound = upperBound;
	}
	
	public boolean process () throws StoryStorageException 
	{		
		Stack<ICEvent> searchStack = new Stack<ICEvent>();
		ICEvent initialEvent = storage.initialEvent();
		ICEvent observableEvent = storage.observableEvent();
		boolean compressed = false;

		storage.markAllUnresolved();
		
		// Add initial event to stack
		//initialEvent.setMark(EMarkOfEvent.KEPT);
		searchStack.add(initialEvent);
		
		// Add observable event to stack
		observableEvent.setMark(EMarkOfEvent.KEPT,storage);

		searchStack.add(observableEvent);
		
		while (!searchStack.empty())
		{
			ICEvent top = searchStack.peek();
			
			ICEvent nextEvent = selectEventToBranch(top);
			
			if (nextEvent == null)
			{
				searchStack.pop();
				continue;
			}
			
			nextEvent.setMark(EMarkOfEvent.DELETED, storage);
			
			if (!propagate(nextEvent))
			{
				nextEvent.setMark(EMarkOfEvent.KEPT, storage);
				searchStack.push(nextEvent);
			} else if (!compressed)
				compressed = true;
		}
		
		return compressed;
	}

	private ICEvent selectEventToBranch (ICEvent keptEvent) throws StoryStorageException
	{
		WireHashKey selectedWire = keptEvent.getWireWithMinimumUresolvedEvent(storage);
		
		if (selectedWire == null)
			return null;

		IEventIterator eventIterator = storage.eventIterator(selectedWire, true);
				
		while (eventIterator.hasNext())
		{
			eventIterator.next();
				
			ICEvent nextEvent = eventIterator.value(); 
					
			if (nextEvent.getMark() == EMarkOfEvent.UNRESOLVED)
				return nextEvent; 
		}
		
		throw new StoryStorageException("selectEventToBranch(): no UNRESOLVED event");
	}
	
	private ArrayList<QueueEntry> uninvestigatedQueue = new ArrayList<QueueEntry>();
	private Stack<StackEntry> wireStack = new Stack<StackEntry>();
	private ArrayList<ICEvent> candidatesToDelete = new ArrayList<ICEvent>();
	
	int currentNodeIdx;
	QueueEntry currentNode = null;
	StackEntry topEntry = null;
	
	private boolean propagate (ICEvent event) throws StoryStorageException
	{
		uninvestigatedQueue.clear();
		wireStack.clear();
		
		uninvestigatedQueue.add(new QueueEntry(event, 0, 0, 0));
		
		currentNodeIdx = 0;
		currentNode = uninvestigatedQueue.get(currentNodeIdx);
		
		pushEntry();
		
		while (currentNodeIdx != uninvestigatedQueue.size())
		{
			currentNode = uninvestigatedQueue.get(currentNodeIdx);
			
			// Shift to next event in queue
			if (currentNode.currentWireIdx == currentNode.getWireCount())
			{
				currentNodeIdx++;
				continue;
			}
			
			topEntry = wireStack.peek();
			
			// Push next wire from event to stack
			if (currentNode.currentWireIdx != topEntry.getWireIdx())
			{
				pushEntry();
				continue;
			}
			
			// Add new events to queue
			if (topEntry.hasNextState())
			{
				// Find block of events on current wire to delete
				if (addDeletedEvents())
					currentNode.currentWireIdx++;
				
				// Go to next wire state
				continue;
			}
			
			
			// Restore stack and queue
			// Backtrack to previous wire
			if (currentNode.currentWireIdx > 0)
			{
				currentNode.currentWireIdx--;
				decreaseStack(wireStack.size() - 1);

				QueueEntry last = uninvestigatedQueue.get(uninvestigatedQueue.size() - 1);
				int previousPos = last.getQueuePosIdx();
				
				// If queue has events added from previous wire
				if (previousPos == currentNodeIdx && 
					uninvestigatedQueue.get(previousPos).getStackSize() == wireStack.size())
					decreaseQueue(last.getQueueSize());

				continue;
			}
				
			// Backtrack to source event and wire
			decreaseQueue(currentNode.getQueueSize());
			decreaseStack(currentNode.getStackSize());
				
			currentNodeIdx = currentNode.getQueuePosIdx();
			
			if (uninvestigatedQueue.size() > 0)
				uninvestigatedQueue.get(currentNodeIdx).currentWireIdx = wireStack.peek().getWireIdx();
		}
		
		if (currentNodeIdx > 0)
			return true;
		return false;
	}
	
	private void pushEntry () throws StoryStorageException
	{
		ICEvent event = currentNode.getEvent();
		
		if (event.getAtomicEventCount() == 0)
			throw new StoryStorageException("pushEntry(): event has no non-BOUND/FREE atomic events");
			
		if (currentNode.getWireType() == ETypeOfWire.BOUND_FREE)
			throw new StoryStorageException("pushEntry(): unexpected BOUND/FREE wire");
		
		wireStack.push(new StackEntry(storage, event, currentNode.currentWireIdx));
	}
	
	private void decreaseQueue (int size) throws StoryStorageException
	{
		while (uninvestigatedQueue.size() != size)
		{
			QueueEntry last = uninvestigatedQueue.get(uninvestigatedQueue.size() - 1);
			last.getEvent().setMark(EMarkOfEvent.UNRESOLVED, storage);
			uninvestigatedQueue.remove(uninvestigatedQueue.size() - 1);
		}
	}
	
	private void decreaseStack (int size)
	{
		while (wireStack.size() != size)
			wireStack.pop();
	}
	
	@SuppressWarnings("unchecked")
	public boolean walkOnInternalStateWire (Integer state, boolean upwards) throws StoryStorageException
	{
		ICEvent event = currentNode.getEvent();
		WireHashKey wireKey = event.getWireKey(topEntry.getWireIdx());

		IEventIterator eventIterator = storage.eventIterator(wireKey, event.getStepId(), upwards);

		while (eventIterator.hasNext())
		{
			eventIterator.next();

			ICEvent curEvent = eventIterator.value();

			// DELETED
			if (curEvent.getMark() == EMarkOfEvent.DELETED)
				continue;

			AtomicEvent<Integer> atomicEvent = (AtomicEvent<Integer>) curEvent.getAtomicEvent(wireKey);

			// KEPT and UNRESOLVED
			switch (atomicEvent.getType())
			{
			case TEST:
				if (state != atomicEvent.getState().getBeforeState())
				{
					if (!deleteEvent(curEvent))
						return false;
					continue;
				}
				return true;
			case TEST_AND_MODIFICATION:
				if (upwards)
				{
					if (state != atomicEvent.getState().getAfterState())
					{
						if (!deleteEvent(curEvent))
							return false;
						continue;
					}
				} else if (state != atomicEvent.getState().getBeforeState())
				{
					if (!deleteEvent(curEvent))
						return false;
					continue;
				}
				return true;
			case MODIFICATION:
				if (upwards && state != atomicEvent.getState().getAfterState())
				{
					if (!deleteEvent(curEvent))
						return false;
					continue;
				}
				return true;
			}
		}
		return true;
	}
		
	@SuppressWarnings("unchecked")
	public boolean walkOnAgentWire () throws StoryStorageException
	{
		ICEvent event = currentNode.getEvent();
		WireHashKey wireKey = event.getWireKey(topEntry.getWireIdx());
		AtomicEvent<EState> atomicEvent = (AtomicEvent<EState>) event.getAtomicEvent(topEntry.getWireIdx());

		if (atomicEvent.getType() == EActionOfAEvent.TEST)
			return true;

		boolean upwards = false;
		EState state = null;

		if (atomicEvent.getState().getBeforeState() == null)
		{
			state = atomicEvent.getState().getAfterState();
		} else if (atomicEvent.getState().getAfterState() == null)
		{
			upwards = true;
			state = atomicEvent.getState().getBeforeState();
		} else
			throw new StoryStorageException("walkOnAgentWire(): wire doesn't have null state before/after modification");

		IEventIterator eventIterator = storage.eventIterator(wireKey, event.getStepId(), upwards);

		while (eventIterator.hasNext())
		{
			eventIterator.next();

			ICEvent curEvent = eventIterator.value();

			// DELETED
			if (curEvent.getMark() == EMarkOfEvent.DELETED)
				continue;

			AtomicEvent<EState> curAtomicEvent = (AtomicEvent<EState>) curEvent.getAtomicEvent(wireKey);

			// KEPT and UNRESOLVED
			switch (curAtomicEvent.getType())
			{
			case TEST:
				if (!deleteEvent(curEvent))
					return false;
				continue;
			case TEST_AND_MODIFICATION:
			case MODIFICATION:
				if (!deleteEvent(curEvent))
					return false;
				
				if (upwards)
				{
					if (state != curAtomicEvent.getState().getAfterState() || curAtomicEvent.getState().getBeforeState() != null)
						throw new StoryStorageException("walkOnAgentWire(): inconsistent storage");
					return true;
				}

				if ((curAtomicEvent.getType() == EActionOfAEvent.TEST_AND_MODIFICATION &&
						state != curAtomicEvent.getState().getBeforeState()) ||
						curAtomicEvent.getState().getAfterState() != null)
					throw new StoryStorageException("walkOnAgentWire(): inconsistent storage");

				return true;
			}
		}
		return true;
	}
	
	final static CStateOfLink freeLinkState = new CStateOfLink(CStateOfLink.FREE, CStateOfLink.FREE);
	
	@SuppressWarnings("unchecked")
	public boolean walkOnLinkStateWire () throws StoryStorageException
	{
		ICEvent event = currentNode.getEvent();
		AtomicEvent<CStateOfLink> atomicEvent = (AtomicEvent<CStateOfLink>) event.getAtomicEvent(topEntry.getWireIdx());

		if (atomicEvent.getType() == EActionOfAEvent.TEST)
			return true;

		boolean bothDirections = false;
		boolean upwards = false;

		if (atomicEvent.getState().getBeforeState() == null || freeLinkState.equals(atomicEvent.getState().getBeforeState()))
		{
			upwards = false;
		} else if (freeLinkState.equals(atomicEvent.getState().getAfterState()))
		{
			upwards = true;
		} else if (atomicEvent.getState().getAfterState() == null)
		{
			throw new StoryStorageException("walkOnLinkStateWire(): inconsistent storage");
		} else
			bothDirections = true;
		
		// TODO: more advanced deletion of events needed 
		if (bothDirections)
			return walkOnLinkStateSingleDirection(false) && walkOnLinkStateSingleDirection(true);
		
		return walkOnLinkStateSingleDirection(upwards);
	}
	
	@SuppressWarnings("unchecked")
	private boolean walkOnLinkStateSingleDirection (boolean upwards) throws StoryStorageException
	{
		ICEvent event = currentNode.getEvent();
		WireHashKey wireKey = event.getWireKey(topEntry.getWireIdx());
		
		IEventIterator eventIterator = storage.eventIterator(wireKey, event.getStepId(), upwards);

		while (eventIterator.hasNext())
		{
			eventIterator.next();

			ICEvent curEvent = eventIterator.value();

			// DELETED
			if (curEvent.getMark() == EMarkOfEvent.DELETED)
				continue;

			AtomicEvent<CStateOfLink> curAtomicEvent = (AtomicEvent<CStateOfLink>) curEvent.getAtomicEvent(wireKey);

			// KEPT and UNRESOLVED
			if (!deleteEvent(curEvent))
				return false;
			if (curAtomicEvent.getType() == EActionOfAEvent.TEST)
				continue;

			if (upwards)
			{
				if (curAtomicEvent.getState().getBeforeState() == null || 
						freeLinkState.equals(curAtomicEvent.getState().getBeforeState()))
					return true;
				continue;
			}
			
			if (curAtomicEvent.getState().getAfterState() == null)
				throw new StoryStorageException("walkOnLinkStateSingleDirection(): inconsistent storage");

			if (freeLinkState.equals(curAtomicEvent.getState().getAfterState()))
				return true;
		}
		
		return true;
	}
	
	private boolean deleteEvent (ICEvent curEvent) throws StoryStorageException
	{
		if (curEvent.getMark() == EMarkOfEvent.KEPT)
			return false;

		candidatesToDelete.add(curEvent);
		return true;
	}

	private boolean addDeletedEvents () throws StoryStorageException
	{
		candidatesToDelete.clear();
		
		switch (currentNode.getWireType())
		{
		case INTERNAL_STATE:
			Integer currentInternalState = topEntry.nextState();
			
			if (!walkOnInternalStateWire(currentInternalState, false) || 
				!walkOnInternalStateWire(currentInternalState, true))
				return false;
			break;
		case LINK_STATE:
			topEntry.selectNonIterable();
			if (!walkOnLinkStateWire())
				return false;
			break;
		case AGENT:
			topEntry.selectNonIterable();
			if (!walkOnAgentWire())
				return false;
			break;
		}

		int srcQueueSize = uninvestigatedQueue.size();
		
		for (ICEvent e: candidatesToDelete)
		{
			e.setMark(EMarkOfEvent.DELETED, storage);
			uninvestigatedQueue.add(new QueueEntry(e, srcQueueSize, wireStack.size(), currentNodeIdx));
		}
		
		return true;	
	}

}
