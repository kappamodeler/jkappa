package com.plectix.simulator.parser;

import java.util.*;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.*;

/*package*/ class SubstanceConstructor {
	private final NameDictionary myNameDictionary = SimulationMain
		.getSimulationManager().getNameDictionary();
	
	public CSite createSite(String name, String internalStateName, String linkIndex) {
		CSite site = new CSite(myNameDictionary.addName(name));
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
	
	public CAgent createAgent(String name, List<CSite> sites) {
		CAgent agent = new CAgent(myNameDictionary.addName(name));
		for (CSite site : sites) {
			agent.addSite(site);
		}
		return agent;
	}
	
	private void bound(CSite site1, CSite site2) {
		site1.getLinkState().setSite(site2);
		site2.getLinkState().setSite(site1);
	}
	
	public CConnectedComponent createCC(List<CAgent> list) {
		CConnectedComponent cc = new CConnectedComponent(list);
		HashMap<Integer, CSite> map = new HashMap<Integer, CSite>();
		for (CAgent agent : cc.getAgents()) {
			for (CSite site : agent.getSites()) {
				int linkIndex = site.getLinkIndex();
				if (linkIndex != -1) {
					CSite boundedSite = map.get(linkIndex); 
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
