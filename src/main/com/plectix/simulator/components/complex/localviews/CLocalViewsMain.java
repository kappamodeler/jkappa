package com.plectix.simulator.components.complex.localviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.subviews.CSubViewClass;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;

public class CLocalViewsMain {
	private IAllSubViewsOfAllAgents subviews;
	private Map<Integer, HashSet<CAbstractAgent>> localViews;

	public CLocalViewsMain(IAllSubViewsOfAllAgents subViews) {
		this.subviews = subViews;
		localViews = new HashMap<Integer, HashSet<CAbstractAgent>>();
	}

	public void initLocalViews() {
		Map<Integer, CAbstractAgent> agentsMap = subviews.getFullMapOfAgents();
		for (Integer agentId : agentsMap.keySet()){
			HashSet<CAbstractAgent> setOfLocalViews = new HashSet<CAbstractAgent>();
			
			HashSet<CAbstractAgent> temp = new HashSet<CAbstractAgent>();
			
			for(ISubViews subView : subviews.getAllSubViewsByTypeId(agentId)){
				if(setOfLocalViews.isEmpty()){
					temp.addAll(subView.getAllSubViewsCoherent(null));
				}
				else{
					for(CAbstractAgent agent : setOfLocalViews){
						temp.addAll(subView.getAllSubViewsCoherent(agent));
					}
				}
				
				setOfLocalViews = temp;
			}
		
			localViews.put(agentId, setOfLocalViews);
		
		}
		
	}
	
	public Map<Integer, HashSet<String>> extract(){
		return null;
		
	}
}
