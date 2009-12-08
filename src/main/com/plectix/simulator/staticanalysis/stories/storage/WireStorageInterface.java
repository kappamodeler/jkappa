package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.plectix.simulator.staticanalysis.stories.compressions.CompressionPassport;

public interface WireStorageInterface {
	/**
	 * @return Initial event
	 * @throws StoryStorageException
	 */
	Event initialEvent() throws StoryStorageException;

	/**
	 * @return Observable event
	 * @throws StoryStorageException
	 */
	Event observableEvent() throws StoryStorageException;

	/**
	 * Mark all events UNRESOLVED and calculate count of UNRESOLVED for each
	 * wire
	 * 
	 * @throws StoryStorageException
	 */
	void markAllUnresolved() throws StoryStorageException;

	/**
	 * Mark all UNRESOLVED events as DELETED
	 * 
	 * @throws StoryStorageException
	 */
	boolean markAllUnresolvedAsDeleted() throws StoryStorageException;

	/**
	 * Atomic event getter
	 * 
	 * @param wkey
	 * @param event
	 * @return
	 */
	AtomicEvent<?> getAtomicEvent(WireHashKey wkey, Long event);

	/**
	 * Event iterator within wire
	 * 
	 * @param wkey
	 * @param reverse
	 *            : true - goUpwards from bottom, against the current of real
	 *            time
	 * @return
	 * @throws StoryStorageException
	 */
	EventIteratorInterface eventIterator(WireHashKey wkey, boolean reverse)
			throws StoryStorageException;

	/**
	 * Event iterator within wire
	 * 
	 * @param wkey
	 * @param first
	 * @param reverse
	 *            : true - goUpwards from bottom, against the current of real
	 *            time
	 * @return
	 * @throws StoryStorageException
	 */
	EventIteratorInterface eventIterator(WireHashKey wkey, Long first,
			boolean reverse) throws StoryStorageException;

	// /**
	// * Get count of UNRESOLVED modify event within wire
	// *
	// * @param wkey
	// * @return
	// * @throws StoryStorageException
	// */
	// int getUnresolvedModifyCount(WireHashKey wkey) throws
	// StoryStorageException;

	// /**
	// * Put count of UNRESOLVED modify event within wire
	// *
	// * @param wkey
	// * @return
	// */
	// void putUnresolvedModifyEvent(WireHashKey wireHashKey, int valueOf);

	// /**
	// * Get iterator for all atomic states on a wire
	// *
	// * @param wkey
	// * . Only for INTERNAL_STATE wire
	// * @return
	// * @throws StoryStorageException
	// */
	// Iterator<String> wireInternalStateIterator(WireHashKey wkey)
	// throws StoryStorageException;

	// /**
	// * @param wkey
	// * @return
	// * @throws StoryStorageException
	// */
	// Iterator<StateOfLink> wireLinkStateIterator(WireHashKey wkey)
	// throws StoryStorageException;

	/**
	 * Create interface class for strong compression
	 * 
	 * @return
	 */
	CompressionPassport extractPassport();

	/**
	 * remove wire if it doesn't contain atomic events. Return true - removing
	 * is successful
	 * 
	 * @param arrayList
	 * @return
	 */
	boolean removeWire(ArrayList<WireHashKey> arrayList);

	// internal procedures
	// ////////////////////////////////////////////////////////////////////////

	public boolean isImportantStory();

	public void handling() throws StoryStorageException;

	public void addEventContainer(Event eventContainer)
			throws StoryStorageException;

	public void addLastEventContainer(Event eventContainer, double currentTime)
			throws StoryStorageException;

	public Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> getStorageWires();

	// public Map<CEvent, Map<WireHashKey, AtomicEvent<?>>> getWiresByEvent();

	public Set<Event> getEvents();

	void clearList();

	void replaceWireToWire(Map<WireHashKey, WireHashKey> map,
			Long firstEventId, boolean swapTop,
			TreeMap<Long, AtomicEvent<?>> allEventsByNumber)
			throws StoryStorageException;

	double getAverageTime();

	void markAllNull() throws StoryStorageException;

	int getIteration();

	public StoriesAgentTypesStorage getStoriesAgentTypesStorage();

	void updateWires(Set<WireHashKey> sets) throws StoryStorageException;

	boolean tryToSwap(long agentId1, WireHashKey wk);

	MasterInformationAboutWires getInformationAboutWires();
}
