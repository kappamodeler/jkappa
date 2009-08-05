package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;

/**
 * <p>This is a storage for the species.
 * It somehow contains information about solution's species.
 * In fact, solution consist of one or two storages.</p>
 * <p>We have two implementations which are SuperStorage and StraightSrorage.
 * StraightStorage keeps agents separately, instead of SuperStorage containing information about 
 * connected components only.</p>
 */
public interface IStorage {
	
	/**
	 * This method represents storage's contents as collection of connected components
	 * @return collection of connected components.
	 */
	public Collection<IConnectedComponent> split();

	/**
	 * This method takes an injection and finds connected component in storage
	 * that this injection points to.
	 * @param inj injection
	 * @return connected component which this injection points to
	 */
	public IConnectedComponent extractComponent(CInjection inj);
	
	/**
	 * Removes all species from the storage.
	 */
	public void clear();

	/**
	 * Adds connected component to the storage
	 * @param component component to be added
	 */
	public void addConnectedComponent(IConnectedComponent component);
	
	/**
	 * This method indicates whether this storage is empty
	 * @return <tt>true</tt> is so, otherwise <tt>false</tt>
	 */
	public boolean isEmpty();
}
