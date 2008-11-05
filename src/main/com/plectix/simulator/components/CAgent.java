package com.plectix.simulator.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISite;

public class CAgent implements IAgent {
	/**
	 * idInConnectedComponent is the unique id in ConnectedComponent id is an
	 * unique id for agent
	 */
	public static final int UNMARKED = -1;
	public static final byte ACTION_CREATE = -2;
	public static final byte EMPTY = -1;

	private int idInConnectedComponent;
	private int idInRuleSide = UNMARKED;

	private long id;

	// TODO: is this field static or not???
	public final CSite EMPTY_SITE = new CSite(CSite.NO_INDEX, this);

	private HashMap<Integer, CSite> siteMap = new HashMap<Integer, CSite>();

	public HashMap<Integer, CSite> getSiteMap() {
		return siteMap;
	}

	private int nameId;

	public int getIdInRuleSide() {
		return idInRuleSide;
	}

	public void setIdInRuleSide(int idInRuleSide) {
		this.idInRuleSide = idInRuleSide;
	}

	public CAgent(int nameId) {
		id = SimulationMain.getSimulationManager().generateNextAgentId();
		this.nameId = nameId;
	}

	public boolean isAgentHaveLinkToConnectedComponent(CConnectedComponent cc) {

		for (CSite site : siteMap.values()) {
			if (site.isConnectedComponentInLift(cc))
				return true;
		}
		return false;
	}

	/**
	 * returns linked agent of this from solution which is equal to input
	 * parameter
	 */

	public final CAgent findLinkAgent(CAgent agentFromCC, List<CSite> siteFromCC) {
		if (agentFromCC == null || siteFromCC.size()==0)
			return null;
		CAgent agent = (CAgent) this.getSite(siteFromCC.get(0).getNameId())
				.getLinkState().getSite().getAgentLink();
		for (CSite siteF : siteFromCC) {
			CAgent agent2 = (CAgent) this.getSite(siteF.getNameId())
					.getLinkState().getSite().getAgentLink();
			if (agent != agent2)
				return null;
		}
		if (agent.equals(agentFromCC))
			return agent;

		return null;
	}

	@Override
	public final void addSite(CSite site) {
		site.setAgentLink(this);
		siteMap.put(site.getNameId(), site);
	}

	@Override
	public final int getIdInConnectedComponent() {
		return idInConnectedComponent;
	}

	public final void setIdInConnectedComponent(int index) {
		idInConnectedComponent = index;
	}

	@Override
	public final List<String> getInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final String getSiteInternalState(ISite internal_state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final CLinkState getSiteLinkState(ISite site) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final Collection<CSite> getSites() {
		return siteMap.values();
	}

	@Override
	public final void setSiteInternalState(ISite site, String internal_state) {
		// TODO Auto-generated method stub

	}

	@Override
	public final void setSiteLinkState(ISite site, CLinkState link_state) {
		// TODO Auto-generated method stub

	}

	public final long getId() {
		return id;
	}

	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof CAgent))
			return false;
		CAgent agent = (CAgent) obj;
		if (nameId != agent.getNameId())
			return false;
		// return siteMap.equals(agent.siteMap);
		return true;
	}

	public final CSite getSite(int siteNameId) {
		return siteMap.get(siteNameId);
	}

	public final int getNameId() {
		return nameId;
	}

	public final String getName() {
		return SimulationMain.getSimulationManager().getNameDictionary()
				.getName(nameId);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(getName() + "(");
		boolean first = true;
		for (CSite site : siteMap.values()) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(site.getName());
			if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
				sb.append("~" + site.getInternalState().getName());
			}
			if (site.getLinkIndex() != -1) {
				sb.append("!" + site.getLinkIndex());
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
