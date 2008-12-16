package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.Simulator;

public final class SnapshotElement {
	private int count;
	private IConnectedComponent cc;
	private String ccName;

	public SnapshotElement(IConnectedComponent cc2, boolean isOcamlStyleObsName) {
		count = 1;
		this.cc = cc2;
		this.cc.initSpanningTreeMap();
		parseCC(isOcamlStyleObsName);
	}
	
	public final int getCount() {
		return count;
	}

	public final String getCcName() {
		return ccName;
	}

	private final void parseCC(boolean isOcamlStyleObsName) {
		ccName = SimulationUtils.printPartRule(cc, new int[] {0}, isOcamlStyleObsName);
	}

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

	public final void setConnectedComponent(IConnectedComponent cc) {
		this.cc = cc;
	}
}
