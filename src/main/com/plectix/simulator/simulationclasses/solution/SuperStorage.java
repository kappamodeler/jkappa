package com.plectix.simulator.simulationclasses.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulator.initialization.InjectionsBuilder;

/**
 * <p>This one is the SuperStorage. This kind of storage keeps substances
 * as connected components with counters (SuperSubstances). This type of storage
 * was intended specially for operation modes feature.</p>
 * @see SuperSubstance
 * @see OperationMode
 */
public final class SuperStorage implements StorageInterface {
	private final Map<String, SuperSubstance> data = new LinkedHashMap<String, SuperSubstance>();
	private final SolutionInterface solution;
	private int agentsLimit = Integer.MAX_VALUE;
	/**
	 * This one keeps current maximum length for the connected component stored here.
	 * We use it to optimize the work of tryIncrement() method.
	 */
	private int maxComponentLength = 0;
	
	SuperStorage(SolutionInterface solution) {
		this.solution = solution;
	}
	
	/**
	 * This method searches for the super substance where this component could be
	 * added to. And if it finds it, then it increments substance's counter and returns <tt>true</tt>
	 * @param component component to be added
	 * @return <tt>true</tt> if there is super substance and we successfully incremented it's counter,
	 * otherwise <tt>false</tt>
	 */
	public final boolean tryIncrement(ConnectedComponentInterface component) {
		if (component.getAgents().size() <= maxComponentLength) {
			String hash = component.getSmilesString();
			SuperSubstance previousEntry = data.get(hash);
			if (previousEntry != null) {
				previousEntry.add();
				// there could be some injections here even after the negative update!
				component.deleteIncomingInjections();
				previousEntry.getComponent().incrementIncomingInjections();
				return true;
			}
		}
		return false;
	}
	
	private final void refreshMaxLength(ConnectedComponentInterface component) {
		maxComponentLength = Math.max(maxComponentLength, component.getAgents().size());
	}
	
	/**
	 * This method should be used only on the initialization stage, when adding new components
	 * to solution. It  
	 * @param quant
	 * @param component
	 */
	final void addOrEvenIncrement(long quant, ConnectedComponentInterface component) {
		String hash = component.getSmilesString();
		SuperSubstance substanceToAdd = new SuperSubstance(quant, component);
		SuperSubstance previousEntry = data.get(hash);
		if (previousEntry == null) {
			data.put(hash, substanceToAdd);
			refreshMaxLength(component);
		} else {
			previousEntry.add(substanceToAdd.getQuantity());
		}
	}
	
	@Override
	public final void addConnectedComponent(ConnectedComponentInterface component) {
		if (!this.tryIncrement(component)) { 
			SuperSubstance s = new SuperSubstance(1, component);
			refreshMaxLength(component);
			data.put(s.getHash(), s);
			component.deleteIncomingInjections();
			setInjectionsForSuperSubstance(s);
		}
	}
	
	@Override
	public final void clear() {
		data.clear();
	}

	@Override
	public final Collection<ConnectedComponentInterface> split() {
		List<ConnectedComponentInterface> list = new ArrayList<ConnectedComponentInterface>();
		for (SuperSubstance substance : data.values()) {
			for (int i = 0; i < substance.getQuantity(); i++) {
				list.add(substance.getComponent());
			}
		}
		return list;
	}

	@Override
	// TODO add a chance that we can take the same component twice?
	public final ConnectedComponentInterface extractComponent(Injection injection) {
		SuperSubstance image = injection.getSuperSubstance();
		if (image != null && !image.isEmpty()) {
			ConnectedComponentInterface component = this.extract(image);
			component.burnIncomingInjections();
			return component;
		} else {
			return null;
		}
	}

	private final ConnectedComponentInterface extract(SuperSubstance image) {
		if (!image.isEmpty()) {
			ConnectedComponentInterface component = image.extract();
			image.setComponent(solution.cloneConnectedComponent(component));
			// we don't want to choose an injection built to empty SS
			if (!image.isEmpty()) {
				setInjectionsForSuperSubstance(image);
			} else {
				// TODO do we really want to remove this component from collection?
				data.remove(image.getHash());
				image.getComponent().deleteIncomingInjections();
			}
			return component;
		} else {
			// not reachable
			return null;
		}
	}

	private final void setInjectionsForSuperSubstance(SuperSubstance substance) {
		(new InjectionsBuilder(solution.getKappaSystem())).build(substance);
	}
	
	/**
	 * This method returns collection of SuperSubstances contained in this storage.
	 * Method specified for this type of storage only.
	 * @return collection of super substances
	 */
	public final Collection<SuperSubstance> getComponents() {
		return data.values();
	}
	
	/**
	 * This method retrieves the limit for the length of connected component allowed 
	 * to be stored in super substance. Default value is Integer.MAX_VALUE
	 * @return limit for the agents' quantity in connected component
	 */
	public final int getAgentsLimit() {
		return agentsLimit;
	}
	
	/**
	 * This method sets the limit for the length of connected component allowed 
	 * to be stored in super substance
	 * @param limit limit for the agents' quantity in connected component
	 */
	public final void setAgentsLimit(int limit) {
		agentsLimit = limit;
	}
}
