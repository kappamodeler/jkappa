package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

public class UCorrelationAbstractAgent {
	private CActionType type;
	private ECorrelationType correlationType;
	private IContactMapAbstractAgent fromAgent;
	private IContactMapAbstractAgent toAgent;
	private List<CActionType> atomicActions;
	private CContactMapAbstractAction action;

	public UCorrelationAbstractAgent(CContactMapAbstractAction action,IContactMapAbstractAgent fromAgent,
			IContactMapAbstractAgent toAgent, ECorrelationType correlationType) {
		this.fromAgent = fromAgent;
		this.toAgent = toAgent;
		this.correlationType = correlationType;
		this.action = action;
		if (correlationType == ECorrelationType.CORRELATION_LHS_AND_RHS)
			this.type = CActionType.NONE;
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

	public CActionType getType() {
		return type;
	}

	public void setType(CActionType type) {
		this.type = type;
	}

	public static List<UCorrelationAbstractAgent> createCorrelationSites(
			CContactMapAbstractAction action,
			List<IContactMapAbstractAgent> fromAgent,
			List<IContactMapAbstractAgent> toAgent,
			ECorrelationType correlationType) {
		List<UCorrelationAbstractAgent> list = new ArrayList<UCorrelationAbstractAgent>();
		int i = 0;
		for (IContactMapAbstractAgent a : fromAgent) {
			list.add(new UCorrelationAbstractAgent(action,a, toAgent.get(i++),
					correlationType));
		}
		return list;
	}

	public void initAtomicActionList() {
		atomicActions = new ArrayList<CActionType>();
		if (correlationType != ECorrelationType.CORRELATION_LHS_AND_RHS)
			return;

		switch (type) {
		case MODIFY:
			atomicActions.add(CActionType.MODIFY);
			break;
		case ABSTRACT_BREAK_OR_BOUND:
			findBreakBound();
			break;
		case ABSTRACT_BREAK_OR_BOUND_AND_MODIFY:
			findBreakBound();
			atomicActions.add(CActionType.MODIFY);
			break;
		case DELETE:
			atomicActions.add(CActionType.DELETE);
			break;
		}
	}

	private void findBreakBound() {
//		CContactMapLinkState linkStateFrom = fromAgent.getLinkState();
//		CContactMapLinkState linkStateTo = toAgent.getLinkState();
//		if (linkStateFrom.getAgentNameID() != CSite.NO_INDEX
//				&& linkStateTo.getAgentNameID() == CSite.NO_INDEX) {
//			atomicActions.add(CActionType.BREAK);
//			return;
//		}
//		if (linkStateFrom.getStatusLinkRank() == CLinkRank.SEMI_LINK
//				&& linkStateTo.getStatusLinkRank() == CLinkRank.FREE) {
//			atomicActions.add(CActionType.BREAK);
//			return;
//		}
//
//		if (linkStateFrom.getAgentNameID() == CSite.NO_INDEX
//				&& linkStateTo.getAgentNameID() != CSite.NO_INDEX) {
//			atomicActions.add(CActionType.BOUND);
//			return;
//		}
//		atomicActions.add(CActionType.BREAK);
//		atomicActions.add(CActionType.BOUND);
	}

	public List<IContactMapAbstractSite> modifySiteFromSolution(
			IContactMapAbstractSite newSite,
			CContactMapAbstractSolution solution) {
		if (correlationType != ECorrelationType.CORRELATION_LHS_AND_RHS)
			return null;
		List<IContactMapAbstractSite> listOut = new ArrayList<IContactMapAbstractSite>();
		for (CActionType t : atomicActions) {
			switch (t) {
			case BREAK:
				listOut.addAll(doBreak(newSite, solution));
				break;
			case DELETE:
				return doDelete(newSite,solution);
			case BOUND:
				doBound(newSite);
				break;
			case MODIFY:
				doModify(newSite);
				break;
			}
		}
		return listOut;
	}

	private List<IContactMapAbstractSite> doBreak(
			IContactMapAbstractSite newSite,
			CContactMapAbstractSolution solution) {
		List<IContactMapAbstractSite> listOut = new ArrayList<IContactMapAbstractSite>();
		// TODO
		
//		CLinkRank linkRank = fromAgent.getLinkState().getStatusLinkRank();
//		if (linkRank == CLinkRank.BOUND) {
//			newSite.getLinkState().setFreeLinkState();
//			return listOut;
//		}
//		if (linkRank == CLinkRank.BOUND_OR_FREE) {
//			if (newSite.getLinkState().getLinkSiteNameID() == CSite.NO_INDEX)
//				return listOut;
//		}

//		IContactMapAbstractSite site = solution.findSite(newSite.getLinkState()
//				.getAgentNameID(), newSite.getLinkState().getLinkSiteNameID(),
//				newSite.getLinkState().getInternalStateNameID(), newSite
//						.getAgentLink().getNameId(), newSite.getNameId(),
//				newSite.getInternalState().getNameId());
//		IContactMapAbstractSite addSite = site.clone();
//		addSite.getLinkState().setFreeLinkState();
//		listOut.add(addSite);
		listOut.addAll(breakLinkedSite(newSite, solution));
		newSite.getLinkState().setFreeLinkState();
		return listOut;
	}

	private void doBound(IContactMapAbstractSite newSite) {
		
	}

	private void doModify(IContactMapAbstractSite newSite) {
		
		//TODO
//		newSite.getInternalState().setNameId(
//				toAgent.getInternalState().getNameId());
	}

	private List<IContactMapAbstractSite> doDelete(
			IContactMapAbstractSite newSite,
			CContactMapAbstractSolution solution) {
		List<IContactMapAbstractSite> listOut = new ArrayList<IContactMapAbstractSite>();
		IContactMapAbstractAgent agent = solution.getAbstractAgentMapOld().get(newSite.getAgentLink().getNameId());
		if(agent == null)
			return listOut;
//		for(IContactMapAbstractSite s : agent.getSites()){
//			listOut.addAll(breakLinkedSite(s, solution));
//		}
		return listOut;
	}
	
	private List<IContactMapAbstractSite> breakLinkedSite(IContactMapAbstractSite inSite, CContactMapAbstractSolution solution){
		List<IContactMapAbstractSite> listOut = new ArrayList<IContactMapAbstractSite>();
		IContactMapAbstractSite site = solution.findSite(inSite.getLinkState()
				.getAgentNameID(), inSite.getLinkState().getLinkSiteNameID(),
				inSite.getLinkState().getInternalStateNameID(), inSite
				.getAgentLink().getNameId(), inSite.getNameId(),
				inSite.getInternalState().getNameId());
		if(site == null)
			return listOut;
		IContactMapAbstractSite addSite = site.clone();
		addSite.getLinkState().setFreeLinkState();
		listOut.add(addSite);
		return listOut;
	}
}
