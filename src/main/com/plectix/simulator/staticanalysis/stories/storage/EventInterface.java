package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.Iterator;

import com.plectix.simulator.staticanalysis.stories.MarkOfEvent;
import com.plectix.simulator.staticanalysis.stories.TypeOfWire;


public interface EventInterface {
	/**
	 * Get ID
	 * @return
	 */
	long getStepId();
	
	/**
	 * Get iterator for all atomic events within event
	 * @return
	 */
	Iterator<WireHashKey> wireEventIterator ();
	

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
	TypeOfWire    getAtomicEventType (int index) throws StoryStorageException;
	
	/**
	 * @return
	 */
	MarkOfEvent getMark ();
	
	/**
	 * Mark event with state UNRESOLVED/DELETED/KEPT
	 * Update count of unresolved events for each wire 
	 * @param mark
	 * @throws StoryStorageException 
	 */
	void setMark (MarkOfEvent mark,MasterInformationAboutWires information) throws StoryStorageException;	
	
	
	/**
	 * 
	 * @return wire with non-zero unresolved Event or null
	 * @throws StoryStorageException 
	 */
	WireHashKey getWireWithMinimumUresolvedEvent(MasterInformationAboutWires information) throws StoryStorageException;

	int getRuleId();
}
