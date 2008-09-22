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
	private int idInConnectedComponent;
	private long id;

	private HashMap<Integer, CSite> siteMap = new HashMap<Integer, CSite>();
	private int nameId;

	public CAgent(int nameId) {
		id = SimulationMain.getSimulationManager().generateNextAgentId();
		this.nameId = nameId;
	}

	// TODO: Write documentation for this method. 
	public final CAgent findLinkAgent(CAgent agent) {
		if (agent == null)
			return null;
		for (CSite site : siteMap.values()) {
			CSite aSite = (CSite) site.getLinkState().getSite();
			if (aSite != null) {
				if (agent.equals(aSite.getAgentLink()))
					return aSite.getAgentLink();
			}
		}
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
		if (! (nameId == agent.nameId))
			return false;
		return siteMap.equals(agent.siteMap);
	}

	public final CSite getSite(int siteNameId) {
		return siteMap.get(siteNameId);
	}

	public final int getNameId() {
		return nameId;
	}

}
