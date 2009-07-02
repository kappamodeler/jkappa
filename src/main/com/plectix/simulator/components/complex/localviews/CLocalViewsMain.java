package com.plectix.simulator.components.complex.localviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;

public class CLocalViewsMain {
	private IAllSubViewsOfAllAgents subviews;
	private Map<Integer, HashSet<String>> localViews;

	public CLocalViewsMain(IAllSubViewsOfAllAgents subViews) {
		this.subviews = subViews;
		localViews = new HashMap<Integer, HashSet<String>>();
	}

	public void initLocalViews() {
		Map<Integer, CAbstractAgent> agentsMap = subviews.getFullMapOfAgents();
		List<List<CAbstractAgent>> agentMap = new ArrayList<List<CAbstractAgent>>();
		for (Integer agentId : agentsMap.keySet())
			localViews.put(agentId, new HashSet<String>());

		
	}
}
