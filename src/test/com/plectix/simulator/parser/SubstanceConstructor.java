package com.plectix.simulator.parser;

import java.util.HashMap;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.NameDictionary;

public class SubstanceConstructor {
	private final NameDictionary nameDictionary = ThreadLocalData.getNameDictionary();
	private int myAgentIndexGenerator;
	
	public ISite createSite(String name, String internalStateName, String linkIndex) {
		ISite site = new CSite(nameDictionary.addName(name));
		if (internalStateName != null) { 
			site.setInternalState(new CInternalState(nameDictionary.addName(internalStateName)));
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
		IAgent agent = new CAgent(nameDictionary.addName(name), myAgentIndexGenerator++);
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
