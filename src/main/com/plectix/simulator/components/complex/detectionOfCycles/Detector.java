package com.plectix.simulator.components.complex.detectionOfCycles;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.graphs.Edge;
import com.plectix.simulator.graphs.Graph;

public class Detector {
	private IAllSubViewsOfAllAgents subViews = null;
	List<CAbstractAgent> listOfAgents = null;

	public Detector(IAllSubViewsOfAllAgents subViews,
			List<CAbstractAgent> listOfAgents) {
		this.subViews = subViews;
		this.listOfAgents = listOfAgents;

	}

	public List<EdgeFromContactMap> extractCycles() {
		Graph g = new Graph();

		LinkedHashMap<Integer, TreeMap<Integer, NodeFromContactMap>> mapGraph = new LinkedHashMap<Integer, TreeMap<Integer, NodeFromContactMap>>();
		for (CAbstractAgent agent : listOfAgents) {
			TreeMap<Integer, NodeFromContactMap> mapAgent = new TreeMap<Integer, NodeFromContactMap>();
			for (int siteId : agent.getSitesMap().keySet()) {
				NodeFromContactMap node = new NodeFromContactMap(agent, agent
						.getSite(siteId));
				g.addVertex(node);
				mapAgent.put(siteId, node);
			}
			mapGraph.put(agent.getNameId(), mapAgent);
		}

		Integer i;
		NodeFromContactMap nodeFrom;
		NodeFromContactMap nodeTo;
		int siteLink;
		int agentLink;
		Iterator<Integer> iterator = subViews.getAllTypesIdOfAgents();
		while (iterator.hasNext()) {
			i = iterator.next();
			for (ISubViews subView : subViews.getAllSubViewsByTypeId(i)) {
				for (CAbstractAgent view : subView.getAllSubViews()) {
					for (int siteId : view.getSitesMap().keySet()) {
						if (view.getSite(siteId).getLinkState()
								.getStatusLinkRank() == CLinkRank.BOUND) {
							nodeFrom = mapGraph.get(view.getNameId()).get(
									siteId);
							siteLink = view.getSite(siteId).getLinkState()
									.getLinkSiteNameID();
							agentLink = view.getSite(siteId).getLinkState()
									.getAgentNameID();
							for (int site : mapGraph.get(agentLink).keySet()) {
								if(site ==siteLink){
									continue;
								}
								nodeTo = mapGraph.get(agentLink).get(site);
								EdgeFromContactMap edge = new EdgeFromContactMap(
										nodeFrom, nodeTo);
								g.addEdge(edge);
							}
						}
					}
				}
			}
		}
		LinkedList<EdgeFromContactMap> answer = new LinkedList<EdgeFromContactMap>();
		for (Edge e : g.getAllEdgesInDirectedCycles()) {
			answer.add((EdgeFromContactMap) e);
		}
		return answer;
	}

}
