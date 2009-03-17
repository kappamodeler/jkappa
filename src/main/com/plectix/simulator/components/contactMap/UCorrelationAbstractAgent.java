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
		atomicActions = new ArrayList<CActionType>();
		if(toAgent == null){
			fromAgent.shouldAdd();
			atomicActions.add(CActionType.DELETE);
			return;
		}
		Iterator<Integer> iterator = fromAgent.getSitesMap().keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
				
			IContactMapAbstractSite siteFrom = fromAgent.getSitesMap().get(key);
			IContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);
			
			if(siteFrom.getInternalState().getNameId() != siteTo.getInternalState().getNameId()){
				fromAgent.shouldAdd();
				toAgent.shouldAdd();
				atomicActions.add(CActionType.MODIFY);
			}
			
			CContactMapLinkState lsFrom = siteFrom.getLinkState();
			CContactMapLinkState lsTo = siteTo.getLinkState();
			
			if(lsFrom.getAgentNameID()!=lsTo.getAgentNameID() || (lsFrom.getLinkSiteNameID()!= lsTo.getLinkSiteNameID()) ){
				fromAgent.shouldAdd();
				toAgent.shouldAdd();
				findBreakBound(siteFrom,siteTo);
			}
		}
	}

	private void findBreakBound(IContactMapAbstractSite siteFrom, IContactMapAbstractSite siteTo) {
		CContactMapLinkState lsFrom = siteFrom.getLinkState();
		CContactMapLinkState lsTo = siteTo.getLinkState();
		
		
//		CContactMapLinkState linkStateFrom = fromAgent.getLinkState();
//		CContactMapLinkState linkStateTo = toAgent.getLinkState();
		if (lsFrom.getAgentNameID() != CSite.NO_INDEX
				&& lsTo.getAgentNameID() == CSite.NO_INDEX) {
			atomicActions.add(CActionType.BREAK);
			return;
		}
		if (lsFrom.getStatusLinkRank() == CLinkRank.SEMI_LINK
				&& lsTo.getStatusLinkRank() == CLinkRank.FREE) {
			atomicActions.add(CActionType.BREAK);
			return;
		}

		if (lsFrom.getAgentNameID() == CSite.NO_INDEX
				&& lsTo.getAgentNameID() != CSite.NO_INDEX) {
			atomicActions.add(CActionType.BOUND);
			return;
		}
		atomicActions.add(CActionType.BREAK);
		atomicActions.add(CActionType.BOUND);
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
		Iterator<Integer> iterator = toAgent.getSitesMap().keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			IContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);
			IContactMapAbstractSite siteNew = newAgent.getSitesMap().get(key);
			
			CContactMapLinkState lsTo = siteTo.getLinkState();
			CContactMapLinkState lsNew = siteNew.getLinkState();
			
			if(lsNew.getAgentNameID() != lsTo.getAgentNameID() || lsNew.getLinkSiteNameID() != lsTo.getLinkSiteNameID()){
				lsNew.setAgentNameID(CSite.NO_INDEX);
				lsNew.setLinkSiteNameID(CSite.NO_INDEX);
				lsNew.setStatusLink(CLinkStatus.FREE);
			}
		}
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
		Iterator<Integer> iterator = toAgent.getSitesMap().keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			IContactMapAbstractSite siteTo = toAgent.getSitesMap().get(key);
			IContactMapAbstractSite siteNew = newAgent.getSitesMap().get(key);
			
			CContactMapLinkState lsTo = siteTo.getLinkState();
			CContactMapLinkState lsNew = siteNew.getLinkState();
			
			lsNew.setAgentNameID(lsTo.getAgentNameID());
			lsNew.setLinkSiteNameID(lsTo.getLinkSiteNameID());
			lsNew.setStatusLink(CLinkStatus.BOUND);
		}
		
	}

	private void doModify(IContactMapAbstractAgent newAgent) {
		Iterator<Integer> iterator = toAgent.getSitesMap().keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			IInternalState  stateTo = toAgent.getSitesMap().get(key).getInternalState();
			if(stateTo.getNameId() == CSite.NO_INDEX)
				continue;
			IInternalState stateNew = newAgent.getSitesMap().get(key).getInternalState();
			stateNew.setNameId(stateTo.getNameId());
		}
	}

	private List<IContactMapAbstractAgent> doDelete(
			IContactMapAbstractAgent newAgent,
			CContactMapAbstractSolution solution) {
		// TODO DELL
		
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		Iterator<Integer> iterator = newAgent.getSitesMap().keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			CContactMapLinkState ls = newAgent.getSitesMap().get(key).getLinkState();
			if(ls.getAgentNameID() == CSite.NO_INDEX)
				continue;
			List<IContactMapAbstractAgent> agentlist = solution.getAgentNameIdToAgentsList().get(ls.getAgentNameID());
			if(agentlist == null || agentlist.isEmpty())
				continue;
			
			// TODO ...
			
		}
		
		
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
