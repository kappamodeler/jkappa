package com.plectix.simulator.components.complex.subviews.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.CSubViewClass;
import com.plectix.simulator.components.complex.subviews.storage.CSubViewsLinkedlist;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.graphs.Edge;
import com.plectix.simulator.graphs.Graph;
import com.plectix.simulator.graphs.Vertex;

public abstract class AbstractClassSubViewBuilder {
	protected Map<Integer, List<ISubViews>> subViewsMap;

	public AbstractClassSubViewBuilder() {
		this.subViewsMap = new LinkedHashMap<Integer, List<ISubViews>>();
	}

	protected void constructClassesSubViews(List<AbstractionRule> abstractRules,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {

		// CSubViewClass with one site = Vertex
		Map<Integer, Graph> graphsByAgent = new LinkedHashMap<Integer, Graph>();
		Map<Integer, Map<Integer, CSubViewClass>> agentVertexBySite = new LinkedHashMap<Integer, Map<Integer, CSubViewClass>>();
		// create graph for each agent
		for (Map.Entry<Integer, CAbstractAgent> entery : agentNameIdToAgent
				.entrySet()) {
			Integer agentType = entery.getKey();
			CAbstractAgent agent = entery.getValue();
			Graph graphForAgent = new Graph();
			Map<Integer, CSubViewClass> vertexBySite = new LinkedHashMap<Integer, CSubViewClass>();

			for (CAbstractSite site : agent.getSitesMap().values()) {
				CSubViewClass primitiveView = new CSubViewClass(agentType);
				primitiveView.addSite(site.getNameId());
				graphForAgent.addVertex(primitiveView);
				vertexBySite.put(site.getNameId(), primitiveView);
			}
			graphsByAgent.put(agentType, graphForAgent);
			agentVertexBySite.put(agentType, vertexBySite);
			List<ISubViews> subViewsList = new LinkedList<ISubViews>();
			subViewsMap.put(agentType, subViewsList);
		}

		// draw edges in graphs
		for (AbstractionRule aRule : abstractRules) {
			List<AbstractAction> actions = aRule.getActions();
			for (AbstractAction action : actions) {
				List<CAbstractSite> modificatedSites = action
						.getModificatedSites();
				List<CAbstractSite> testedSites = action.getTestedSites();
				if (modificatedSites == null)
					continue;
				int agentType = action.getLeftHandSideAgent().getNameId();
				if (modificatedSites.isEmpty())
					continue;
				for (CAbstractSite modSite : modificatedSites) {
					for (CAbstractSite testedSite : testedSites) {
						graphsByAgent.get(agentType).addEdge(
								new Edge(agentVertexBySite.get(agentType).get(
										modSite.getNameId()), agentVertexBySite
										.get(agentType).get(
												testedSite.getNameId())));
					}
					for (CAbstractSite mod2Site : modificatedSites) {
						if (modSite == mod2Site)
							continue;
						graphsByAgent.get(agentType).addEdge(
								new Edge(agentVertexBySite.get(agentType).get(
										modSite.getNameId()), agentVertexBySite
										.get(agentType).get(
												mod2Site.getNameId())));

					}
					agentVertexBySite.get(agentType).get(modSite.getNameId())
							.addRuleId(aRule.getRuleId());
				}
			}
		}
		Map<Integer, ArrayList<CSubViewClass>> agentTypeToClass = new LinkedHashMap<Integer, ArrayList<CSubViewClass>>();

		// extract classesSubView and write correspondence subview- rule
		for (Integer agentType : agentNameIdToAgent.keySet()) {
			if (agentNameIdToAgent.get(agentType).getSitesMap().isEmpty()) {
				ArrayList<CSubViewClass> subViewsOfAgent = new ArrayList<CSubViewClass>();
				CSubViewClass classSubView = new CSubViewClass(agentType);
				subViewsOfAgent.add(classSubView);

				agentTypeToClass.put(agentType, subViewsOfAgent);
				continue;

			}
			ArrayList<CSubViewClass> subViewsOfAgent = new ArrayList<CSubViewClass>();
			// is getAll... doing once?
			for (ArrayList<Vertex> subClass : graphsByAgent.get(agentType)
					.getAllWeakClosureComponent()) {
				CSubViewClass classSubView = new CSubViewClass(agentType);

				for (Vertex v : subClass) {
					classSubView.addSite(((CSubViewClass) v).getSitesId()
							.iterator().next());
					classSubView.addRulesId(((CSubViewClass) v).getRulesId());
				}
				subViewsOfAgent.add(classSubView);
			}
			agentTypeToClass.put(agentType, subViewsOfAgent);

		}

		fillingSubViewsMap(agentTypeToClass);
	}

	// now filling by CSubViewsLinkedlist
	private void fillingSubViewsMap(
			Map<Integer, ArrayList<CSubViewClass>> agentTypeToClass) {
		for (Entry<Integer, ArrayList<CSubViewClass>> entrySets : agentTypeToClass
				.entrySet()) {
			Integer key = entrySets.getKey();
			ArrayList<CSubViewClass> sets = entrySets.getValue();
			for (CSubViewClass subViewClass : sets) {
				// build map action- subview too
				ISubViews subViews = new CSubViewsLinkedlist(subViewClass);
				subViewsMap.get(key).add(subViews);
			}
		}

	}

}
