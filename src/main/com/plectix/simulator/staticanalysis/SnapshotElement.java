package com.plectix.simulator.staticanalysis;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.solution.SuperSubstance;

/**
 * This class implements snapshot element entity.
 * @author avokhmin
 *
 */
public final class SnapshotElement {
	private long count;
	private ConnectedComponentInterface connectedComponent;
	private final String ccName;

	/**
	 * Constructor. Creates snapshot-element with existing connected component
	 * @param component given ConnectedComponent
	 * @param isOcamlStyleObsName <tt>true</tt> if we use O'caml styled observables names, 
	 * otherwise <tt>false</tt>
	 */
	public SnapshotElement(ConnectedComponentInterface component, long count, boolean isOcamlStyleObsName) {
		this.count = count;
		connectedComponent = component;
		ccName = connectedComponent.getSmilesString();
	}
	
	/**
	 * This method returns counter of this snapshot element
	 * @return counter of this snapshot element
	 */
	public final long getCount() {
		return count;
	}

	/**
	 * This method returns name of current snapshot element
	 * @return name of current snapshot element
	 */
	public final String getComponentsName() {
		return ccName;
	}

	/**
	 * This method compares current connected component with the given one and
	 * increments counter for this snapshot element, if they are Automorphic's  
	 * @param component given connected component
	 * @return <tt>true</tt> if given connected component is automorphic to current,  
	 * otherwise <tt>false</tt>
	 */
	public final boolean exists(ConnectedComponentInterface component) {
		if (connectedComponent == component)
			return true;
		if(ccName.equals(component.getSmilesString())){
			SuperSubstance substance = component.getSubstance();
			if (substance != null) {
				count += substance.getQuantity();
			} else {
				count++;
			}
			return true;
		}

		return false;
	}

	/**
	 * This method sets connected component of this snapshot element.
	 * @param connectedComponent new values of connected component.
	 */
	public final void eraseConnectedComponent() {
		connectedComponent = null;
	}
}
