package com.plectix.simulator.components.complex.influenceMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;

class MarkAgent {
	private int agentNameId;
	private Map<Integer,List<MarkSite>> sitesMap;

	public MarkAgent(CAbstractAgent agent) {
		CAbstractAgent agentNew = new CAbstractAgent(agent.getNameId());
		agentNameId = agentNew.getNameId();
		sitesMap = new HashMap<Integer, List<MarkSite>>();
	}

	public int getAgentNameId() {
		return agentNameId;
	}
	
	public void addMarkSite(MarkSite mSite){
		List<MarkSite> mSites = sitesMap.get(mSite.getSite().getNameId());
		if(mSites == null){
			mSites = new LinkedList<MarkSite>();
			sitesMap.put(mSite.getSite().getNameId(), mSites);
		}
		mSites.add(mSite);
	}
	
	public List<MarkSite> getMarkSites(int key){
		return sitesMap.get(key);
	}
	
}
