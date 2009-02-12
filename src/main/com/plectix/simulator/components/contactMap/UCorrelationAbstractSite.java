package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

public class UCorrelationAbstractSite {
	private CActionType type;
	private ECorrelationType correlationType;
	private IContactMapAbstractSite fromSite;
	private IContactMapAbstractSite toSite;
	private List<CActionType> atomicActions;
	
	public UCorrelationAbstractSite(IContactMapAbstractSite fromSite,IContactMapAbstractSite toSite,ECorrelationType correlationType){
		this.fromSite = fromSite;
		this.toSite = toSite;
		this.correlationType = correlationType;
		if(correlationType == ECorrelationType.CORRELATION_LHS_AND_RHS)
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

	public static List<UCorrelationAbstractSite> createCorrelationSites(List<IContactMapAbstractSite> fromSites,List<IContactMapAbstractSite> toSites,ECorrelationType correlationType){
		List<UCorrelationAbstractSite> list = new ArrayList<UCorrelationAbstractSite>();
		int i = 0;
		for(IContactMapAbstractSite s : fromSites){
			list.add(new UCorrelationAbstractSite(s,toSites.get(i++),correlationType));
		}
		return list;
	}
	
	public void initAtomicActionList(){
		atomicActions = new ArrayList<CActionType>();
		if(correlationType != ECorrelationType.CORRELATION_LHS_AND_RHS)
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
	private void findBreakBound(){
		CContactMapLinkState linkStateFrom = fromSite.getLinkState();
		CContactMapLinkState linkStateTo = toSite.getLinkState();
		if(linkStateFrom.getAgentNameID()!=CSite.NO_INDEX && linkStateTo.getAgentNameID()==CSite.NO_INDEX){
			atomicActions.add(CActionType.BREAK);
			return;
		}
		if(linkStateFrom.getAgentNameID()==CSite.NO_INDEX && linkStateTo.getAgentNameID()!=CSite.NO_INDEX){
			atomicActions.add(CActionType.BOUND);
			return;
		}
		atomicActions.add(CActionType.BREAK);
		atomicActions.add(CActionType.BOUND);
	}
}
