package com.plectix.simulator.components.complex.subviews.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.CSubViewClass;
import com.plectix.simulator.components.complex.subviews.base.AbstractAction;
import com.plectix.simulator.components.complex.subviews.base.EAbstractActionType;

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
		for (CAbstractAgent agent : convertAgentsToAbstract(
				getNeedAgentsBySolution(agents), needAgent))
			try {
				addAbstractAgent(agent);
			} catch (SubViewsExeption e) {
				e.printStackTrace();
			}
	}

	public boolean addAbstractAgent(CAbstractAgent agent)
			throws SubViewsExeption {
		if (test(agent))
			return false;
		storage.add(agent);
		return true;
	}

	public boolean burnRule(AbstractAction action) throws SubViewsExeption {
		CAbstractAgent oldViews = action.getLeftHandSideAgent();
		CAbstractAgent newViews = action.getRightHandSideAgent();

		// if (oldViews == null)
		// return addAbstractAgent(newViews);

		List<CAbstractAgent> agentsList;
		if (testDeleteAction(action))
			agentsList = storage;
		else
			agentsList = getAllSubViews(oldViews);
		initBreakingSites(action, agentsList);
		switch (action.getActionType()) {
		case ADD:
			return addAbstractAgent(newViews.clone());
		case TEST_ONLY:
			return false;
		case DELETE:
			return false;
		}

		boolean isAdd = false;
		for (CAbstractAgent agentFromStorage : agentsList) {
			CAbstractAgent newAgent = agentFromStorage.clone();
			newAgent.addAllStates(newViews);
			if (addAbstractAgent(newAgent))
				isAdd = true;
		}
		return isAdd;

	}

	private boolean testDeleteAction(AbstractAction action) {
		if (action.getActionType() != EAbstractActionType.DELETE)
			return false;
		boolean isNotHave = true;
		for (CAbstractSite site : action.getLeftHandSideAgent().getSitesMap()
				.values())
			if (subViewClass.isHaveSite(site.getNameId()))
				return false;

		return isNotHave;
	}

	private void initBreakingSites(AbstractAction action,
			List<CAbstractAgent> agentsList) {
		List<Integer> sideEffectId = action.getSideEffect();
		Map<Integer, CAbstractAgent> sideEfectMap = new HashMap<Integer, CAbstractAgent>();
		switch (action.getActionType()) {
		case DELETE: {
			for (CAbstractAgent agent : agentsList)
				for (CAbstractSite site : agent.getSitesMap().values())
					initSideEffectSite(site, agent, sideEfectMap, action);
			break;
		}
		case TEST_AND_MODIFICATION: {
			if (sideEffectId == null)
				return;
			for (CAbstractAgent agent : agentsList)
				for (Integer siteId : sideEffectId) {
					CAbstractSite site = agent.getSite(siteId);
					initSideEffectSite(site, agent, sideEfectMap, action);
				}
			break;
		}
		}
	}

	private void initSideEffectSite(CAbstractSite site, CAbstractAgent agent,
			Map<Integer, CAbstractAgent> sideEfectMap, AbstractAction action) {
		CAbstractLinkState linkState = site.getLinkState();
		if (linkState.getAgentNameID() != CSite.NO_INDEX) {
			int newAgentNameId = linkState.getAgentNameID();
			int newSiteNameId = linkState.getLinkSiteNameID();
			int hashValue = generateHash(newAgentNameId, newSiteNameId, agent
					.getNameId(), site.getNameId());
			if (sideEfectMap.get(hashValue) == null) {
				CAbstractAgent newAgent = createSideEffectAgent(newAgentNameId,
						newSiteNameId, agent.getNameId(), site.getNameId());
				sideEfectMap.put(hashValue, newAgent);
				action.addSiteSideEffect(newAgent.getSite(newSiteNameId));
			}
		}
	}

	private static CAbstractAgent createSideEffectAgent(int agentNameId,
			int siteNameId, int linkAgentNameId, int linkSiteNameId) {
		CAbstractAgent newAgent = new CAbstractAgent(agentNameId);
		CAbstractSite newSite = new CAbstractSite(newAgent, siteNameId);
		newSite.getLinkState().setAgentNameID(linkAgentNameId);
		newSite.getLinkState().setLinkSiteNameID(linkSiteNameId);
		newSite.getLinkState().setStatusLink(CLinkStatus.BOUND);
		newAgent.addSite(newSite);
		return newAgent;
	}

	private static int generateHash(int agentNameId, int siteNameId,
			int linkAgentNameId, int linkSiteNameId) {
		int hashValue = 11;
		hashValue = 31 * hashValue + agentNameId;
		hashValue = 31 * hashValue + siteNameId;
		hashValue = 31 * hashValue + linkAgentNameId;
		hashValue = 31 * hashValue + linkSiteNameId;
		return hashValue;
	}

	public boolean burnRule(CAbstractAgent oldViews, CAbstractAgent newViews)
			throws SubViewsExeption {
		// if (oldViews == null)
		// return addAbstractAgent(newViews);
		//
		// if (newViews == null) {
		// // TODO DELETE Action!!!
		// }
		// boolean isAdd = false;
		// List<CAbstractAgent> agentsList = getAllSubViews(oldViews);
		// for (CAbstractAgent agentFromStorage : agentsList) {
		// CAbstractAgent newAgent = agentFromStorage.clone();
		// newAgent.addAllStates(newViews);
		// if (addAbstractAgent(newAgent))
		// isAdd = true;
		// }
		// return isAdd;
		return false;
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
		boolean isHave = false;
		if (testView.getSitesMap().isEmpty())
			if (!storage.isEmpty()) {
				return true;
			} else
				return false;

		for (CAbstractSite site : testView.getSitesMap().values())
			if (subViewClass.isHaveSite(site.getNameId()))
				isHave = true;
		if (!isHave)
			throw new SubViewsExeption(subViewClass, testView);

		for (CAbstractAgent aAgent : storage) {
			isHave = true;
			for (CAbstractSite site : testView.getSitesMap().values()) {
				int siteId = site.getNameId();
				// if (!aAgent.getSite(siteId).isFit(site)) {
				if ((subViewClass.isHaveSite(siteId))
						&& (!site.isFit(aAgent.getSite(siteId)))) {
					isHave = false;
					break;
				}
			}
			if (isHave)
				return true;
		}
		return false;
	}
	
	public List<CAbstractAgent> getAllSubViews(){
		return storage;
	}

	public List<CAbstractAgent> getAllSubViews(CAbstractAgent view) {
		List<CAbstractAgent> outList = new LinkedList<CAbstractAgent>();
		if (view == null || view.getNameId() != subViewClass.getAgentTypeId())
			return outList;
		if (view.getSitesMap().isEmpty())
			return storage;
		for (CAbstractAgent aAgent : storage) {
			boolean isHave = false;
			for (CAbstractSite site : view.getSitesMap().values()) {
				// if (!aAgent.getSite(site.getNameId()).isFit(site)) {
				int siteId = site.getNameId();
				if (subViewClass.isHaveSite(siteId)
						&& site.isFit(aAgent.getSite(siteId))) {
					isHave = true;
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
		List<CAbstractSite> breakingSites = action.getSitesSideEffect();
		List<CAbstractAgent> addlist = new LinkedList<CAbstractAgent>();
		if (breakingSites == null)
			return false;
		for (CAbstractSite site : breakingSites) {
			if (site.getAgentLink().getNameId() != subViewClass
					.getAgentTypeId())
				continue;
			for (CAbstractAgent agentFromStorage : storage) {
				CAbstractSite siteFromStorage = agentFromStorage.getSite(site
						.getNameId());
//				if (site.equalz(siteFromStorage)) {
				if (siteFromStorage!=null && site.isFit(siteFromStorage)) {
					CAbstractAgent newAgent = new CAbstractAgent(
							agentFromStorage);
					newAgent.getSite(site.getNameId()).getLinkState()
							.setFreeLinkState();
					addlist.add(newAgent);
				}
			}
		}
		boolean isAdd = false;
		for (CAbstractAgent agent : addlist)
			try {
				if (addAbstractAgent(agent))
					isAdd = true;
			} catch (SubViewsExeption e) {
				// e.printStackTrace();
			}

		return isAdd;
	}

	public CSubViewClass getSubViewClass() {
		return subViewClass;
	}

	@Override
	public List<CAbstractAgent> getAllSubViewsCoherent(CAbstractAgent view) {
		List<CAbstractAgent> outList = new LinkedList<CAbstractAgent>();
		if (view == null||view.getSitesMap().isEmpty())
			return storage;
		
		for (CAbstractAgent aAgent : storage) {
			boolean isHave = true;
			for (CAbstractSite site : view.getSitesMap().values()) {
				int siteId = site.getNameId();
				if (subViewClass.isHaveSite(siteId)
						&& !site.isFit(aAgent.getSite(siteId))) {
					isHave = false;
				}
			}
			if (isHave)
				outList.add(aAgent.plus(view));
		}
		return outList;
	}
}
