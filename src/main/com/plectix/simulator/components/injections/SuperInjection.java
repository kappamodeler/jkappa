package com.plectix.simulator.components.injections;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IAgentLink;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISite;

public class SuperInjection extends CInjection {

	private final SuperSubstance mySubstance;
	
	public SuperInjection(CConnectedComponent connectedComponent,
			List<ISite> sitesList, List<IAgentLink> agentLinkList, SuperSubstance substance) {
		super(connectedComponent, sitesList, agentLinkList);
		mySubstance = substance;
	}
	
	public boolean isSuper() {
		return true;
	}
	
	public SuperSubstance getSubstance() {
		return mySubstance;
	}
}
