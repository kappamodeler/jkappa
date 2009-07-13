package com.plectix.simulator.components.stories.storage;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public interface IWireStorage
{
	/**
	 * @return Initial event 
	 * @throws StoryStorageException 
	 */
	ICEvent initialEvent () throws StoryStorageException;
	
	/**
	 * @return Observable event 
	 * @throws StoryStorageException 
	 */
	ICEvent observableEvent () throws StoryStorageException;
	
	/**
	 * Mark all events UNRESOLVED and calculate count of UNRESOLVED for each wire
	 * @throws StoryStorageException 
	 */
	void markAllUnresolved () throws StoryStorageException;
	
	/**
	 * Event getter
	 * @param wkey
	 * @param event
	 * @return
	 */
	ICEvent getEvent (WireHashKey wkey, Long event);
	
	/**
	 * Atomic event getter
	 * @param wkey
	 * @param event
	 * @return
	 */
	AtomicEvent<?> getAtomicEvent (WireHashKey wkey, Long event);
	
	/**
	 * Event iterator
	 * @param reverse : true - goUpwards from bottom, against the current of real time
	 * @return
	 * @throws StoryStorageException 
	 */
	IEventIterator eventIterator (boolean reverse) throws StoryStorageException;

	/**
	 * Event iterator within wire
	 * @param wkey
	 * @param reverse : true - goUpwards from bottom, against the current of real time
	 * @return
	 * @throws StoryStorageException 
	 */
	IEventIterator eventIterator (WireHashKey wkey, boolean reverse) throws StoryStorageException;

	/**
	 * Event iterator within wire
	 * @param wkey
	 * @param first
	 * @param reverse : true - goUpwards from bottom, against the current of real time
	 * @return
	 * @throws StoryStorageException 
	 */
	IEventIterator eventIterator (WireHashKey wkey, Long first, boolean reverse) throws StoryStorageException;
	
	/**
	 * Get count of UNRESOLVED modify event within wire
	 * @param wkey
	 * @return
	 */
	int getUnresolvedModifyCount (WireHashKey wkey);

	/**
	 * Put count of UNRESOLVED modify event within wire
	 * @param wkey
	 * @return
	 */
	void putUnresolvedModifyEvent(WireHashKey wireHashKey, Integer valueOf);

	
	/**
	 * Get iterator for all atomic states on a wire
	 * @param wkey. Only for INTERNAL_STATE wire
	 * @return
	 * @throws StoryStorageException 
	 */
	Iterator<Integer> wireInternalStateIterator (WireHashKey wkey) throws StoryStorageException;
	
	/**
	 * Get iterator for all agent types
	 * @return
	 */
	Iterator<Integer> agentTypeIterator ();
	
	/**
	 * Get iterator for all agents with given type
	 * @param typeId
	 * @return
	 */
	Iterator<Long> agentIterator (int typeId);
	
	/**
	 * Make storage with swapped agents 
	 * @param agents1 Agent IDs to swap
	 * @param agents2 Agent IDs to swap with
	 * @param firstEventId Event ID to start swapping from
	 * @param swapTop If true then swap higher atomic events
	 * @return new storage
	 */
	IWireStorage swapAgents(Long[] agents1, Long[] agents2, Long firstEventId, boolean swapTop);
	

	
	
	
	
	
	
	
	
	
	//internal procedures
	//////////////////////////////////////////////////////////////////////////


	public boolean isImportantStory();
	
	public void handling() throws StoryStorageException;	
	
	public void addEventContainer(CEvent eventContainer, boolean putToSeconfMap) throws StoryStorageException;
	
	public void addLastEventContainer(CEvent eventContainer, double currentTime) throws StoryStorageException ;
	
	public void setEndOfStory();
	
	public Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> getStorageWires();

	public Map<CEvent, Map<WireHashKey, AtomicEvent<?>>> getWiresByEvent();

	void clearList();



}

