package com.plectix.simulator.components.complex.subviews.base;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.CSubViewClass;
import com.plectix.simulator.components.complex.subviews.CSubViews;

public abstract class AbstractClassSubViewBuilder {
	protected Map<Integer, List<CSubViews>> subViewsMap;

	public AbstractClassSubViewBuilder() {
		this.subViewsMap = new HashMap<Integer, List<CSubViews>>();
	}

	protected void constructClassesSubViews(List<SubViewsRule> abstractRules,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {

		// TODO need work!!
		
		
//		Map<Integer, Map<Integer, List<CSubViewClass>>> agentTypeToSiteToClass = new HashMap<Integer, Map<Integer, List<CSubViewClass>>>();
//		for (Map.Entry<Integer, CAbstractAgent> entery : agentNameIdToAgent
//				.entrySet()) {
//			Integer agentType = entery.getKey();
//			CAbstractAgent agent = entery.getValue();
//			List<CSubViews> subViewsList = new LinkedList<CSubViews>();
//			subViewsMap.put(agentType, subViewsList);
//			Map<Integer, List<CSubViewClass>> siteToClass = new HashMap<Integer, List<CSubViewClass>>();
//			agentTypeToSiteToClass.put(agentType, siteToClass);
//			for (CAbstractSite site : agent.getSitesMap().values()) {
//				List<CSubViewClass> subViewClass = new LinkedList<CSubViewClass>();
//				subViewClass.add(new CSubViewClass(agent.getNameId(), site
//						.getNameId()));
//				siteToClass
//						.put(Integer.valueOf(site.getNameId()), subViewClass);
//			}
//		}
//
//		for (SubViewsRule aRule : abstractRules) {
//			List<AbstractAction> actions = aRule.getActions();
//			for (AbstractAction action : actions) {
//				List<CAbstractSite> modificatedSites = action
//						.getModificatedSites();
//				List<CAbstractSite> testedSites = action.getTestedSites();
//
//				if (modificatedSites != null || testedSites != null) {
//					int agentType = action.getLeftHandSideAgent().getNameId();
//					CAbstractSite headSite;
//					if (!modificatedSites.isEmpty())
//						headSite = modificatedSites.get(0);
//
//					for (CAbstractSite modSite : modificatedSites) {
//						List<CSubViewClass> modClasses = agentTypeToSiteToClass
//								.get(agentType).get(modSite.getNameId());
//						for (CAbstractSite testSite : testedSites) {
//							for (CSubViewClass modClass : modClasses) {
//								modClass.addSite(testSite.getNameId());
//								agentTypeToSiteToClass.get(agentType).get(
//										testSite.getNameId()).add(modClass);
//							}
//						}
//						
//						
//					}
//					
//
//				}
//
//			}
//		}

	}

}
