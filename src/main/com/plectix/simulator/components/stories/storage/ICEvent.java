package com.plectix.simulator.components.stories.storage;

import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;


public interface ICEvent
{
	/**
	 * Get ID
	 * @return
	 */
	long getStepId();
	
	/**
	 * Get iterator for all atomic events within event
	 * @return
	 */
	IAtomicEventIterator wireEventIterator ();
	

	/**
	 * Check if event contains atomic event on a wire
	 * @param wireKey
	 * @return
	 */
	boolean containsWire (WireHashKey wireKey);
	
	/**
	 * Get atomic event by wire key
	 * @param wireKey
	 * @return
	 * @throws StoryStorageException 
	 */
	AtomicEvent<?> getAtomicEvent (WireHashKey wireKey) throws StoryStorageException;
	
	/**
	 * Get number of atomic events within event
	 * BOUND/FREE events are ignored
	 * @return
	 * @throws StoryStorageException 
	 */
	int getAtomicEventCount () throws StoryStorageException;
	
	// TODO: Agent wires first
	
	/**
	 * Get wire key from list with search order 
	 * BOUND/FREE events are ignored
	 * @param index from range [0..n-1] where n is number of atomic events
	 * @return
	 * @throws StoryStorageException 
	 */
	WireHashKey    getWireKey (int index) throws StoryStorageException;
	
	/**
	 * Get atomic event from list with search order
	 * BOUND/FREE events are ignored
	 * @param index from range [0..n-1] where n is number of atomic events
	 * @return
	 * @throws StoryStorageException 
	 */
	AtomicEvent<?> getAtomicEvent (int index) throws StoryStorageException;

	/**
	 * Get wire type from list with search order
	 * BOUND/FREE events are ignored
	 * @param index from range [0..n-1] where n is number of atomic events
	 * @return
	 * @throws StoryStorageException 
	 */
	ETypeOfWire    getAtomicEventType (int index) throws StoryStorageException;
	
	/**
	 * @return
	 */
	EMarkOfEvent getMark ();
	
	/**
	 * Mark event with state UNRESOLVED/DELETED/KEPT
	 * Update count of unresolved events for each wire 
	 * @param mark
	 * @throws StoryStorageException 
	 */
	void setMark (EMarkOfEvent mark,IWireStorage storage) throws StoryStorageException;	
	
	
	/**
	 * 
	 * @return wire with non-zero unresolved Event or null
	 * @throws StoryStorageException 
	 */
	WireHashKey getWireWithMinimumUresolvedEvent(IWireStorage storage) throws StoryStorageException;

	int getRuleId();

}
