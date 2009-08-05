package com.plectix.simulator.components.complex.contactMap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;

public class CContactMapChangedSite {
	// TODO need to be implement one interface with ChangedSite
	private CAbstractSite site = null;
	public CAbstractSite getSite() {
		return site;
	}

	private boolean linkState;
	private boolean internalState;
	private List<Integer> usedRuleIDs;

	public boolean isLinkState() {
		return linkState;
	}

	public boolean isInternalState() {
		return internalState;
	}

	public final void setLinkState(CAbstractSite site) {
		if(this.linkState==true)
			return;
		if (site.getLinkState().getLinkSiteNameID() != CSite.NO_INDEX)
			this.linkState = true;
		else
			this.linkState = false;
	}

	public final void setInternalState(CAbstractSite site) {
		if(this.internalState==true)
			return;
		if (site.getInternalState().getNameId() != CSite.NO_INDEX)
			this.internalState = true;
		else
			this.internalState = false;
	}


	public List<Integer> getUsedRuleIDs() {
		return usedRuleIDs;
	}

	public CContactMapChangedSite(CAbstractSite site) {
		this.site = site;
		setInternalState(site);
		setLinkState(site);
		usedRuleIDs = new ArrayList<Integer>();
	}
	

	
	public void addRules(int ruleId) {
		if (!usedRuleIDs.contains(ruleId))
				usedRuleIDs.add(ruleId);
		
	}

}
