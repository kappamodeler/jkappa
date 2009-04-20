package com.plectix.simulator.components.contactMap;

import java.util.*;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.*;

class UCorrelationAbstractAgent {
	private CContactMapAbstractAgent fromAgent;
	private CContactMapAbstractAgent toAgent;
	private List<CContactMapAtomicAction> atomicActionList;

	public UCorrelationAbstractAgent(CContactMapAbstractAction action,
			CContactMapAbstractAgent fromAgent,
			CContactMapAbstractAgent toAgent) {
		this.fromAgent = fromAgent;
		this.toAgent = toAgent;
	}

	public CContactMapAbstractAgent getFromAgent() {
		return fromAgent;
	}

	public void setFromAgent(CContactMapAbstractAgent fromAgent) {
		this.fromAgent = fromAgent;
	}

	public CContactMapAbstractAgent getToAgent() {
		return toAgent;
	}

	public void setToAgent(CContactMapAbstractAgent toAgent) {
		this.toAgent = toAgent;
	}

	public static List<UCorrelationAbstractAgent> createCorrelationSites(
			CContactMapAbstractAction action,
			List<CContactMapAbstractAgent> fromAgent,
			List<CContactMapAbstractAgent> toAgent) {
		List<UCorrelationAbstractAgent> list = new ArrayList<UCorrelationAbstractAgent>();
		int i = 0;
		for (CContactMapAbstractAgent a : fromAgent) {
			list.add(new UCorrelationAbstractAgent(action, a, toAgent.get(i++)));
		}
		return list;
	}

	public void initAtomicAction() {
		atomicActionList = new ArrayList<CContactMapAtomicAction>();
		if (toAgent == null) {
			fromAgent.shouldAdd();
			atomicActionList.add(new CContactMapAtomicAction(
					CActionType.DELETE, null));
			return;
		}
		Iterator<Integer> iterator = fromAgent.getSitesMap().keySet()
				.iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();

			CContactMapAbstractSite siteFrom = fromAgent.getSitesMap().get(key);
			CContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);

			if (siteFrom.getInternalState().getNameId() != siteTo
					.getInternalState().getNameId()) {
				fromAgent.shouldAdd();
				toAgent.shouldAdd();
				atomicActionList.add(new CContactMapAtomicAction(
						CActionType.MODIFY, siteFrom));
			}

			CContactMapLinkState lsFrom = siteFrom.getLinkState();
			CContactMapLinkState lsTo = siteTo.getLinkState();

			if (lsFrom.getAgentNameID() != lsTo.getAgentNameID()
					|| (lsFrom.getLinkSiteNameID() != lsTo.getLinkSiteNameID())) {
				fromAgent.shouldAdd();
				toAgent.shouldAdd();
				findBreakBound(siteFrom, siteTo);
			}
		}

		sortAtomicActions();
	}

	private void sortAtomicActions() {
		// NONE(-1),
		// BREAK(0),
		// DELETE(1),
		// ADD(2),
		// BOUND(3),
		// MODIFY(4);
		for (int i = 0; i < atomicActionList.size(); i++) {
			for (int j = i + 1; j < atomicActionList.size(); j++) {
				CContactMapAtomicAction actionI = atomicActionList.get(i);
				CContactMapAtomicAction actionJ = atomicActionList.get(j);
				if (actionI.getType().getId() > actionJ.getType().getId()) {
					atomicActionList.set(i, actionJ);
					atomicActionList.set(j, actionI);
				}
			}
		}
	}

	private void findBreakBound(CContactMapAbstractSite siteFrom,
			CContactMapAbstractSite siteTo) {
		CContactMapLinkState lsFrom = siteFrom.getLinkState();
		CContactMapLinkState lsTo = siteTo.getLinkState();

		if (lsFrom.getAgentNameID() != CSite.NO_INDEX
				&& lsTo.getAgentNameID() == CSite.NO_INDEX) {
			atomicActionList.add(new CContactMapAtomicAction(CActionType.BREAK,
					siteFrom));
			return;
		}
		if (lsFrom.getStatusLinkRank() == CLinkRank.SEMI_LINK
				&& lsTo.getStatusLinkRank() == CLinkRank.FREE) {
			atomicActionList.add(new CContactMapAtomicAction(CActionType.BREAK,
					siteFrom));
			return;
		}

		if (lsFrom.getAgentNameID() == CSite.NO_INDEX
				&& lsTo.getAgentNameID() != CSite.NO_INDEX) {
			atomicActionList.add(new CContactMapAtomicAction(CActionType.BOUND,
					siteFrom));
			return;
		}
		atomicActionList.add(new CContactMapAtomicAction(CActionType.BREAK,
				siteFrom));
		atomicActionList.add(new CContactMapAtomicAction(CActionType.BOUND,
				siteFrom));
	}

	public List<CContactMapAbstractAgent> modifySiteFromSolution(
			CContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution){
		List<CContactMapAbstractAgent> listOut = new ArrayList<CContactMapAbstractAgent>();
		for (CContactMapAtomicAction t : atomicActionList) {
			switch (t.getType()) {
			case BREAK:
				listOut.addAll(doBreak(newAgent, solution, t));
				listOut.add(newAgent);			
				break;
			case DELETE:
				return doDelete(newAgent, solution);
			case BOUND:
				doBound(newAgent, t);
				listOut.add(newAgent);			
				break;
			case MODIFY:
				doModify(newAgent, t);
				listOut.add(newAgent);			
				break;
			}
		}
		return listOut;
	}

	private List<CContactMapAbstractAgent> doBreak(
			CContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution, CContactMapAtomicAction type){
		List<CContactMapAbstractAgent> listOut = new ArrayList<CContactMapAbstractAgent>();
		// TODO BRK
		Integer key = type.getSite().getNameId();
//		CContactMapAbstractSiteIContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);
		CContactMapAbstractSite siteNew = newAgent.getSitesMap().get(key);

		// CContactMapLinkState lsTo = siteTo.getLinkState();
		CContactMapLinkState lsNew = siteNew.getLinkState();

		if (type.getSite().getLinkState().getStatusLinkRank() == CLinkRank.BOUND_OR_FREE
				|| type.getSite().getLinkState().getStatusLinkRank() == CLinkRank.SEMI_LINK)
			listOut.addAll(breakAllAgentsWithSite(newAgent, siteNew, solution));

		// if (lsNew.getAgentNameID() != lsTo.getAgentNameID()
		// || lsNew.getLinkSiteNameID() != lsTo.getLinkSiteNameID()) {
		lsNew.setFreeLinkState();
		// lsNew.setAgentNameID(CSite.NO_INDEX);
		// lsNew.setLinkSiteNameID(CSite.NO_INDEX);
		// lsNew.setStatusLink(CLinkStatus.FREE);
		// }
		return listOut;
	}

	private List<CContactMapAbstractAgent> breakAllAgentsWithSite(
			CContactMapAbstractAgent agentNew, CContactMapAbstractSite siteNew,
			CContactMapAbstractSolution solution){
		List<CContactMapAbstractAgent> listOut = new ArrayList<CContactMapAbstractAgent>();
		List<CContactMapAbstractAgent> agentsList = solution
				.getAgentNameIdToAgentsList().get(
						siteNew.getLinkState().getAgentNameID());
		if (agentsList == null)
			return listOut;
		for (CContactMapAbstractAgent a : agentsList) {
			int linkSiteNameID = siteNew.getLinkState().getLinkSiteNameID();
			CContactMapAbstractSite siteFromSolution = a.getSite(linkSiteNameID);
			
			CContactMapLinkState lsFromSolution = siteFromSolution
					.getLinkState();

			if (lsFromSolution.getAgentNameID() != agentNew.getNameId())
				continue;
			if (lsFromSolution.getLinkSiteNameID() != siteNew.getNameId())
				continue;
			if (lsFromSolution.getInternalStateNameID() != CSite.NO_INDEX
					&& siteNew.getInternalState().getNameId() != CSite.NO_INDEX
					&& lsFromSolution.getInternalStateNameID() != siteNew
							.getInternalState().getNameId())
				continue;
			CContactMapAbstractAgent agentToAdd = a.clone();
			agentToAdd.getSite(siteFromSolution.getNameId()).getLinkState()
					.setFreeLinkState();
			listOut.add(agentToAdd);
		}

		return listOut;
	}

	private void doBound(CContactMapAbstractAgent newAgent,
			CContactMapAtomicAction type) {
		Integer key = type.getSite().getNameId();
		CContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);
		CContactMapAbstractSite siteNew = newAgent.getSitesMap().get(key);

		CContactMapLinkState lsTo = siteTo.getLinkState();
		CContactMapLinkState lsNew = siteNew.getLinkState();

		lsNew.setAgentNameID(lsTo.getAgentNameID());
		lsNew.setLinkSiteNameID(lsTo.getLinkSiteNameID());
		lsNew.setStatusLink(CLinkStatus.BOUND);
		if (lsTo.getInternalStateNameID() != CSite.NO_INDEX)
			lsNew.setInternalStateNameID(lsTo.getInternalStateNameID());
	}

	private void doModify(CContactMapAbstractAgent newAgent,
			CContactMapAtomicAction type) {
		Integer key = type.getSite().getNameId();
		CInternalState stateTo = toAgent.getSitesMap().get(key)
				.getInternalState();
		if (stateTo.getNameId() == CSite.NO_INDEX)
			return;
		CInternalState stateNew = newAgent.getSitesMap().get(key)
				.getInternalState();
		stateNew.setNameId(stateTo.getNameId());
	}

	private List<CContactMapAbstractAgent> doDelete(
			CContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution){
		// TODO DELL

		List<CContactMapAbstractAgent> listOut = new ArrayList<CContactMapAbstractAgent>();
		Iterator<Integer> iterator = newAgent.getSitesMap().keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			CContactMapAbstractSite siteNew = newAgent.getSite(key);
			if (siteNew.getLinkState().getAgentNameID() == CSite.NO_INDEX)
				continue;
			listOut.addAll(breakAllAgentsWithSite(newAgent, siteNew, solution));
		}

		// CContactMapAbstractAgent agent =
		// solution.getAbstractAgentMapOld().get
		// (newSite.getAgentLink().getNameId());
		// if(agent == null)
		// return listOut;
		// for(CContactMapAbstractSiteIContactMapAbstractSite s : agent.getSites()){
		// listOut.addAll(breakLinkedSite(s, solution));
		// }
		return listOut;
	}

//	private List<CContactMapAbstractSiteIContactMapAbstractSite> breakLinkedSite(
//			CContactMapAbstractSiteIContactMapAbstractSite inSite, CContactMapAbstractSolution solution) {
//		List<CContactMapAbstractSiteIContactMapAbstractSite> listOut = new ArrayList<CContactMapAbstractSiteIContactMapAbstractSite>();
//		CContactMapAbstractSiteIContactMapAbstractSite site = solution.findSite(inSite.getLinkState()
//				.getAgentNameID(), inSite.getLinkState().getLinkSiteNameID(),
//				inSite.getLinkState().getInternalStateNameID(), inSite
//						.getAgentLink().getNameId(), inSite.getNameId(), inSite
//						.getInternalState().getNameId());
//		if (site == null)
//			return listOut;
//		CContactMapAbstractSiteIContactMapAbstractSite addSite = site.clone();
//		addSite.getLinkState().setFreeLinkState();
//		listOut.add(addSite);
//		return listOut;
//	}
}
