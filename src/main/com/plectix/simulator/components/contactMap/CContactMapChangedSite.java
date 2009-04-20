package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CSite;

public class CContactMapChangedSite {
	// TODO need to be implement one interface with ChangedSite
	private CContactMapAbstractSite site;
	public CContactMapAbstractSite getSite() {
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

	public final void setLinkState(CContactMapAbstractSite site) {
		if(this.linkState==true)
			return;
		if (site.getLinkState().getLinkSiteNameID() != CSite.NO_INDEX)
			this.linkState = true;
		else
			this.linkState = false;
	}

	public final void setInternalState(CContactMapAbstractSite site) {
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

	public CContactMapChangedSite(CContactMapAbstractSite site) {
		this.site = site;
		setInternalState(site);
		setLinkState(site);
		usedRuleIDs = new ArrayList<Integer>();
	}

	public void addRules(CContactMapAbstractRule rule) {
		if (rule != null) {
			int value = rule.getRule().getRuleID();
			if (!usedRuleIDs.contains(value))
				usedRuleIDs.add(value);
		}
	}

}
