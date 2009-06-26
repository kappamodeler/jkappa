package com.plectix.simulator.components.complex.subviews.storage;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.CSubViewClass;
import com.plectix.simulator.components.complex.subviews.base.AbstractAction;

public class CSubViewsLinkedlist implements ISubViews {
	private CSubViewClass subViewClass;
	private List<CAbstractAgent> storage;

	public CSubViewsLinkedlist(CSubViewClass subViewClass) {
		this.subViewClass = subViewClass;
		this.storage = new LinkedList<CAbstractAgent>();
		
	}

	public void fillingInitialState(
			Map<Integer, CAbstractAgent> agentNameIdToAgent,
			Collection<CAgent> agents) {
		CAbstractAgent defAgent = agentNameIdToAgent.get(subViewClass
				.getAgentTypeId());
		CAbstractAgent needAgent = new CAbstractAgent(defAgent);
		for (CAbstractSite defSite : defAgent.getSitesMap().values()) {
			int siteId = defSite.getNameId();
			if (!subViewClass.isHaveSite(siteId))
				needAgent.getSitesMap().remove(siteId);
		}
		// storage.add(needAgent);
		storage.addAll(convertAgentsToAbstract(getNeedAgentsBySolution(agents),
				needAgent));
	}

	public boolean addAbstractAgent(CAbstractAgent agent)
			throws SubViewsExeption {
		if (test(agent))
			return false;
		storage.add(agent);
		return true;
	}

	public boolean burnRule(AbstractAction action) throws SubViewsExeption {
		return burnRule(action.getLeftHandSideAgent(), action.getRightHandSideAgent());
//		CAbstractAgent agent = action.getLeftHandSideAgent();
//		if (agent == null) {
//			agent = action.getRightHandSideAgent().clone();
//			return addAbstractAgent(agent);
//		}
//		if (action.getRightHandSideAgent() == null) {
//			// TODO DELETE Action!!!
//		}
//		boolean isAdd = false;
//		List<CAbstractAgent> agentsList = getAllSubViews(agent);
//		for (CAbstractAgent agentFromStorage : agentsList) {
//			CAbstractAgent newAgent = agentFromStorage.clone();
//			newAgent.addAllStates(action.getRightHandSideAgent());
//			if (addAbstractAgent(newAgent))
//				isAdd = true;
//		}
//		return isAdd;
	}

	public boolean burnRule(CAbstractAgent oldViews, CAbstractAgent newViews)
			throws SubViewsExeption {
		if (oldViews == null)
			return addAbstractAgent(newViews);

		if (newViews == null) {
			// TODO DELETE Action!!!
		}
		boolean isAdd = false;
		List<CAbstractAgent> agentsList = getAllSubViews(oldViews);
		for (CAbstractAgent agentFromStorage : agentsList) {
			CAbstractAgent newAgent = agentFromStorage.clone();
			newAgent.addAllStates(newViews);
			if (addAbstractAgent(newAgent))
				isAdd = true;
		}
		return isAdd;
	}

	public boolean test(AbstractAction action) throws SubViewsExeption {
		CAbstractAgent agent = action.getLeftHandSideAgent();
		if (agent == null)
			return true;
		return test(agent);
	}

	public boolean test(CAbstractAgent testView) throws SubViewsExeption {
		if (testView.getNameId() != subViewClass.getAgentTypeId())
			throw new SubViewsExeption(subViewClass, testView);
		for (CAbstractSite site : testView.getSitesMap().values())
			if (!subViewClass.isHaveSite(site.getNameId()))
				throw new SubViewsExeption(subViewClass, testView);

		for (CAbstractAgent aAgent : storage) {
			boolean isHave = true;
			for (CAbstractSite site : testView.getSitesMap().values()) {
//				int siteId = site.getNameId();
				// if (!aAgent.getSite(siteId).isFit(site)) {
				if (!site.isFit(aAgent.getSite(site.getNameId()))) {
					isHave = false;
					break;
				}
			}
			if (isHave)
				return true;
		}
		return false;
	}

	public List<CAbstractAgent> getAllSubViews(CAbstractAgent view) {
		List<CAbstractAgent> outList = new LinkedList<CAbstractAgent>();
		if (view == null || view.getNameId() != subViewClass.getAgentTypeId())
			return outList;
		for (CAbstractAgent aAgent : storage) {
			boolean isHave = true;
			for (CAbstractSite site : view.getSitesMap().values()) {
				// if (!aAgent.getSite(site.getNameId()).isFit(site)) {
				if (!site.isFit(aAgent.getSite(site.getNameId()))) {
					isHave = false;
					break;
				}
			}
			if (isHave)
				outList.add(aAgent);
		}
		return outList;
	}

	//==========================================================================
	private List<CAbstractAgent> convertAgentsToAbstract(List<CAgent> agents,
			CAbstractAgent fillingAgent) {
		List<CAbstractAgent> outlist = new LinkedList<CAbstractAgent>();
		for (CAgent agent : agents) {

			CAbstractAgent needAgent = fillingAgent.clone();
			needAgent.addAllStates(agent);
			// for (CAbstractSite aSite : needAgent.getSitesMap().values())
			// aSite.addStates(agent.getSiteById(aSite.getNameId()));
			outlist.add(needAgent);
		}
		return outlist;
	}

	private List<CAgent> getNeedAgentsBySolution(Collection<CAgent> agents) {
		List<CAgent> outList = new LinkedList<CAgent>();
		for (CAgent a : agents)
			if (a.getNameId() == subViewClass.getAgentTypeId())
				outList.add(a);
		return outList;
	}

	public boolean isAgentFit(CAbstractAgent agent) {
		for (CAbstractSite site : agent.getSitesMap().values())
			if (!subViewClass.isHaveSite(site.getNameId()))
				return false;
		return true;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof CSubViewClass))
			return false;

		CSubViewClass inClass = (CSubViewClass) obj;
		if (!subViewClass.equals(inClass))
			return false;
		return true;
	}

	public int hashCode() {
		return subViewClass.hashCode();
	}

	public String toString() {
		return subViewClass.toString();
	}

	public boolean burnBreakAllNeedLinkState(AbstractAction action) {
		// TODO Auto-generated method stub
		List<CAbstractSite> breakingSites = action.getBreakingSites();
		
		
		return false;
	}
}
