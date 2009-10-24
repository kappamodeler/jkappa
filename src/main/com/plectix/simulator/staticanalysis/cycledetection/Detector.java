package com.plectix.simulator.staticanalysis.cycledetection;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import com.plectix.simulator.staticanalysis.LinkRank;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.graphs.Edge;
import com.plectix.simulator.staticanalysis.graphs.Graph;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.staticanalysis.subviews.storage.SubViewsInterface;

public final class Detector {
	private final AllSubViewsOfAllAgentsInterface subViews;
	private final List<AbstractAgent> listOfAgents;

	public Detector(AllSubViewsOfAllAgentsInterface subViews, List<AbstractAgent> listOfAgents) {
		this.subViews = subViews;
		this.listOfAgents = listOfAgents;
	}

	public final List<EdgeFromContactMap> extractCycles() {
		Graph graph = new Graph();
		LinkedHashMap<String, TreeMap<String, NodeFromContactMap>> mapGraph 
			= new LinkedHashMap<String, TreeMap<String, NodeFromContactMap>>();
		for (AbstractAgent agent : listOfAgents) {
			TreeMap<String, NodeFromContactMap> mapAgent = new TreeMap<String, NodeFromContactMap>();
			for (String siteName : agent.getSitesMap().keySet()) {
				NodeFromContactMap node = new NodeFromContactMap(agent, agent
						.getSiteByName(siteName));
				graph.addVertex(node);
				mapAgent.put(siteName, node);
			}
			mapGraph.put(agent.getName(), mapAgent);
		}

		String currentAgentName;
		NodeFromContactMap sourceNode;
		NodeFromContactMap targetNode;
		String linkSiteName;
		String linkAgentName;
		Iterator<String> iterator = subViews.getAllTypesIdOfAgents();
		while (iterator.hasNext()) {
			currentAgentName = iterator.next();
			for (SubViewsInterface subView : subViews.getAllSubViewsByType(currentAgentName)) {
				for (AbstractAgent view : subView.getAllSubViews()) {
					for (String siteName : view.getSitesMap().keySet()) {
						if (view.getSiteByName(siteName).getLinkState()
								.getStatusLinkRank() == LinkRank.BOUND) {
							sourceNode = mapGraph.get(view.getName()).get(
									siteName);
							linkSiteName = view.getSiteByName(siteName).getLinkState()
									.getConnectedSiteName();
							linkAgentName = view.getSiteByName(siteName).getLinkState()
									.getAgentName();
							for (String site : mapGraph.get(linkAgentName).keySet()) {
								if (site.equals(linkSiteName)){
									continue;
								}
								targetNode = mapGraph.get(linkAgentName).get(site);
								EdgeFromContactMap edge = new EdgeFromContactMap(
										sourceNode, targetNode);
								graph.addEdge(edge);
							}
						}
					}
				}
			}
		}
		
		// TODO cannot we just return graph.getAllEdgesInDirectedCycles()?
		LinkedList<EdgeFromContactMap> answer = new LinkedList<EdgeFromContactMap>();
		for (Edge edge : graph.getAllEdgesInDirectedCycles()) {
			answer.add((EdgeFromContactMap) edge);
		}
		return answer;
	}
}
