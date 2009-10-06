package com.plectix.simulator.component.complex.contactmap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.component.complex.abstracting.AbstractSite;
import com.plectix.simulator.util.NameDictionary;

public final class ContactMapChangedSite {
	// TODO need to be implement one interface with ChangedSite
	private AbstractSite site = null;
	private boolean hasLinkState;
	private boolean hasInternalState;
	private final List<Integer> usedRulesIds = new ArrayList<Integer>();

	public ContactMapChangedSite(AbstractSite site) {
		this.site = site;
		setInternalState(site);
		setLinkState(site);
	}
	
	public final boolean hasLinkState() {
		return hasLinkState;
	}

	public final boolean hasInternalState() {
		return hasInternalState;
	}

	public final void setLinkState(AbstractSite site) {
		if (this.hasLinkState)
			return;
		if (!NameDictionary.isDefaultSiteName(site.getLinkState().getConnectedSiteName()))
			this.hasLinkState = true;
		else
			this.hasLinkState = false;
	}

	public final void setInternalState(AbstractSite site) {
		if(this.hasInternalState==true)
			return;
		if (!site.getInternalState().hasDefaultName())
			this.hasInternalState = true;
		else
			this.hasInternalState = false;
	}


	public final List<Integer> getUsedRuleIDs() {
		return usedRulesIds;
	}

	public final AbstractSite getSite() {
		return site;
	}

	public final void addRules(int ruleId) {
		if (!usedRulesIds.contains(ruleId))
			usedRulesIds.add(ruleId);
	}
}
