package com.plectix.simulator.staticanalysis.stories.compressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import com.plectix.simulator.staticanalysis.stories.graphs.StoriesGraphs;
import com.plectix.simulator.staticanalysis.stories.storage.AtomicEvent;
import com.plectix.simulator.staticanalysis.stories.storage.EventInterface;
import com.plectix.simulator.staticanalysis.stories.storage.EventIteratorInterface;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.staticanalysis.stories.storage.WireHashKey;
import com.plectix.simulator.staticanalysis.stories.storage.WireStorageInterface;

public interface CompressionPassport {

	/**
	 * Initialize data for strong compression
	 */
	void prepareForStrong();

	WireStorageInterface getStorage();

	/**
	 * Event iterator
	 * 
	 * @param reverse
	 *            : true - goUpwards from bottom, against the current of real
	 *            time
	 * @return
	 * @throws StoryStorageException
	 */
	EventIteratorInterface eventIterator(boolean reverse)
			throws StoryStorageException;

	/**
	 * Get iterator for all agent types
	 * 
	 * @return
	 */
	Iterator<String> agentTypeIterator();

	/**
	 * Get iterator for all agents with given type
	 * 
	 * @param type
	 * @return
	 */
	Iterator<Long> agentIterator(String type);

	/**
	 * Get all wires of given agent
	 * 
	 * @param agentId
	 * @return
	 */
	ArrayList<WireHashKey> getAgentWires(long agentId);

	/**
	 * Get type of an agent
	 * 
	 * @param agentId
	 * @return
	 * @throws StoryStorageException
	 */
	String getAgentType(long agentId) throws StoryStorageException;

	/**
	 * Check if swapping of two agents is possible (they have the same wires)
	 * 
	 * @param agentId1
	 * @param agentId2
	 * @return
	 */
	boolean isAbleToSwap(long agentId1, long agentId2);

	/**
	 * Make storage with swapped agents
	 * 
	 * @param agents1
	 *            Agent IDs to swap
	 * @param agents2
	 *            Agent IDs to swap with
	 * @param extensionLinks
	 * @param firstEventId
	 *            Event ID to start swapping from
	 * @param swapTop
	 *            If true then swap higher atomic events
	 * @return Event which is other-side neighbor for first event
	 * @throws StoryStorageException
	 */
	EventInterface swapAgents(List<Long> agents1, List<Long> agents2,
			Long firstEventId, boolean swapTop) throws StoryStorageException;

	/**
	 * reverse all swaps
	 * 
	 * @throws StoryStorageException
	 */
	void undoSwap() throws StoryStorageException;

	/**
	 * Remove events and update iterators Also removes unnecessary agents (with
	 * zero events)
	 * 
	 * @throws StoryStorageException
	 */
	void removeEventWithMarkDelete() throws StoryStorageException;

	/**
	 * Get count of all events
	 * 
	 * @return
	 */
	int eventCount();

	TreeMap<Long, AtomicEvent<?>> getAllEventsByNumber();

	StoriesGraphs extractGraph();
}
