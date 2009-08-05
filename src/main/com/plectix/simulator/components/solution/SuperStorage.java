package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.initialization.InjectionsBuilder;

/**
 * <p>This one is the SuperStorage. This kind of storage keeps substances
 * as connected components with counters (SuperSubstances). This type of storage
 * was intended specially for operation modes feature.</p>
 * @see SuperSubstance
 * @see OperationMode
 */
public final class SuperStorage implements IStorage {
	private final Map<String, SuperSubstance> myStorage = new LinkedHashMap<String, SuperSubstance>();
	private final ISolution mySolution;
	private int agentsLimit = Integer.MAX_VALUE;
	/**
	 * This one keeps current maximum length for the connected component stored here.
	 * We use it to optimize the work of tryIncrement() method.
	 */
	private int maxComponentLength = 0;
	
	SuperStorage(ISolution solution) {
		mySolution = solution;
	}
	
	/**
	 * This method searches for the super substance where this component could be
	 * added to. And if it finds it, then it increments substance's counter and returns <tt>true</tt>
	 * @param component component to be added
	 * @return <tt>true</tt> if there is super substance and we successfully incremented it's counter,
	 * otherwise <tt>false</tt>
	 */
	public final boolean tryIncrement(IConnectedComponent component) {
		if (component.getAgentsQuantity() <= maxComponentLength) {
			String hash = component.getHash();
			SuperSubstance previousEntry = myStorage.get(hash);
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
	
	private final void refreshMaxLength(IConnectedComponent component) {
		maxComponentLength = Math.max(maxComponentLength, component.getAgentsQuantity());
	}
	
	/**
	 * This method should be used only on the initialization stage, when adding new components
	 * to solution. It  
	 * @param quant
	 * @param component
	 */
	protected final void addOrEvenIncrement(long quant, IConnectedComponent component) {
		String hash = component.getHash();
		SuperSubstance substanceToAdd = new SuperSubstance(quant, component);
		SuperSubstance previousEntry = myStorage.get(hash);
		if (previousEntry == null) {
			myStorage.put(hash, substanceToAdd);
			refreshMaxLength(component);
		} else {
			previousEntry.add(substanceToAdd.getQuantity());
		}
	}
	
	@Override
	public final void addConnectedComponent(IConnectedComponent component) {
		if (!this.tryIncrement(component)) { 
			SuperSubstance s = new SuperSubstance(1, component);
			refreshMaxLength(component);
			myStorage.put(s.getHash(), s);
			component.deleteIncomingInjections();
			setInjectionsForSuperSubstance(s);
		}
	}
	
	@Override
	public final void clear() {
		myStorage.clear();
	}

	@Override
	public final Collection<IConnectedComponent> split() {
		List<IConnectedComponent> list = new ArrayList<IConnectedComponent>();
		for (SuperSubstance substance : myStorage.values()) {
			for (int i = 0; i < substance.getQuantity(); i++) {
				list.add(substance.getComponent());
			}
		}
		return list;
	}

	@Override
	// TODO add a chance that we can take the same component twice?
	public final IConnectedComponent extractComponent(CInjection inj) {
		SuperSubstance image = inj.getSuperSubstance();
		if (image != null && !image.isEmpty()) {
			IConnectedComponent component = this.extract(image);
			component.burnIncomingInjections();
			return component;
		} else {
			return null;
		}
	}

	private final IConnectedComponent extract(SuperSubstance image) {
		if (!image.isEmpty()) {
			IConnectedComponent component = image.extract();
			image.setComponent(mySolution.cloneConnectedComponent(component));
			// we don't want to choose an injection built to empty SS
			if (!image.isEmpty()) {
				setInjectionsForSuperSubstance(image);
			} else {
				// TODO do we really want to remove this component from collection?
				myStorage.remove(image.getHash());
				image.getComponent().deleteIncomingInjections();
			}
			return component;
		} else {
			// not reachable
			return null;
		}
	}

	private final void setInjectionsForSuperSubstance(SuperSubstance substance) {
		(new InjectionsBuilder(mySolution.getKappaSystem())).build(substance);
	}
	
	/**
	 * This method returns collection of SuperSubstances contained in this storage.
	 * Method specified for this type of storage only.
	 * @return collection of super substances
	 */
	public final Collection<SuperSubstance> getComponents() {
		return myStorage.values();
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

	@Override
	public final boolean isEmpty() {
		return myStorage.isEmpty();
	}
}
