package com.plectix.simulator.components;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IConnectedComponent;

public final class SnapshotElement {
	private int count;
	private IConnectedComponent cc;
	private String ccName;

	public SnapshotElement(IConnectedComponent cc2) {
		count = 1;
		this.cc = cc2;
		this.cc.initSpanningTreeMap();
		parseCC();
	}
	
	public final int getCount() {
		return count;
	}

	public final String getCcName() {
		return ccName;
	}

	private final void parseCC() {
		ccName = SimulationMain.getSimulationManager().printPartRule(cc,0);
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
