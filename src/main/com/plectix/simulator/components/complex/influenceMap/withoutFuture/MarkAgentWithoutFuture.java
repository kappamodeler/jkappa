package com.plectix.simulator.components.complex.influenceMap.withoutFuture;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;

class MarkAgentWithoutFuture {
	private int agentNameId;
	private Map<Integer,List<MarkSiteWithoutFuture>> sitesMap;

	public MarkAgentWithoutFuture(CAbstractAgent agent) {
		CAbstractAgent agentNew = new CAbstractAgent(agent.getNameId());
		agentNameId = agentNew.getNameId();
		sitesMap = new HashMap<Integer, List<MarkSiteWithoutFuture>>();
	}

	public int getAgentNameId() {
		return agentNameId;
	}
	
	public void addMarkSite(MarkSiteWithoutFuture mSite){
		List<MarkSiteWithoutFuture> mSites = sitesMap.get(mSite.getSite().getNameId());
		if(mSites == null){
			mSites = new LinkedList<MarkSiteWithoutFuture>();
			sitesMap.put(mSite.getSite().getNameId(), mSites);
		}
		mSites.add(mSite);
	}
	
	public List<MarkSiteWithoutFuture> getMarkSites(int key){
		return sitesMap.get(key);
	}
}
