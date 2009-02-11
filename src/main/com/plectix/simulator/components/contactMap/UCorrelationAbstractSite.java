package com.plectix.simulator.components.contactMap;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

public class UCorrelationAbstractSite {
	private CActionType type;
	private IContactMapAbstractSite fromSite;
	private IContactMapAbstractSite toSite;
	
	public UCorrelationAbstractSite(IContactMapAbstractSite fromSite,IContactMapAbstractSite toSite){
		this.fromSite = fromSite;
		this.toSite = toSite;
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
	
}
