package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.IInternalState;

public class UCorrelationAbstractAgent {
	private IContactMapAbstractAgent fromAgent;
	private IContactMapAbstractAgent toAgent;
	private List<CContactMapAtomicAction> atomicActionList;

	public UCorrelationAbstractAgent(CContactMapAbstractAction action,
			IContactMapAbstractAgent fromAgent,
			IContactMapAbstractAgent toAgent, ECorrelationType correlationType) {
		this.fromAgent = fromAgent;
		this.toAgent = toAgent;
	}

	public IContactMapAbstractAgent getFromAgent() {
		return fromAgent;
	}

	public void setFromAgent(IContactMapAbstractAgent fromAgent) {
		this.fromAgent = fromAgent;
	}

	public IContactMapAbstractAgent getToAgent() {
		return toAgent;
	}

	public void setToAgent(IContactMapAbstractAgent toAgent) {
		this.toAgent = toAgent;
	}

	public static List<UCorrelationAbstractAgent> createCorrelationSites(
			CContactMapAbstractAction action,
			List<IContactMapAbstractAgent> fromAgent,
			List<IContactMapAbstractAgent> toAgent,
			ECorrelationType correlationType) {
		List<UCorrelationAbstractAgent> list = new ArrayList<UCorrelationAbstractAgent>();
		int i = 0;
		for (IContactMapAbstractAgent a : fromAgent) {
			list.add(new UCorrelationAbstractAgent(action, a, toAgent.get(i++),
					correlationType));
		}
		return list;
	}

	public void initAtomicAction() {
		atomicActionList = new ArrayList<CContactMapAtomicAction>();
		if (toAgent == null) {
			fromAgent.shouldAdd();
			atomicActionList.add(new CContactMapAtomicAction(
					CActionType.DELETE, null, null));
			return;
		}
		Iterator<Integer> iterator = fromAgent.getSitesMap().keySet()
				.iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();

			IContactMapAbstractSite siteFrom = fromAgent.getSitesMap().get(key);
			IContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);

			if (siteFrom.getInternalState().getNameId() != siteTo
					.getInternalState().getNameId()) {
				fromAgent.shouldAdd();
				toAgent.shouldAdd();
				atomicActionList.add(new CContactMapAtomicAction(
						CActionType.MODIFY, siteFrom, siteTo));
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

	private void findBreakBound(IContactMapAbstractSite siteFrom,
			IContactMapAbstractSite siteTo) {
		CContactMapLinkState lsFrom = siteFrom.getLinkState();
		CContactMapLinkState lsTo = siteTo.getLinkState();

		if (lsFrom.getAgentNameID() != CSite.NO_INDEX
				&& lsTo.getAgentNameID() == CSite.NO_INDEX) {
			atomicActionList.add(new CContactMapAtomicAction(CActionType.BREAK,
					siteFrom, siteTo));
			return;
		}
		if (lsFrom.getStatusLinkRank() == CLinkRank.SEMI_LINK
				&& lsTo.getStatusLinkRank() == CLinkRank.FREE) {
			atomicActionList.add(new CContactMapAtomicAction(CActionType.BREAK,
					siteFrom, siteTo));
			return;
		}

		if (lsFrom.getAgentNameID() == CSite.NO_INDEX
				&& lsTo.getAgentNameID() != CSite.NO_INDEX) {
			atomicActionList.add(new CContactMapAtomicAction(CActionType.BOUND,
					siteFrom, siteTo));
			return;
		}
		atomicActionList.add(new CContactMapAtomicAction(CActionType.BREAK,
				siteFrom, siteTo));
		atomicActionList.add(new CContactMapAtomicAction(CActionType.BOUND,
				siteFrom, siteTo));
	}

	public List<IContactMapAbstractAgent> modifySiteFromSolution(
			IContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution) {
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		for (CContactMapAtomicAction t : atomicActionList) {
			switch (t.getType()) {
			case BREAK:
				listOut.addAll(doBreak(newAgent, solution, t));
				break;
			case DELETE:
				return doDelete(newAgent, solution);
			case BOUND:
				doBound(newAgent, t);
				break;
			case MODIFY:
				doModify(newAgent, t);
				break;
			}
		}
		return listOut;
	}

	private List<IContactMapAbstractAgent> doBreak(
			IContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution, CContactMapAtomicAction type) {
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		// TODO BRK
		Integer key = type.getSiteFrom().getNameId();
		IContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);
		IContactMapAbstractSite siteNew = newAgent.getSitesMap().get(key);

		CContactMapLinkState lsTo = siteTo.getLinkState();
		CContactMapLinkState lsNew = siteNew.getLinkState();

		// if (lsNew.getAgentNameID() != lsTo.getAgentNameID()
		// || lsNew.getLinkSiteNameID() != lsTo.getLinkSiteNameID()) {
		lsNew.setFreeLinkState();
		// lsNew.setAgentNameID(CSite.NO_INDEX);
		// lsNew.setLinkSiteNameID(CSite.NO_INDEX);
		// lsNew.setStatusLink(CLinkStatus.FREE);
		// }
		return listOut;
	}

	private void doBound(IContactMapAbstractAgent newAgent,
			CContactMapAtomicAction type) {
		Integer key = type.getSiteFrom().getNameId();
		IContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);
		IContactMapAbstractSite siteNew = newAgent.getSitesMap().get(key);

		CContactMapLinkState lsTo = siteTo.getLinkState();
		CContactMapLinkState lsNew = siteNew.getLinkState();

		lsNew.setAgentNameID(lsTo.getAgentNameID());
		lsNew.setLinkSiteNameID(lsTo.getLinkSiteNameID());
		lsNew.setStatusLink(CLinkStatus.BOUND);
		if (lsTo.getInternalStateNameID() != CSite.NO_INDEX)
			lsNew.setInternalStateNameID(lsTo.getInternalStateNameID());
	}

	private void doModify(IContactMapAbstractAgent newAgent,
			CContactMapAtomicAction type) {
		Integer key = type.getSiteFrom().getNameId();
		IInternalState stateTo = toAgent.getSitesMap().get(key)
				.getInternalState();
		if (stateTo.getNameId() == CSite.NO_INDEX)
			return;
		IInternalState stateNew = newAgent.getSitesMap().get(key)
				.getInternalState();
		stateNew.setNameId(stateTo.getNameId());
	}

	private List<IContactMapAbstractAgent> doDelete(
			IContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution) {
		// TODO DELL

		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		Iterator<Integer> iterator = newAgent.getSitesMap().keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			CContactMapLinkState ls = newAgent.getSitesMap().get(key)
					.getLinkState();
			if (ls.getAgentNameID() == CSite.NO_INDEX)
				continue;
			List<IContactMapAbstractAgent> agentlist = solution
					.getAgentNameIdToAgentsList().get(ls.getAgentNameID());
			if (agentlist == null || agentlist.isEmpty())
				continue;

			// TODO ...

		}

		// IContactMapAbstractAgent agent =
		// solution.getAbstractAgentMapOld().get
		// (newSite.getAgentLink().getNameId());
		// if(agent == null)
		// return listOut;
		// for(IContactMapAbstractSite s : agent.getSites()){
		// listOut.addAll(breakLinkedSite(s, solution));
		// }
		return listOut;
	}

	private List<IContactMapAbstractSite> breakLinkedSite(
			IContactMapAbstractSite inSite, CContactMapAbstractSolution solution) {
		List<IContactMapAbstractSite> listOut = new ArrayList<IContactMapAbstractSite>();
		IContactMapAbstractSite site = solution.findSite(inSite.getLinkState()
				.getAgentNameID(), inSite.getLinkState().getLinkSiteNameID(),
				inSite.getLinkState().getInternalStateNameID(), inSite
						.getAgentLink().getNameId(), inSite.getNameId(), inSite
						.getInternalState().getNameId());
		if (site == null)
			return listOut;
		IContactMapAbstractSite addSite = site.clone();
		addSite.getLinkState().setFreeLinkState();
		listOut.add(addSite);
		return listOut;
	}
}
