package com.plectix.simulator.staticanalysis.subviews.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;
import com.plectix.simulator.staticanalysis.graphs.Edge;
import com.plectix.simulator.staticanalysis.graphs.Graph;
import com.plectix.simulator.staticanalysis.graphs.Vertex;
import com.plectix.simulator.staticanalysis.subviews.SubViewClass;
import com.plectix.simulator.staticanalysis.subviews.storage.SubViewsInterface;
import com.plectix.simulator.staticanalysis.subviews.storage.SubViewsLinkedlist;

public abstract class AbstractClassSubViewBuilder {
	private final Map<String, List<SubViewsInterface>> subViewsMap
			= new LinkedHashMap<String, List<SubViewsInterface>>();

	public AbstractClassSubViewBuilder() {
	}

	protected final Map<String, List<SubViewsInterface>> getSubViews() {
		return subViewsMap;
	}
	
	protected final void constructClassesSubViews(
			List<AbstractionRule> abstractRules,
			Map<String, AbstractAgent> agentNameToAgent) {

		// CSubViewClass with one site = Vertex
		Map<String, Graph> graphsByAgent = new LinkedHashMap<String, Graph>();
		Map<String, Map<String, SubViewClass>> agentVertexBySite 	
				= new LinkedHashMap<String, Map<String, SubViewClass>>();
		// create graph for each agent
		for (Map.Entry<String, AbstractAgent> entery : agentNameToAgent
				.entrySet()) {
			String agentType = entery.getKey();
			AbstractAgent agent = entery.getValue();
			Graph graphForAgent = new Graph();
			Map<String, SubViewClass> vertexBySite = new LinkedHashMap<String, SubViewClass>();

			for (AbstractSite site : agent.getSitesMap().values()) {
				SubViewClass primitiveView = new SubViewClass(agentType);
				primitiveView.addSite(site.getName());
				graphForAgent.addVertex(primitiveView);
				vertexBySite.put(site.getName(), primitiveView);
			}
			graphsByAgent.put(agentType, graphForAgent);
			agentVertexBySite.put(agentType, vertexBySite);
			List<SubViewsInterface> subViewsList = new LinkedList<SubViewsInterface>();
			subViewsMap.put(agentType, subViewsList);
		}

		// draw edges in graphs
		for (AbstractionRule aRule : abstractRules) {
			List<AbstractAction> actions = aRule.getActions();
			for (AbstractAction action : actions) {
				List<AbstractSite> modificatedSites = action
						.getModificatedSites();
				List<AbstractSite> testedSites = action.getTestedSites();
				if (modificatedSites == null)
					continue;
				String agentType = action.getLeftHandSideAgent().getName();
				if (modificatedSites.isEmpty())
					continue;
				for (AbstractSite modSite : modificatedSites) {
					for (AbstractSite testedSite : testedSites) {
						graphsByAgent.get(agentType).addEdge(
								new Edge(agentVertexBySite.get(agentType).get(
										modSite.getName()), agentVertexBySite
										.get(agentType).get(
												testedSite.getName())));
					}
					for (AbstractSite mod2Site : modificatedSites) {
						if (modSite == mod2Site)
							continue;
						graphsByAgent.get(agentType).addEdge(
								new Edge(agentVertexBySite.get(agentType).get(
										modSite.getName()), agentVertexBySite
										.get(agentType).get(
												mod2Site.getName())));

					}
					agentVertexBySite.get(agentType).get(modSite.getName())
							.addRuleId(aRule.getRuleId());
				}
			}
		}
		Map<String, ArrayList<SubViewClass>> agentTypeToClass = new LinkedHashMap<String, ArrayList<SubViewClass>>();

		// extract classesSubView and write correspondence subview- rule
		for (String agentType : agentNameToAgent.keySet()) {
			if (agentNameToAgent.get(agentType).getSitesMap().isEmpty()) {
				ArrayList<SubViewClass> subViewsOfAgent = new ArrayList<SubViewClass>();
				SubViewClass classSubView = new SubViewClass(agentType);
				subViewsOfAgent.add(classSubView);

				agentTypeToClass.put(agentType, subViewsOfAgent);
				continue;

			}
			ArrayList<SubViewClass> subViewsOfAgent = new ArrayList<SubViewClass>();
			// is getAll... doing once?
			for (ArrayList<Vertex> subClass : graphsByAgent.get(agentType)
					.getAllWeakClosureComponent()) {
				SubViewClass classSubView = new SubViewClass(agentType);

				for (Vertex v : subClass) {
					classSubView.addSite(((SubViewClass) v).getSitesNames()
							.iterator().next());
					classSubView.addRulesId(((SubViewClass) v).getRulesId());
				}
				subViewsOfAgent.add(classSubView);
			}
			agentTypeToClass.put(agentType, subViewsOfAgent);

		}

		fillingSubViewsMap(agentTypeToClass);
	}

	// now filling by CSubViewsLinkedlist
	private final void fillingSubViewsMap(
			Map<String, ArrayList<SubViewClass>> agentTypeToClass) {
		for (Entry<String, ArrayList<SubViewClass>> entrySets : agentTypeToClass
				.entrySet()) {
			String key = entrySets.getKey();
			ArrayList<SubViewClass> sets = entrySets.getValue();
			for (SubViewClass subViewClass : sets) {
				// build map action- subview too
				SubViewsInterface subViews = new SubViewsLinkedlist(
						subViewClass);
				subViewsMap.get(key).add(subViews);
			}
		}

	}
	
	public boolean isEmpty(){
		for(List<SubViewsInterface> list : subViewsMap.values()){
			for(SubViewsInterface subView : list){
				if(!subView.isEmpty()){
					return false;
				}
			}
		}
		return true;
		
	}

}
