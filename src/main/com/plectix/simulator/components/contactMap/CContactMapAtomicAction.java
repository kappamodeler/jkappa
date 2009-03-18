package com.plectix.simulator.components.contactMap;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

class CContactMapAtomicAction {
	private IContactMapAbstractSite siteFrom;
	private IContactMapAbstractSite siteTo;
	private CActionType type;

	public CContactMapAtomicAction(CActionType type,
			IContactMapAbstractSite siteFrom, IContactMapAbstractSite siteTo) {
		this.type = type;
		this.siteFrom = siteFrom;
		this.siteTo = siteTo;
	}

	public IContactMapAbstractSite getSiteFrom() {
		return siteFrom;
	}

	public IContactMapAbstractSite getSiteTo() {
		return siteTo;
	}

	public CActionType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
}
