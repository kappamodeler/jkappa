package com.plectix.simulator.components.complex.contactMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;

class UCorrelationAbstractAgent {
	private CAbstractAgent fromAgent;
	private CAbstractAgent toAgent;
	private List<CContactMapAtomicAction> atomicActionList;

	public UCorrelationAbstractAgent(CContactMapAbstractAction action,
			CAbstractAgent fromAgent,
			CAbstractAgent toAgent) {
		this.fromAgent = fromAgent;
		this.toAgent = toAgent;
	}

	public CAbstractAgent getFromAgent() {
		return fromAgent;
	}

	public void setFromAgent(CAbstractAgent fromAgent) {
		this.fromAgent = fromAgent;
	}

	public CAbstractAgent getToAgent() {
		return toAgent;
	}

	public void setToAgent(CAbstractAgent toAgent) {
		this.toAgent = toAgent;
	}

	public static List<UCorrelationAbstractAgent> createCorrelationSites(
			CContactMapAbstractAction action,
			List<CAbstractAgent> fromAgent,
			List<CAbstractAgent> toAgent) {
		List<UCorrelationAbstractAgent> list = new ArrayList<UCorrelationAbstractAgent>();
		int i = 0;
		for (CAbstractAgent a : fromAgent) {
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
		for (Map.Entry<Integer, CAbstractSite> entry : fromAgent.getSitesMap().entrySet()) {
			CAbstractSite siteFrom = entry.getValue();
			CAbstractSite siteTo = toAgent.getSitesMap().get(entry.getKey());

			if (siteFrom.getInternalState().getNameId() != siteTo
					.getInternalState().getNameId()) {
				fromAgent.shouldAdd();
				toAgent.shouldAdd();
				atomicActionList.add(new CContactMapAtomicAction(
						CActionType.MODIFY, siteFrom));
			}

			CAbstractLinkState lsFrom = siteFrom.getLinkState();
			CAbstractLinkState lsTo = siteTo.getLinkState();

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

	private void findBreakBound(CAbstractSite siteFrom,
			CAbstractSite siteTo) {
		CAbstractLinkState lsFrom = siteFrom.getLinkState();
		CAbstractLinkState lsTo = siteTo.getLinkState();

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

	public List<CAbstractAgent> modifySiteFromSolution(
			CAbstractAgent newAgent,
			CContactMapAbstractSolution solution){
		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
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

	private List<CAbstractAgent> doBreak(
			CAbstractAgent newAgent,
			CContactMapAbstractSolution solution, CContactMapAtomicAction type){
		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		// TODO BRK
		Integer key = type.getSite().getNameId();
//		CContactMapAbstractSiteIContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);
		CAbstractSite siteNew = newAgent.getSitesMap().get(key);

		// CContactMapLinkState lsTo = siteTo.getLinkState();
		CAbstractLinkState lsNew = siteNew.getLinkState();

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

	private List<CAbstractAgent> breakAllAgentsWithSite(
			CAbstractAgent agentNew, CAbstractSite siteNew,
			CContactMapAbstractSolution solution){
		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		List<CAbstractAgent> agentsList = solution
				.getAgentNameIdToAgentsList().get(
						siteNew.getLinkState().getAgentNameID());
		if (agentsList == null)
			return listOut;
		for (CAbstractAgent a : agentsList) {
			int linkSiteNameID = siteNew.getLinkState().getLinkSiteNameID();
			CAbstractSite siteFromSolution = a.getSite(linkSiteNameID);
			
			CAbstractLinkState lsFromSolution = siteFromSolution
					.getLinkState();

			if (lsFromSolution.getAgentNameID() != agentNew.getNameId())
				continue;
			if (lsFromSolution.getLinkSiteNameID() != siteNew.getNameId())
				continue;
//			if (lsFromSolution.getInternalStateNameID() != CSite.NO_INDEX
//					&& siteNew.getInternalState().getNameId() != CSite.NO_INDEX
//					&& lsFromSolution.getInternalStateNameID() != siteNew
//							.getInternalState().getNameId())
//				continue;
			CAbstractAgent agentToAdd = a.clone();
			agentToAdd.getSite(siteFromSolution.getNameId()).getLinkState()
					.setFreeLinkState();
			listOut.add(agentToAdd);
		}

		return listOut;
	}

	private void doBound(CAbstractAgent newAgent,
			CContactMapAtomicAction type) {
		Integer key = type.getSite().getNameId();
		CAbstractSite siteTo = toAgent.getSitesMap().get(key);
		CAbstractSite siteNew = newAgent.getSitesMap().get(key);

		CAbstractLinkState lsTo = siteTo.getLinkState();
		CAbstractLinkState lsNew = siteNew.getLinkState();

		lsNew.setAgentNameID(lsTo.getAgentNameID());
		lsNew.setLinkSiteNameID(lsTo.getLinkSiteNameID());
		lsNew.setStatusLink(CLinkStatus.BOUND);
//		if (lsTo.getInternalStateNameID() != CSite.NO_INDEX)
//			lsNew.setInternalStateNameID(lsTo.getInternalStateNameID());
	}

	private void doModify(CAbstractAgent newAgent,
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

	private List<CAbstractAgent> doDelete(
			CAbstractAgent newAgent,
			CContactMapAbstractSolution solution){
		// TODO DELL

		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		for (CAbstractSite siteNew : newAgent.getSitesMap().values()) {
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
