package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;

public class CAgent implements IAgent {
	private String name;
	private Integer dbId;
	private List<CSite> listSite = new ArrayList<CSite>();

	private static int staticId = 0;

	public CAgent(String name) {
		this.name = name;
		dbId = staticId++;
	}

	@Override
	public void addSite(CSite site) {
		if ((site != null) && (!listSite.contains(site))
				&& (findSite(site) == null)) {
			listSite.add(site);
			((CSite) site).setAgentLink(this);
		}
	}

	private CSite findSite(CSite site) {
		if (site == null)
			return null;
		for (CSite fSite : listSite)
			if (fSite.getName().equalsIgnoreCase(site.getName()))
				return fSite;
		return null;
	}

	@Override
	public Integer getDBId() {
		return dbId;
	}

	@Override
	public List<String> getInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IInternalState getSiteInternalState(ISite internal_state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILinkState getSiteLinkState(ISite site) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CSite> getSites() {
		return listSite;
	}

	@Override
	public void setSiteInternalState(ISite site, IInternalState internal_state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSiteLinkState(ISite site, ILinkState link_state) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CAgent))
			return false;
		CAgent agent = (CAgent) obj;
		if (!name.equals(agent.name))
			return false;
		return listSite.equals(agent.listSite);
	}

	public void setDbId(int index) {
		dbId = index;
	}
}
