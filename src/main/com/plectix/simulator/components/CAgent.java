package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISite;

public final class CAgent implements IAgent {
	/**
	 * idInConnectedComponent is the unique id in ConnectedComponent id is an
	 * unique id for agent
	 */
	public static final int UNMARKED = -1;
	public static final byte ACTION_CREATE = -2;
	public static final byte EMPTY = -1;
	
	private int idInConnectedComponent;
	private int idInRuleSide = UNMARKED;
	private int nameId = -1;
	private long id = -1;
	
	// TODO: is this field static or not???
	private final ISite myEmptySite = new CSite(CSite.NO_INDEX, this);
	private TreeMap<Integer, ISite> siteMap = new TreeMap<Integer, ISite>();

	public CAgent(int nameId) {
		id = SimulationMain.getSimulationManager().generateNextAgentId();
		this.nameId = nameId;
	}

	public ISite getEmptySite() {
		return myEmptySite;
	}
	
	public Map<Integer, ISite> getSiteMap() {
		return Collections.unmodifiableMap(siteMap);
	}

	public int getIdInRuleSide() {
		return idInRuleSide;
	}

	public void setIdInRuleSide(int idInRuleSide) {
		this.idInRuleSide = idInRuleSide;
	}

	public boolean isAgentHaveLinkToConnectedComponent(IConnectedComponent cc) {
		for (ISite site : siteMap.values()) {
			if (site.getAgentLink().getEmptySite().isConnectedComponentInLift(cc))
				return true;
			if (site.isConnectedComponentInLift(cc))
				return true;
		}
		return false;
	}

	public boolean isAgentHaveLinkToConnectedComponent(IConnectedComponent cc,
			IInjection injection) {
		if (checkSites(this.getEmptySite(), injection, cc))
			return true;
		for (ISite site : siteMap.values()) {
			if (checkSites(site.getAgentLink().getEmptySite(), injection, cc))
				return true;
			if (checkSites(site, injection, cc))
				return true;
		}
		return false;
	}

	private boolean checkSites(ISite site, IInjection injection,
			IConnectedComponent cc) {
		List<IInjection> sitesInjections = site.getInjectionFromLift(cc);
		if (sitesInjections.size() != 0) {
			if (compareInjectedLists(sitesInjections, injection))
				return true;
		}
		return false;
	}

	private boolean isSiteInList(List<ISite> sitesList, ISite site) {
		for (ISite siteList : sitesList) {
			if (site == siteList
					&& site.getInternalState().equals(
							siteList.getInternalState())) {
				return true;
			}
		}
		return false;
	}

	private boolean compareInjectedLists(List<IInjection> list, IInjection two) {
		int counter = 0;
		for (IInjection one : list) {
			if (one.getSiteList().size() == two.getSiteList().size()) {
				for (ISite siteOne : one.getSiteList()) {
					if (isSiteInList(two.getSiteList(), siteOne))
						counter++;
					else {
						counter = 0;
						break;
					}
				}
				if (counter == one.getSiteList().size())
					return true;
				counter = 0;
			}
		}
		return false;
	}

	/**
	 * returns linked agent of this from solution which is equal to input
	 * parameter
	 */

	public final IAgent findLinkAgent(IAgent agentFromCC, List<ISite> siteFromCC) {
		if (agentFromCC == null || siteFromCC.size() == 0)
			return null;
		IAgent agent = (CAgent) this.getSite(siteFromCC.get(0).getNameId())
				.getLinkState().getSite().getAgentLink();
		for (ISite siteF : siteFromCC) {
			IAgent agent2 = (CAgent) this.getSite(siteF.getNameId())
					.getLinkState().getSite().getAgentLink();
			if (agent != agent2)
				return null;
		}
		if (agent.equals(agentFromCC))
			return agent;

		return null;
	}

	public final void addSite(ISite site) {
		site.setAgentLink(this);
		siteMap.put(site.getNameId(), site);
	}

	public final int getIdInConnectedComponent() {
		return idInConnectedComponent;
	}

	public final void setIdInConnectedComponent(int index) {
		idInConnectedComponent = index;
	}

	public final Collection<ISite> getSites() {
		return Collections.unmodifiableCollection(siteMap.values());
	}

	public final long getId() {
		return id;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CAgent)) {
			return false;
		}

		CAgent agent = (CAgent) obj;

		if (nameId != agent.getNameId()) {
			return false;
		}
		return true;
	}

	public final ISite getSite(int siteNameId) {
		return siteMap.get(siteNameId);
	}

	public final int getNameId() {
		return nameId;
	}

	public final String getName() {
		return SimulationMain.getSimulationManager().getNameDictionary()
				.getName(nameId);
	}

	public long getHash() {
		return id;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getName() + "(");
		boolean first = true;
		for (ISite site : siteMap.values()) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(site.getName());
			if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
				sb.append("~" + site.getInternalState().getName());
			}
			if (site.getLinkState().getStatusLinkRank() == CLinkState.RANK_SEMI_LINK) {
				sb.append("!_");
			} else if (site.getLinkIndex() != -1) {
				sb.append("!" + site.getLinkIndex());
			} else if (site.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_WILDCARD) {
				sb.append("?");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public int compareTo(IAgent o) {
		return idInRuleSide - o.getIdInRuleSide();
	}
}
