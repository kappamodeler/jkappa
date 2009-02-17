/**
 * 
 */
package com.plectix.simulator.components.stories;

import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;

final class SitesFromRules {
	private NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;

	public NetworkNotationMode getInternalStateMode() {
		return internalStateMode;
	}

	private NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;

	public NetworkNotationMode getLinkStateMode() {
		return linkStateMode;
	}

	public final SitesFromRules clone(){
		SitesFromRules sfr = new SitesFromRules();
		sfr.internalStateMode = internalStateMode;
		sfr.linkStateMode = linkStateMode;
		sfr.linkAgentNameID = linkAgentNameID;
		return sfr;
	} 

	private int linkAgentNameID;

	public SitesFromRules(NetworkNotationMode internalStateMode,
			NetworkNotationMode linkStateMode, int linkAgentNameID) {
		this.internalStateMode = internalStateMode;
		this.linkStateMode = linkStateMode;
		this.linkAgentNameID = linkAgentNameID;
	}

	public SitesFromRules() {
	}

	public final void setInternalStateMode(
			NetworkNotationMode internalStateMode, int linkAgentNameID) {
		this.internalStateMode = internalStateMode;
		this.linkAgentNameID = linkAgentNameID;
	}

	public final void setLinkStateMode(
			NetworkNotationMode linkStateMode, int linkAgentNameID) {
		this.linkStateMode = linkStateMode;
	}

	public final boolean isCausing(SitesFromRules sfr, boolean isLink) {
		if (isLink) {
			if (isCausing(this.linkStateMode, sfr.linkStateMode))
				return true;
		} else if (isCausing(this.internalStateMode,
				sfr.internalStateMode))
			return true;

		return false;
	}

	public final boolean isCausing(NetworkNotationMode mode,
			NetworkNotationMode sfrMode) {
		if (mode == NetworkNotationMode.TEST_OR_MODIFY
				&& sfrMode == NetworkNotationMode.TEST_OR_MODIFY)
			return true;
		if (mode == NetworkNotationMode.TEST_OR_MODIFY
				&& sfrMode == NetworkNotationMode.TEST)
			return true;
		if (mode == NetworkNotationMode.MODIFY
				&& sfrMode == NetworkNotationMode.TEST)
			return true;

		return false;
	}
}