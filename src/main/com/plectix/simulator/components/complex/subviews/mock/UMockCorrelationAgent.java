package com.plectix.simulator.components.complex.subviews.mock;

import java.util.*;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.*;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;

class UMockCorrelationAgent {
	private CAbstractAgent fromAgent;
	private CAbstractAgent toAgent;
	private List<MockAtomicAction> atomicActionList;

	public UMockCorrelationAgent(MockAction action,
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

	public static List<UMockCorrelationAgent> createCorrelationSites(
			MockAction action,
			List<CAbstractAgent> fromAgent,
			List<CAbstractAgent> toAgent) {
		List<UMockCorrelationAgent> list = new ArrayList<UMockCorrelationAgent>();
		int i = 0;
		for (CAbstractAgent a : fromAgent) {
			list.add(new UMockCorrelationAgent(action, a, toAgent.get(i++)));
		}
		return list;
	}

	public void initAtomicAction() {
		atomicActionList = new ArrayList<MockAtomicAction>();
		if (toAgent == null) {
			fromAgent.shouldAdd();
			atomicActionList.add(new MockAtomicAction(
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
				atomicActionList.add(new MockAtomicAction(
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
				MockAtomicAction actionI = atomicActionList.get(i);
				MockAtomicAction actionJ = atomicActionList.get(j);
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
			atomicActionList.add(new MockAtomicAction(CActionType.BREAK,
					siteFrom));
			return;
		}
		if (lsFrom.getStatusLinkRank() == CLinkRank.SEMI_LINK
				&& lsTo.getStatusLinkRank() == CLinkRank.FREE) {
			atomicActionList.add(new MockAtomicAction(CActionType.BREAK,
					siteFrom));
			return;
		}

		if (lsFrom.getAgentNameID() == CSite.NO_INDEX
				&& lsTo.getAgentNameID() != CSite.NO_INDEX) {
			atomicActionList.add(new MockAtomicAction(CActionType.BOUND,
					siteFrom));
			return;
		}
		atomicActionList.add(new MockAtomicAction(CActionType.BREAK,
				siteFrom));
		atomicActionList.add(new MockAtomicAction(CActionType.BOUND,
				siteFrom));
	}

	public List<CAbstractAgent> modifySiteFromSolution(
			CAbstractAgent newAgent,
			CMockSubViews solution){
		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		for (MockAtomicAction t : atomicActionList) {
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
			CMockSubViews solution, MockAtomicAction type){
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
			CMockSubViews solution){
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
			MockAtomicAction type) {
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
			MockAtomicAction type) {
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
			CMockSubViews solution){
		// TODO DELL

		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		for (CAbstractSite siteNew : newAgent.getSitesMap().values()) {
			if (siteNew.getLinkState().getAgentNameID() == CSite.NO_INDEX)
				continue;
			listOut.addAll(breakAllAgentsWithSite(newAgent, siteNew, solution));
		}
		return listOut;
	}
}
