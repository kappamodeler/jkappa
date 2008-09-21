package com.plectix.simulator.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.SimulatorManager;

public class CAgent implements IAgent {
	/**
	 * idInConnectedComponent is the unique id in ConnectedComponent id is an
	 * unique id for agent
	 */
	private String name;
	private int idInConnectedComponent;
	private long id;

	private HashMap<String, CSite> siteMap = new HashMap<String, CSite>();

	public CAgent(String name) {
		this.name = name;
		id = SimulatorManager.getInstance().generateNextAgentId();
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
		siteMap.put(site.getName(), site);
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
	public final String getName() {
		return name;
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
	public boolean equals(Object obj) {
		if (!(obj instanceof CAgent))
			return false;
		CAgent agent = (CAgent) obj;
		if (!name.equals(agent.name))
			return false;
		return siteMap.equals(agent.siteMap);
	}

	public CSite getSite(String siteName) {
		return siteMap.get(siteName);
	}

}
