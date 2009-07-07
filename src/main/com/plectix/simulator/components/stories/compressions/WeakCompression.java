package com.plectix.simulator.components.stories.compressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.IAtomicEventIterator;
import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.components.stories.storage.EMarkOfEvent;

public class WeakCompression 
{
	private IWireStorage storage;
	
	public WeakCompression (IWireStorage storage) 
	{
		this.storage = storage;
	}
	
	public void process () 
	{		
		Stack<Object[]> searchStack = new Stack<Object[]>();
		CEvent observableEvent = storage.observableEvent();
		Long observableEventId = storage.observableEventId();

		storage.markAllUnresolved();
		
		storage.markEvent(observableEvent, EMarkOfEvent.KEPT);
		searchStack.add(new Object[] {observableEventId, observableEvent});
		
		while (!searchStack.empty())
		{
			CEvent top = (CEvent)searchStack.peek()[1];
			
			Object[] nextEvent = selectEventToBranch(top);
			
			if (nextEvent == null)
			{
				searchStack.pop();
				continue;
			}
			
			CEvent nextEventEntity = (CEvent)nextEvent[1];
			
			storage.markEvent(nextEventEntity, EMarkOfEvent.DELETED);
			
			//if (!propagate((Long)nextEvent[0], nextEventEntity))
			//{
				storage.markEvent(nextEventEntity, EMarkOfEvent.KEPT);
				searchStack.push(nextEvent);
			//}
		}
	}

	private Object[] selectEventToBranch (CEvent keptEvent)
	{
		IAtomicEventIterator iterator = storage.wireEventIterator(keptEvent);
		
		WireHashKey selectedWire = null;
		int minUnresolvedCount; 
		
		if (iterator.hasNext())
		{
			selectedWire = iterator.next();
			minUnresolvedCount = storage.getUnresolvedCount(selectedWire);

			while (iterator.hasNext())
			{
				WireHashKey wire = iterator.next();
				int unresolvedCount = storage.getUnresolvedCount(wire);
				
				if (unresolvedCount > 0)
				{
					if (unresolvedCount < minUnresolvedCount || minUnresolvedCount == 0)
					{
						minUnresolvedCount = unresolvedCount;
						selectedWire = wire;
					}
				}
			}
			
			if (minUnresolvedCount > 0)
			{
				IEventIterator eventIterator = storage.eventIterator(selectedWire, true);
				
				while (eventIterator.hasNext())
				{
					Long eventId = eventIterator.next();
					
					CEvent nextEvent = eventIterator.value(); 
					
					if (nextEvent.getMark() == EMarkOfEvent.UNRESOLVED)
						return new Object[] {eventId, nextEvent}; 
				}
			}
		}
		return null;
	}
	
	private class StackEntry
	{
		private int    wireIdx;
		//private Iterator<Integer> internalStateIterator = null;
		
		public StackEntry (IWireStorage storage, long eventId, ICEvent event, int wireIdx)
		{
			this.wireIdx = wireIdx;
			//this.eventIterator = storage.eventIterator(event.getWireKey(wireIdx), eventId, false); 
		}
		
		public int getWireIdx ()
		{
			return wireIdx;
		}
		
		public boolean hasNextState ()
		{
			// TODO: states enumeration
			return false; 
		}
	}
	
	private class QueueEntry
	{
		private long eventId;
		private ICEvent event;
		private int queueSize; 
		private int stackSize; 
		
		public int currentWireIdx = 0;
		
		public QueueEntry (long eventId, ICEvent event, int queueSize, int stackSize)
		{
			this.eventId = eventId;
			this.event = event;
			this.queueSize = queueSize;
			this.stackSize = stackSize;
		}
		
		public ICEvent getEvent ()
		{
			return event;
		}

		public int getWireCount ()
		{
			return event.getAtomicEventCount();
		}

		public long getEventId ()
		{
			return eventId;
		}
		
		public ETypeOfWire getWireType ()
		{
			return event.getWireKey(currentWireIdx).getKeyOfState();
		}
		
		public int getQueueSize ()
		{
			return queueSize;
		}

		public int getStackSize ()
		{
			return stackSize;
		}
	}
	
	private ArrayList<QueueEntry> uninvestigatedQueue = new ArrayList<QueueEntry>();
	private Stack<StackEntry> wireStack = new Stack<StackEntry>();
	
	int currentNodeIdx;
	QueueEntry currentNode = null;
	StackEntry topEntry = null;
	
	private boolean propagate (Long eventId, ICEvent event)
	{
		uninvestigatedQueue.clear();
		wireStack.clear();
		
		uninvestigatedQueue.add(new QueueEntry(eventId, event, 0, 0));
		wireStack.push(new StackEntry(storage, eventId, event, 0));
		
		currentNodeIdx = 0;
		
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
			
			// Push wire from event to stack
			if (currentNode.currentWireIdx != topEntry.getWireIdx())
			{
				wireStack.push(new StackEntry(storage, currentNode.getEventId(), currentNode.getEvent(), currentNode.currentWireIdx));
				continue;
			}
			
			ETypeOfWire wireType = currentNode.getWireType();
			boolean has_next_state = false;
			
			if (wireType == ETypeOfWire.BOUND_FREE || wireType == ETypeOfWire.INTERNAL_STATE)
				has_next_state = topEntry.hasNextState();
			
			// Find block of events on current wire to delete
			if (has_next_state && addDeletedEvents())
			{
				currentNode.currentWireIdx++;
				continue;
			}
			
			// Restore stack and queue
			if (!has_next_state)
			{
				// Backtrack to previous wire
				if (currentNode.currentWireIdx > 0)
				{
					currentNode.currentWireIdx--;
					wireStack.pop();
					continue;
				}
				
				// Backtrack to source wire
				while (uninvestigatedQueue.size() != currentNode.getQueueSize())
					uninvestigatedQueue.remove(uninvestigatedQueue.size() - 1);
				while (wireStack.size() != currentNode.getStackSize())
					wireStack.pop();
				
				if (uninvestigatedQueue.size() > 0)
				{
					currentNodeIdx = uninvestigatedQueue.size() - 1;
					uninvestigatedQueue.get(currentNodeIdx).currentWireIdx = wireStack.peek().getWireIdx();
				}
			}
		}
		
		if (currentNodeIdx > 0)
			return true;
		return false;
	}
	
	private boolean addDeletedEvents ()
	{
		int srcQueueSize = uninvestigatedQueue.size();
		
		switch (currentNode.getWireType())
		{
		case BOUND_FREE:
			return true;
		case INTERNAL_STATE:
			return true;
		case LINK_STATE:
			return true;
		case AGENT:
			return true;
		}

		while (uninvestigatedQueue.size() != srcQueueSize)
			uninvestigatedQueue.remove(uninvestigatedQueue.size() - 1);
		
		return false;	
	}
}
