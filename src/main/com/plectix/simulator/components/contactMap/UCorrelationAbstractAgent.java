package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Iterator;
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
	
	public void initAtomicAction(){
		// TODO
		Iterator<Integer> iterator = fromAgent.getSitesMap().keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			if(toAgent == null){
				fromAgent.shouldAdd();
				continue;
			}
				
			IContactMapAbstractSite siteFrom = fromAgent.getSitesMap().get(key);
			IContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);
			
			if(siteFrom.getInternalState().getNameId() != siteTo.getInternalState().getNameId()){
				fromAgent.shouldAdd();
				toAgent.shouldAdd();
				// TODO action MOD
			}
			
			CContactMapLinkState lsFrom = siteFrom.getLinkState();
			CContactMapLinkState lsTo = siteTo.getLinkState();
			
			if(lsFrom.getAgentNameID()!=lsTo.getAgentNameID() || (lsFrom.getLinkSiteNameID()!= lsTo.getLinkSiteNameID()) ){
				fromAgent.shouldAdd();
				toAgent.shouldAdd();
			}
			
			
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

	public List<IContactMapAbstractAgent> modifySiteFromSolution(
			IContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution) {
		if (correlationType != ECorrelationType.CORRELATION_LHS_AND_RHS)
			return null;
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		for (CActionType t : atomicActions) {
			switch (t) {
			case BREAK:
				listOut.addAll(doBreak(newAgent, solution));
				break;
			case DELETE:
				return doDelete(newAgent,solution);
			case BOUND:
				doBound(newAgent);
				break;
			case MODIFY:
				doModify(newAgent);
				break;
			}
		}
		return listOut;
	}

	private List<IContactMapAbstractAgent> doBreak(
			IContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution) {
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		// TODO BRK
		
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
		
//		listOut.addAll(breakLinkedSite(newSite, solution));
//		newSite.getLinkState().setFreeLinkState();
		return listOut;
	}

	private void doBound(IContactMapAbstractAgent newAgent) {
		// TODO BND
	}

	private void doModify(IContactMapAbstractAgent newAgent) {
		
		//TODO MOD
//		newSite.getInternalState().setNameId(
//				toAgent.getInternalState().getNameId());
	}

	private List<IContactMapAbstractAgent> doDelete(
			IContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution) {
		// TODO DELL
		
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
//		IContactMapAbstractAgent agent = solution.getAbstractAgentMapOld().get(newSite.getAgentLink().getNameId());
//		if(agent == null)
//			return listOut;
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
