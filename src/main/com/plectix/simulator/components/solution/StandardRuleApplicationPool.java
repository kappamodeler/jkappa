package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.action.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.SimulationUtils;

public class StandardRuleApplicationPool extends RuleApplicationPool {
	// We think that this collection is not so big
	private final List<IConnectedComponent> myInitialComponents = new ArrayList<IConnectedComponent>();
	private final StraightStorage myTempStorage = new StraightStorage();

	public StandardRuleApplicationPool(List<IInjection> injections) {
		for (IInjection inj : injections) {
			IConnectedComponent image = inj.getImage();
			if (image != null) {
				myTempStorage.addConnectedComponent(image);
				myInitialComponents.add(image);
			}
		}
	}

	public void addAgent(IAgent agent) {
		myTempStorage.addAgent(agent);
	}

	public void removeAgent(IAgent agent) {
		myTempStorage.removeAgent(agent);
	}

	public List<IConnectedComponent> getCurrentComponents() {
		return myTempStorage.split();
	}

	public List<IConnectedComponent> getInitialComponents() {
		return myInitialComponents;
	}
}
