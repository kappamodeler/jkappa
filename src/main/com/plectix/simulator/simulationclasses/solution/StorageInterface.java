package com.plectix.simulator.simulationclasses.solution;

import java.util.Collection;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;

/**
 * <p>This is a storage for the species.
 * It somehow contains information about solution's species.
 * In fact, solution consist of one or two storages.</p>
 * <p>We have two implementations which are SuperStorage and StraightSrorage.
 * StraightStorage keeps agents separately, instead of SuperStorage containing information about 
 * connected components only.</p>
 */
public interface StorageInterface {
	
	/**
	 * This method represents storage's contents as collection of connected components
	 * @return collection of connected components.
	 */
	public Collection<ConnectedComponentInterface> split();

	/**
	 * This method takes an injection and finds connected component in storage
	 * that this injection points to.
	 * @param injection injection
	 * @return connected component which this injection points to
	 */
	public ConnectedComponentInterface extractComponent(Injection injection);
	
	/**
	 * Removes all species from the storage.
	 */
	public void clear();

	/**
	 * Adds connected component to the storage
	 * @param component component to be added
	 */
	public void addConnectedComponent(ConnectedComponentInterface component);
}
