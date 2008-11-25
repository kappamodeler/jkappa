package com.plectix.simulator.parser;

import java.util.*;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;

/*package*/ class SubstanceConstructor {
	private final NameDictionary myNameDictionary = SimulationMain
		.getSimulationManager().getNameDictionary();
	
	public ISite createSite(String name, String internalStateName, String linkIndex) {
		ISite site = new CSite(myNameDictionary.addName(name));
		if (internalStateName != null) { 
			site.setInternalState(new CInternalState(
					myNameDictionary.addName(internalStateName)));
		}
		if (linkIndex != null) {
			if ("?".equals(linkIndex)) {
				site.getLinkState().setStatusLink(CLinkState.STATUS_LINK_WILDCARD);
			} else {
				site.getLinkState().setStatusLink(CLinkState.STATUS_LINK_BOUND);
				if (!"_".equals(linkIndex)) {
					site.setLinkIndex(Integer.valueOf(linkIndex));
				}
			}
		}
		return site;
	}
	
	public IAgent createAgent(String name, List<ISite> sites) {
		IAgent agent = new CAgent(myNameDictionary.addName(name));
		for (ISite site : sites) {
			agent.addSite(site);
		}
		return agent;
	}
	
	private void bound(ISite site1, ISite site2) {
		site1.getLinkState().setSite(site2);
		site2.getLinkState().setSite(site1);
	}
	
	public IConnectedComponent createCC(List<IAgent> list) {
		IConnectedComponent cc = new CConnectedComponent(list);
		HashMap<Integer, ISite> map = new HashMap<Integer, ISite>();
		for (IAgent agent : cc.getAgents()) {
			for (ISite site : agent.getSites()) {
				int linkIndex = site.getLinkIndex();
				if (linkIndex != -1) {
					ISite boundedSite = map.get(linkIndex); 
					if (boundedSite != null) {
						bound(site, boundedSite);
						map.remove(linkIndex);
					} else {
						map.put(linkIndex, site);
					}
				}
			}
		}
		return cc;
	}
}
