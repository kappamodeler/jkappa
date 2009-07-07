package com.plectix.simulator.components.stories.storage;

import java.util.Iterator;

public interface IWireStorage
{
	// Final event
	CEvent observableEvent ();
	Long   observableEventId ();
	
	// Mark all events UNRESOLVED
	void markAllUnresolved ();
	
	// Event getters 
	CEvent getEvent (WireHashKey wkey, Long event);
	AtomicEvent<?> getAtomicEvent (WireHashKey wkey, Long event);
	
	// Event iterator within wire
	IEventIterator eventIterator (WireHashKey wkey, boolean reverse);
	IEventIterator eventIterator (WireHashKey wkey, Long first, boolean reverse);
	
	// Atomic event iterator within event
	IAtomicEventIterator wireEventIterator (CEvent event);
	
	// Get unresolved event count within wire 
	int getUnresolvedCount (WireHashKey wkey);
	
	// Mark event with state
	// Should update count of unresolved events for each wire
	void markEvent     (CEvent event, EMarkOfEvent state);
	
	// Possible internal state iterator within wire
	Iterator<Integer> wireInternalStateIterator (WireHashKey wkey);
}

