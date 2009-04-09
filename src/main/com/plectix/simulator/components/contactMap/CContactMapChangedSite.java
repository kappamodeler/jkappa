package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IContactMapAbstractRule;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

public class CContactMapChangedSite {
	// TODO need to be implement one interface with ChangedSite
	private IContactMapAbstractSite site;
	public IContactMapAbstractSite getSite() {
		return site;
	}

	private boolean linkState;
	private boolean internalState;

	public boolean isLinkState() {
		return linkState;
	}

	public boolean isInternalState() {
		return internalState;
	}

	public final void setLinkState(boolean linkState) {
		this.linkState = linkState;
	}

	public final void setInternalState(boolean internalState) {
		this.internalState = internalState;
	}

	public final void setLinkState(IContactMapAbstractSite site) {
		if(this.linkState==true)
			return;
		if (site.getLinkState().getLinkSiteNameID() != CSite.NO_INDEX)
			this.linkState = true;
		else
			this.linkState = false;
	}

	public final void setInternalState(IContactMapAbstractSite site) {
		if(this.internalState==true)
			return;
		if (site.getInternalState().getNameId() != CSite.NO_INDEX)
			this.internalState = true;
		else
			this.internalState = false;
	}

	private List<Integer> usedRuleIDs;

	public List<Integer> getUsedRuleIDs() {
		return usedRuleIDs;
	}

	public CContactMapChangedSite(IContactMapAbstractSite site) {
		this.site = site;
		setInternalState(site);
		setLinkState(site);
		usedRuleIDs = new ArrayList<Integer>();
	}

	public void addRules(IContactMapAbstractRule rule) {
		if (rule != null) {
			int value = rule.getRule().getRuleID();
			if (!usedRuleIDs.contains(value))
				usedRuleIDs.add(value);
		}
	}

}
