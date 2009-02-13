package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

public class UCorrelationAbstractSite {
	private CActionType type;
	private ECorrelationType correlationType;
	private IContactMapAbstractSite fromSite;
	private IContactMapAbstractSite toSite;
	private List<CActionType> atomicActions;

	public UCorrelationAbstractSite(IContactMapAbstractSite fromSite,
			IContactMapAbstractSite toSite, ECorrelationType correlationType) {
		this.fromSite = fromSite;
		this.toSite = toSite;
		this.correlationType = correlationType;
		if (correlationType == ECorrelationType.CORRELATION_LHS_AND_RHS)
			this.type = CActionType.NONE;
	}

	public IContactMapAbstractSite getFromSite() {
		return fromSite;
	}

	public void setFromSite(IContactMapAbstractSite fromSite) {
		this.fromSite = fromSite;
	}

	public IContactMapAbstractSite getToSite() {
		return toSite;
	}

	public void setToSite(IContactMapAbstractSite toSite) {
		this.toSite = toSite;
	}

	public CActionType getType() {
		return type;
	}

	public void setType(CActionType type) {
		this.type = type;
	}

	public static List<UCorrelationAbstractSite> createCorrelationSites(
			List<IContactMapAbstractSite> fromSites,
			List<IContactMapAbstractSite> toSites,
			ECorrelationType correlationType) {
		List<UCorrelationAbstractSite> list = new ArrayList<UCorrelationAbstractSite>();
		int i = 0;
		for (IContactMapAbstractSite s : fromSites) {
			list.add(new UCorrelationAbstractSite(s, toSites.get(i++),
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
		CContactMapLinkState linkStateFrom = fromSite.getLinkState();
		CContactMapLinkState linkStateTo = toSite.getLinkState();
		if (linkStateFrom.getAgentNameID() != CSite.NO_INDEX
				&& linkStateTo.getAgentNameID() == CSite.NO_INDEX) {
			atomicActions.add(CActionType.BREAK);
			return;
		}
		if (linkStateFrom.getStatusLinkRank() == CLinkRank.SEMI_LINK
				&& linkStateTo.getStatusLinkRank() == CLinkRank.FREE) {
			atomicActions.add(CActionType.BREAK);
			return;
		}

		if (linkStateFrom.getAgentNameID() == CSite.NO_INDEX
				&& linkStateTo.getAgentNameID() != CSite.NO_INDEX) {
			atomicActions.add(CActionType.BOUND);
			return;
		}
		atomicActions.add(CActionType.BREAK);
		atomicActions.add(CActionType.BOUND);
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
		CLinkRank linkRank = fromSite.getLinkState().getStatusLinkRank();
		if (linkRank == CLinkRank.BOUND) {
			newSite.getLinkState().setFreeLinkState();
			return listOut;
		}
		if (linkRank == CLinkRank.BOUND_OR_FREE) {
			if (newSite.getLinkState().getLinkSiteNameID() == CSite.NO_INDEX)
				return listOut;
		}

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
		newSite.getInternalState().setNameId(
				toSite.getInternalState().getNameId());
	}

	private List<IContactMapAbstractSite> doDelete(
			IContactMapAbstractSite newSite,
			CContactMapAbstractSolution solution) {
		List<IContactMapAbstractSite> listOut = new ArrayList<IContactMapAbstractSite>();
		IContactMapAbstractAgent agent = solution.getAbstractAgentMap().get(newSite.getAgentLink().getNameId());
		if(agent == null)
			return listOut;
		for(IContactMapAbstractSite s : agent.getSites()){
			listOut.addAll(breakLinkedSite(s, solution));
		}
		return listOut;
	}
	
	private List<IContactMapAbstractSite> breakLinkedSite(IContactMapAbstractSite inSite, CContactMapAbstractSolution solution){
		List<IContactMapAbstractSite> listOut = new ArrayList<IContactMapAbstractSite>();
		IContactMapAbstractSite site = solution.findSite(inSite.getLinkState()
				.getAgentNameID(), inSite.getLinkState().getLinkSiteNameID(),
				inSite.getLinkState().getInternalStateNameID(), inSite
				.getAgentLink().getNameId(), inSite.getNameId(),
				inSite.getInternalState().getNameId());
		IContactMapAbstractSite addSite = site.clone();
		addSite.getLinkState().setFreeLinkState();
		listOut.add(addSite);
		return listOut;
	}
}
