package com.plectix.simulator.parser;

import java.util.LinkedHashMap;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.NameDictionary;

public class SubstanceConstructor {
	private final NameDictionary nameDictionary = ThreadLocalData.getNameDictionary();
	private int myAgentIndexGenerator;
	
	public CSite createSite(String name, String internalStateName, String linkIndex) {
		CSite site = new CSite(nameDictionary.addName(name));
		if (internalStateName != null) { 
			site.setInternalState(new CInternalState(nameDictionary.addName(internalStateName)));
		}
		if (linkIndex != null) {
			if ("?".equals(linkIndex)) {
				site.getLinkState().setStatusLink(CLinkStatus.WILDCARD);
			} else {
				site.getLinkState().setStatusLink(CLinkStatus.BOUND);
				if (!"_".equals(linkIndex)) {
					site.setLinkIndex(Integer.valueOf(linkIndex));
				}
			}
		}
		return site;
	}
	
	public CAgent createAgent(String name, List<CSite> sites) {
		CAgent agent = new CAgent(nameDictionary.addName(name), myAgentIndexGenerator++);
		for (CSite site : sites) {
			agent.addSite(site);
		}
		return agent;
	}
	
	private void bound(CSite site1, CSite site2) {
		site1.getLinkState().connectSite(site2);
		site2.getLinkState().connectSite(site1);
	}
	
	public IConnectedComponent createCC(List<CAgent> list) {
		IConnectedComponent cc = new CConnectedComponent(list);
		LinkedHashMap<Integer, CSite> map = new LinkedHashMap<Integer, CSite>();
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
