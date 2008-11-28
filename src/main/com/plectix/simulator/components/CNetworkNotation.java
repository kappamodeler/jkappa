package com.plectix.simulator.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.interfaces.*;

public class CNetworkNotation implements INetworkNotation {
	public static final byte MODE_TEST = 0;
	public static final byte MODE_TEST_OR_MODIFY = 1;
	public static final byte MODE_MODIFY = 2;
	public static final byte MODE_NONE = -1;

	private int step;

	private IRule rule;

	HashMap<Long, AgentSites> changedAgentsFromSolution;

	HashMap<Long, AgentSitesFromRules> usedAgentsFromRules;

	class AgentSitesFromRules {
		HashMap<Integer, SitesFromRules> sites;

		byte mode;

		public AgentSitesFromRules(byte mode) {
			this.mode = mode;
			sites = new HashMap<Integer, SitesFromRules>();
		}

		class SitesFromRules {
			byte internalStateMode = MODE_NONE;
			byte linkStateMode = MODE_NONE;

			public SitesFromRules(byte internalStateMode, byte linkStateMode) {
				this.internalStateMode = internalStateMode;
				this.linkStateMode = linkStateMode;
			}

			public SitesFromRules() {
			}

			public void setInternalStateMode(byte internalStateMode) {
				this.internalStateMode = internalStateMode;
			}

			public void setLinkStateMode(byte linkStateMode) {
				this.linkStateMode = linkStateMode;
			}

			public boolean isCausing(SitesFromRules sfr, boolean isLink) {
				if (isLink)
					if (isCausing(this.linkStateMode, sfr.linkStateMode))
						return true;
					else if (isCausing(this.internalStateMode,
							sfr.internalStateMode))
						return true;

				return false;
			}

			public boolean isCausing(byte mode, byte sfrMode) {
				if (mode == MODE_TEST_OR_MODIFY
						&& sfrMode == MODE_TEST_OR_MODIFY)
					return true;
				if (mode == MODE_TEST_OR_MODIFY && sfrMode == MODE_TEST)
					return true;
				if (mode == MODE_MODIFY && sfrMode == MODE_TEST)
					return true;

				return false;
			}
		}

		public void addToSitesFromRules(int idSite, byte internalStateMode,
				byte linkStateMode) {
			SitesFromRules sFR = sites.get(idSite);
			if (sFR == null) {
				sFR = new SitesFromRules();
				sites.put(idSite, sFR);
			}
			if (internalStateMode != MODE_NONE)
				sFR.setInternalStateMode(internalStateMode);
			if (linkStateMode != MODE_NONE)
				sFR.setLinkStateMode(linkStateMode);
		}

		public void addFixedSitesFromRules(int idSite, byte internalStateMode,
				byte linkStateMode) {
			SitesFromRules sFR = sites.get(idSite);
			if (sFR == null)

				if (internalStateMode != MODE_NONE)
					sFR.setInternalStateMode(internalStateMode);
			if (linkStateMode != MODE_NONE)
				sFR.setLinkStateMode(linkStateMode);
			sites.put(idSite, sFR);
		}
	}

	class AgentSites {
		HashMap<Integer, IStoriesSiteStates> sites;

		public AgentSites() {
			sites = new HashMap<Integer, IStoriesSiteStates>();
		}

		public void addToSites(int idSite, IStoriesSiteStates siteStates,
				int index) {
			IStoriesSiteStates ss = sites.get(idSite);
			if (ss == null)
				sites.put(idSite, siteStates);
			else
				ss.addInformation(index, siteStates);
		}

		public void addToSites(int nameId, CStoriesSiteStates siteStates,
				int index) {
			// TODO Auto-generated method stub

		}
	}

	public final void checkLinkForNetworkNotation(int index, ISite site) {
		if (site.getLinkState().getSite() == null)
			this
					.addToAgents(site, new CStoriesSiteStates(index, -1, -1),
							index);
		else
			this
					.addToAgents(site, new CStoriesSiteStates(index,
							((CAgent) site.getLinkState().getSite()
									.getAgentLink()).getHash(), ((CSite) site
									.getLinkState().getSite()).getNameId()),
							index);

	}

	public final void checkLinkForNetworkNotationDel(int index, ISite site) {
		if (site.getLinkState().getSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), -1, -1), index);
		else
			this
					.addToAgents(site, new CStoriesSiteStates(index, site
							.getInternalState().getNameId(),
							((CAgent) site.getLinkState().getSite()
									.getAgentLink()).getHash(), ((CSite) site
									.getLinkState().getSite()).getNameId()),
							index);
	}

	public void addToAgents(ISite site, IStoriesSiteStates siteStates, int index) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSites as = changedAgentsFromSolution.get(key);
			if (as == null) {
				as = new AgentSites();
				changedAgentsFromSolution.put(key, as);
			}
			as.addToSites(site.getNameId(), siteStates, index);
		}
	}

	public void addToAgentsFromRules(ISite site, byte agentMode,
			byte internalStateMode, byte linkStateMode) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(key);
			if (aSFR == null) {
				aSFR = new AgentSitesFromRules(agentMode);
				usedAgentsFromRules.put(key, aSFR);
			}
			aSFR.addToSitesFromRules(site.getNameId(), internalStateMode,
					linkStateMode);
		}
	}

	public void addFixedSitesFromRules(ISite site, byte agentMode,
			boolean internalState, boolean linkState) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(key);
			if (aSFR == null) {
				aSFR = new AgentSitesFromRules(agentMode);
				usedAgentsFromRules.put(key, aSFR);
			}
			byte internalStateMode = MODE_NONE;
			byte linkStateMode = MODE_NONE;

			if (internalState == true)
				internalStateMode = MODE_TEST;
			if (linkState == true)
				linkStateMode = MODE_TEST;

			aSFR.addToSitesFromRules(site.getNameId(), internalStateMode,
					linkStateMode);
		}
	}

	public IRule getRule() {
		return rule;
	}

	public CNetworkNotation(int step, IRule rule) {
		this.step = step;
		this.rule = rule;
		this.changedAgentsFromSolution = new HashMap<Long, AgentSites>();
		this.usedAgentsFromRules = new HashMap<Long, AgentSitesFromRules>();
	}

	public final boolean isOpposite(List<CNetworkNotation> networkNotationList) {
		for (int i = networkNotationList.size() - 1; i >= 0; i--) {
			CNetworkNotation nn = networkNotationList.get(i);
			switch (isIntersects(nn)) {
			case HAS_FULL_INTERSECTION:
				networkNotationList.remove(i);
				return false;
			case HAS_PART_INTERSECTION:
				return true;
			}
		}
		return true;
	}

	public final static byte HAS_FULL_INTERSECTION = 2;
	public final static byte HAS_PART_INTERSECTION = 1;
	public final static byte HAS_NO_INTERSECTION = 0;

	public final byte isIntersects(CNetworkNotation nn) {
		Iterator<Long> iterator = this.changedAgentsFromSolution.keySet()
				.iterator();
		int counter = 0;
		int fullCounter = 0;

		while (iterator.hasNext()) {
			Long key = iterator.next();

			if (nn.changedAgentsFromSolution.containsKey(key)) {
				if (checkSites(key, nn) == HAS_FULL_INTERSECTION)
					fullCounter++;
				counter++;
			}
		}

		if ((fullCounter == this.changedAgentsFromSolution.size())
				&& (this.changedAgentsFromSolution.size() == nn.changedAgentsFromSolution
						.size()))
			return HAS_FULL_INTERSECTION;

		if (counter > 0)
			return HAS_PART_INTERSECTION;

		return HAS_NO_INTERSECTION;
	}

	private final byte checkSites(long key, CNetworkNotation nn) {
		Iterator<Integer> iterator = this.changedAgentsFromSolution.get(key).sites
				.keySet().iterator();
		int counter = 0;
		int fullCounter = 0;

		while (iterator.hasNext()) {
			Integer keySite = iterator.next();
			if (nn.changedAgentsFromSolution.get(key).sites
					.containsKey(keySite)) {
				counter++;

				if (CStoriesSiteStates.isEqual(this.changedAgentsFromSolution
						.get(key).sites.get(keySite).getCurrentState(),
						nn.changedAgentsFromSolution.get(key).sites
								.get(keySite).getLastState())) {
					fullCounter++;
				}
			}
		}

		if ((fullCounter == this.changedAgentsFromSolution.get(key).sites
				.size())
				&& (this.changedAgentsFromSolution.get(key).sites.size() == nn.changedAgentsFromSolution
						.get(key).sites.size()))
			return HAS_FULL_INTERSECTION;

		if (counter > 0)
			return HAS_PART_INTERSECTION;

		return HAS_NO_INTERSECTION;
	}
}
