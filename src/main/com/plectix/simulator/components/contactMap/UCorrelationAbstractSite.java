package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

public class UCorrelationAbstractSite {
	private CActionType type;
	private ECorrelationType correlationType;
	private IContactMapAbstractSite fromSite;
	private IContactMapAbstractSite toSite;
	
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
}
