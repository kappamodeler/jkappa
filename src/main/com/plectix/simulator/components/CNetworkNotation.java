package com.plectix.simulator.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.interfaces.*;

public class CNetworkNotation implements INetworkNotation {

	private int step;

	private IRule rule;

	HashMap<Long, AgentSites> changedAgentsFromSolution;

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

	public final void checkLinkForNetworkNotation(int index,
			ISite site) {
		if (site.getLinkState().getSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, -1, -1),
					index);
		else
			this.addToAgents(site, new CStoriesSiteStates(index,
					((CAgent) site.getLinkState().getSite().getAgentLink())
							.getHash(), ((CSite) site.getLinkState()
							.getSite()).getNameId()), index);

	}

	public final void checkLinkForNetworkNotationDel(int index,
			ISite site) {
		if (site.getLinkState().getSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), -1, -1), index);
		else
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), ((CAgent) site
					.getLinkState().getSite().getAgentLink()).getHash(),
					((CSite) site.getLinkState().getSite()).getNameId()),
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

	public IRule getRule() {
		return rule;
	}

	public CNetworkNotation(int step, IRule rule) {
		this.step = step;
		this.rule = rule;
		this.changedAgentsFromSolution = new HashMap<Long, AgentSites>();
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
