package com.plectix.simulator.components.complex.subviews.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
		this.subViewsMap = new HashMap<Integer, List<ISubViews>>();
	}

	protected void constructClassesSubViews(List<SubViewsRule> abstractRules,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {

		// CSubViewClass with one site = Vertex
		Map<Integer, Graph> graphsByAgent = new HashMap<Integer, Graph>();
		Map<Integer, Map<Integer, CSubViewClass>> agentVertexBySite = new HashMap<Integer, Map<Integer, CSubViewClass>>();
		// create graph for each agent
		for (Map.Entry<Integer, CAbstractAgent> entery : agentNameIdToAgent
				.entrySet()) {
			Integer agentType = entery.getKey();
			CAbstractAgent agent = entery.getValue();
			Graph graphForAgent = new Graph();
			// int = site.id
			Map<Integer, CSubViewClass> vertexBySite = new HashMap<Integer, CSubViewClass>();

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
		for (SubViewsRule aRule : abstractRules) {
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
		Map<Integer, ArrayList<CSubViewClass>> agentTypeToClass = new HashMap<Integer, ArrayList<CSubViewClass>>();

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

		// Map<Integer, Set<CSubViewClass>> agentTypeToClass = new
		// HashMap<Integer, Set<CSubViewClass>>();
		// for (Map.Entry<Integer, CAbstractAgent> entery : agentNameIdToAgent
		// .entrySet()) {
		// Integer agentType = entery.getKey();
		// CAbstractAgent agent = entery.getValue();
		// List<ISubViews> subViewsList = new LinkedList<ISubViews>();
		// subViewsMap.put(agentType, subViewsList);
		// Set<CSubViewClass> setClasses = new HashSet<CSubViewClass>();
		// agentTypeToClass.put(agentType, setClasses);
		// for (CAbstractSite site : agent.getSitesMap().values()) {
		// setClasses.add(new CSubViewClass(agent.getNameId(), site
		// .getNameId()));
		// }
		// }
		//
		// for (SubViewsRule aRule : abstractRules) {
		// List<AbstractAction> actions = aRule.getActions();
		// for (AbstractAction action : actions) {
		// List<CAbstractSite> modificatedSites = action
		// .getModificatedSites();
		// List<CAbstractSite> testedSites = action.getTestedSites();
		//
		// if (modificatedSites == null)
		// continue;
		// int agentType = action.getLeftHandSideAgent().getNameId();
		// if (modificatedSites.isEmpty())
		// continue;
		// Set<CSubViewClass> setClasses = agentTypeToClass.get(agentType);
		// CAbstractSite headSite = modificatedSites.get(0);
		// CSubViewClass headClass = getFirstClass(setClasses, headSite
		// .getNameId());
		// headClass.addRuleId(aRule.getRuleId());
		// for (CAbstractSite modSite : modificatedSites) {
		// for (CAbstractSite testSite : testedSites) {
		// headClass = getFirstClass(setClasses, headSite
		// .getNameId());
		// addSiteToClass(agentTypeToClass, testSite, headClass);
		// }
		// if (modSite == headSite)
		// continue;
		//
		// int removedSiteId = modSite.getNameId();
		// CSubViewClass removedClass = getSecondClass(setClasses,
		// removedSiteId);
		// boolean first = true;
		// while (first
		// || getNeedClasses(setClasses, removedSiteId).size() != 1) {
		// mergerSubViewsClasses(agentTypeToClass, removedClass,
		// headClass);
		// removedClass = getSecondClass(setClasses, removedSiteId);
		// first = false;
		// }
		// }
		//
		// }
		// }
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

	private static CSubViewClass getFirstClass(Set<CSubViewClass> setClasses,
			int siteId) {
		for (CSubViewClass sbc : setClasses)
			if (sbc.isHaveSite(siteId))
				return sbc;
		return null;
	}

	private static CSubViewClass getSecondClass(Set<CSubViewClass> setClasses,
			int siteId) {
		boolean isEnd = false;
		CSubViewClass firstClass = null;
		for (CSubViewClass sbc : setClasses)
			if (sbc.isHaveSite(siteId))
				if (!isEnd) {
					isEnd = true;
					firstClass = sbc;
				} else
					return sbc;
		return firstClass;
	}

	private static List<CSubViewClass> getNeedClasses(
			Set<CSubViewClass> setClasses, int siteId) {
		List<CSubViewClass> outList = new LinkedList<CSubViewClass>();
		for (CSubViewClass sbc : setClasses)
			if (sbc.isHaveSite(siteId))
				outList.add(sbc);
		return outList;
	}

	private static void mergerSubViewsClasses(
			Map<Integer, Set<CSubViewClass>> agentTypeToClass,
			CSubViewClass removedClass, CSubViewClass fillingClass) {
		int agentId = fillingClass.getAgentTypeId();
		agentTypeToClass.get(agentId).remove(removedClass);
		agentTypeToClass.get(agentId).remove(fillingClass);

		for (Integer siteId : removedClass.getSitesId())
			fillingClass.addSite(siteId);
		fillingClass.addRulesId(removedClass);
		agentTypeToClass.get(agentId).add(fillingClass);
	}

	private static void addSiteToClass(
			Map<Integer, Set<CSubViewClass>> agentTypeToClass,
			CAbstractSite testSite, CSubViewClass fillingClass) {
		int agentId = fillingClass.getAgentTypeId();
		Set<CSubViewClass> set = agentTypeToClass.get(agentId);
		set.remove(fillingClass);
		fillingClass.addSite(testSite.getNameId());
		if (set.contains(fillingClass)) {
			for (CSubViewClass svc : set)
				if (svc.hashCode() == fillingClass.hashCode()) {
					svc.addRulesId(fillingClass);
					break;
				}
		} else
			agentTypeToClass.get(agentId).add(fillingClass);
	}

}
