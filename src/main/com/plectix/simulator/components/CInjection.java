package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IInjection;

public class CInjection implements IInjection {

	private List<CAgentLink> agentLinkList;

	private List<CSite> sitesList = new ArrayList<CSite>();

	private CConnectedComponent connectedComponent;

	public CInjection() {
	}

	public CInjection(CConnectedComponent connectedComponent,
			List<CSite> sitesList, List<CAgentLink> agentLinkList) {
		this.connectedComponent = connectedComponent;
		this.sitesList = sitesList;
		this.agentLinkList = agentLinkList;
	}

	public List<CAgentLink> getAgentLinkList() {
		return agentLinkList;
	}

	public List<CSite> getSiteList() {
		return sitesList;
	}

	public void setSiteList(List<CSite> siteList) {
		this.sitesList = siteList;
	}

	public CConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}

	@Override
	public List<IAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAgents(List<IAgent> agents) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public final boolean equals(Object obj) {
	// if (!(obj instanceof CInjection))
	// return false;
	// CInjection injection = (CInjection) obj;
	// if (! (nameId == agent.nameId))
	// return false;
	// // return siteMap.equals(agent.siteMap);
	// return true;
	// }

}
