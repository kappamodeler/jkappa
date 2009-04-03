package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.Simulator;

/**
 * Class implements snapshot element.
 * @author avokhmin
 *
 */
public final class SnapshotElement {
	private int count;
	private IConnectedComponent cc;
	private String ccName;

	/**
	 * Default constructor.
	 * @param cc2 given ConnectedComponent
	 * @param isOcamlStyleObsName type creates name's of new SnapshotElement.
	 */
	public SnapshotElement(IConnectedComponent cc2, boolean isOcamlStyleObsName) {
		count = 1;
		this.cc = cc2;
		this.cc.initSpanningTreeMap();
		parseCC(isOcamlStyleObsName);
	}
	
	/**
	 * This method returns count SnapshotElement. 
	 */
	public final int getCount() {
		return count;
	}

	/**
	 * This method returns name of current SnapshotElement. 
	 */
	public final String getCcName() {
		return ccName;
	}

	/**
	 * This method creates name of current SnapshotElement.
	 * @param isOcamlStyleObsName type creates name's of new SnapshotElement.
	 */
	private final void parseCC(boolean isOcamlStyleObsName) {
		ccName = SimulationUtils.printPartRule(cc, new int[] {0}, isOcamlStyleObsName);
	}

	/**
	 * This method compare current connected component with given and Up "count", if they are Automorphic's  
	 * @param ccEx given connected component
	 * @return <tt>true</tt> if given connected component Automorphic's current,  otherwise <tt>false</tt>
	 */
	public final boolean exists(IConnectedComponent ccEx) {
		if (cc == ccEx)
			return true;
		ccEx.initSpanningTreeMap();
		//if (cc.isAutomorphism(ccEx.getAgents().get(0))) {
		if (cc.unify(ccEx.getAgents().get(0)) && ccEx.unify(cc.getAgents().get(0))) {
			count++;
			return true;
		}

		return false;
	}

	/**
	 * This method sets given connected component to current SnapshotElement.
	 * @param cc given connected component.
	 */
	public final void setConnectedComponent(IConnectedComponent cc) {
		this.cc = cc;
	}
}
