package com.plectix.simulator.parser;

import java.util.*;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.*;

/*package*/ class SubstanceConstructor {
	private final NameDictionary myNameDictionary = SimulationMain
		.getSimulationManager().getNameDictionary();
	
	public CSite createSite(String name, String internalStateName) {
		CSite site = new CSite(myNameDictionary.addName(name));
		if (internalStateName != null) { 
			site.setInternalState(new CInternalState(
					myNameDictionary.addName(internalStateName)));
		}
		return site;
	}
	
	public CAgent createAgent(String name, String...siteNames) {
		CAgent agent = new CAgent(myNameDictionary.addName(name));
		for (String siteName : siteNames) {
			agent.addSite(new CSite(myNameDictionary.addName(siteName)));
		}
		return agent;
	}
	
	public CAgent createAgent(String name, List<CSite> sites) {
		CAgent agent = new CAgent(myNameDictionary.addName(name));
		for (CSite site : sites) {
			agent.addSite(site);
		}
		return agent;
	}
	
	public CConnectedComponent createCC(List<CAgent> list) {
		return new CConnectedComponent(list);
	}
}
