package com.plectix.simulator.components.contactMap;

import java.util.List;
import java.util.Map;

import com.plectix.simulator.interfaces.IContactMapAbstractSite;

public class CContactMapAbstractAction {
	private Map<Integer, List<IContactMapAbstractSite>> siteMap;
	private CContactMapAbstractRule rule;
	
	public CContactMapAbstractAction(CContactMapAbstractRule rule){
		this.rule = rule;
	}

	
	private void createAtomicActions(){
		// TODO createAtomicActions
	}
}
