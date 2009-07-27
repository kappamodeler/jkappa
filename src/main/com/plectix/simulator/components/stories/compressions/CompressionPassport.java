package com.plectix.simulator.components.stories.compressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.components.stories.storage.graphs.StoriesGraphs;

public interface CompressionPassport {
	
	/**
	 * Initialize data for strong compression
	 */
	void prepareForStrong();
	
	IWireStorage getStorage();
	
	/**
	 * Event iterator
	 * @param reverse : true - goUpwards from bottom, against the current of real time
	 * @return
	 * @throws StoryStorageException 
	 */
	IEventIterator eventIterator (boolean reverse) throws StoryStorageException;

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
	 * Get all wires of given agent
	 * @param agentId
	 * @return
	 */
	ArrayList<WireHashKey> getAgentWires (long agentId);
	
	/**
	 * Get type of an agent
	 * @param agentId
	 * @return 
	 */
	int getAgentType (long agentId);
	
	/**
	 * Check if swapping of two agents is possible (they have the same wires)
	 * @param agentId1
	 * @param agentId2
	 * @return
	 */
	boolean isAbleToSwap (long agentId1, long agentId2);
	
	/**
	 * Make storage with swapped agents 
	 * @param agents1 Agent IDs to swap
	 * @param agents2 Agent IDs to swap with
	 * @param firstEventId Event ID to start swapping from
	 * @param swapTop If true then swap higher atomic events
	 * @return Event which is other-side neighbor for first event
	 * @throws StoryStorageException 
	 */
	ICEvent swapAgents(List<Long> agents1, List<Long> agents2, Long firstEventId, boolean swapTop) throws StoryStorageException;
	
	/**
	 * reverse all swaps
	 * @throws StoryStorageException 
	 */
	void undoSwap () throws StoryStorageException;
	
	/**
	 * Remove events and update iterators
	 * Also removes unnecessary agents (with zero events) 
	 * @throws StoryStorageException 
	 */
	void removeEventWithMarkDelete() throws StoryStorageException;

	boolean isFirstEvent (long eventId);
	
	TreeMap<Long, AtomicEvent<?>> getAllEventsByNumber();

	Map<Long, ArrayList<WireHashKey>> getWiresByIdAgent();

	StoriesGraphs extractGraph();

}
