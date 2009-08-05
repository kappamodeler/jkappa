package com.plectix.simulator.components.stories.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.plectix.simulator.components.stories.compressions.CompressionPassport;

public interface IWireStorage
{
	/**
	 * @return Initial event 
	 * @throws StoryStorageException 
	 */
	CEvent initialEvent () throws StoryStorageException;
	
	/**
	 * @return Observable event 
	 * @throws StoryStorageException 
	 */
	CEvent observableEvent () throws StoryStorageException;
	
	/**
	 * Mark all events UNRESOLVED and calculate count of UNRESOLVED for each wire
	 * @throws StoryStorageException 
	 */
	void markAllUnresolved () throws StoryStorageException;
	
	
	/**
	 * Mark all UNRESOLVED events as DELETED
	 * @throws StoryStorageException
	 */
	boolean markAllUnresolvedAsDeleted() throws StoryStorageException;

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
	 * @throws StoryStorageException 
	 */
	int getUnresolvedModifyCount (WireHashKey wkey) throws StoryStorageException;

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
	 * @param wkey
	 * @return
	 * @throws StoryStorageException
	 */
	Iterator<CStateOfLink> wireLinkStateIterator(WireHashKey wkey) throws StoryStorageException;

	/**
	 * Create interface class for strong compression 
	 * @return
	 */
	CompressionPassport extractPassport();
	/**
	 * remove wire if it doesn't contain atomic events. Return true - removing is successful
	 * @param arrayList
	 * @return
	 */
	boolean removeWire(ArrayList<WireHashKey> arrayList);
	
	//internal procedures
	//////////////////////////////////////////////////////////////////////////


	public boolean isImportantStory();
	
	public void handling() throws StoryStorageException;	
	
	public void addEventContainer(CEvent eventContainer, boolean putToSeconfMap) throws StoryStorageException;
	
	public void addLastEventContainer(CEvent eventContainer, double currentTime) throws StoryStorageException ;
	
	public void setEndOfStory();
	
	public Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> getStorageWires();

	//public Map<CEvent, Map<WireHashKey, AtomicEvent<?>>> getWiresByEvent();
	
	public Set<CEvent> getEvents();

	void clearList();


	void replaceWireToWire(Map<WireHashKey, WireHashKey> map,
			Long firstEventId, boolean swapTop,
			TreeMap<Long, AtomicEvent<?>> allEventsByNumber) throws StoryStorageException;


	double getAverageTime();

	void markAllNull() throws StoryStorageException;
	
	int getIteration();
	/**
	 * old1 and new1 has same siteId
	 * old2 and new2 has same siteId
	 * a_1(x!1),b_1(x!1) <-> a_2(x!1),b_2(x!2)
	 * old1 - linkstate a_1(x) on wk1
	 * new1 - linkstate a_2(x) on wk2
	 * old2 - linkstate b_1(x) on wk3
	 * new2 - linkstate b_2(x) on wk4
	 * 
	 * @param old1 
	 * @param new1
	 * @param old2
	 * @param new2
	 * "first" events changes too. if top =true than initial event may be changed
	 */
	public void correctLinkStates(CStateOfLink old1,WireHashKey wk1,CStateOfLink new1,WireHashKey wk2, CStateOfLink old2,WireHashKey wk3,CStateOfLink new2, WireHashKey wk4, Long first, boolean top);


}

