package com.plectix.simulator.components.complex.subviews.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.CSubViewClass;
import com.plectix.simulator.components.complex.subviews.storage.CSubViewsLinkedlist;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;

public abstract class AbstractClassSubViewBuilder {
	protected Map<Integer, List<ISubViews>> subViewsMap;

	public AbstractClassSubViewBuilder() {
		this.subViewsMap = new HashMap<Integer, List<ISubViews>>();
	}

	protected void constructClassesSubViews(List<SubViewsRule> abstractRules,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {

		Map<Integer, Set<CSubViewClass>> agentTypeToClass = new HashMap<Integer, Set<CSubViewClass>>();
		for (Map.Entry<Integer, CAbstractAgent> entery : agentNameIdToAgent
				.entrySet()) {
			Integer agentType = entery.getKey();
			CAbstractAgent agent = entery.getValue();
			List<ISubViews> subViewsList = new LinkedList<ISubViews>();
			subViewsMap.put(agentType, subViewsList);
			Set<CSubViewClass> setClasses = new HashSet<CSubViewClass>();
			agentTypeToClass.put(agentType, setClasses);
			for (CAbstractSite site : agent.getSitesMap().values()) {
				setClasses.add(new CSubViewClass(agent.getNameId(), site
						.getNameId()));
			}
		}

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
				Set<CSubViewClass> setClasses = agentTypeToClass.get(agentType);
				CAbstractSite headSite = modificatedSites.get(0);
				CSubViewClass headClass = getFirstClass(setClasses, headSite
						.getNameId());
				headClass.addRuleId(aRule.getRuleId());
				for (CAbstractSite modSite : modificatedSites) {
					for (CAbstractSite testSite : testedSites) {
						headClass = getFirstClass(setClasses, headSite
								.getNameId());
						addSiteToClass(agentTypeToClass, testSite, headClass);
					}
					if (modSite == headSite)
						continue;

					int removedSiteId = modSite.getNameId();
					CSubViewClass removedClass = getSecondClass(setClasses,
							removedSiteId);
					boolean first = true;
					while (first
							|| getNeedClasses(setClasses, removedSiteId).size() != 1) {
						mergerSubViewsClasses(agentTypeToClass, removedClass,
								headClass);
						removedClass = getSecondClass(setClasses, removedSiteId);
						first = false;
					}
				}

			}
		}
		fillingSubViewsMap(agentTypeToClass);
		System.out.println();
	}

	private void fillingSubViewsMap(
			Map<Integer, Set<CSubViewClass>> agentTypeToClass) {
		for (Map.Entry<Integer, Set<CSubViewClass>> entrySets : agentTypeToClass
				.entrySet()) {
			Integer key = entrySets.getKey();
			Set<CSubViewClass> sets = entrySets.getValue();
			for (CSubViewClass subViewClass : sets) {
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
