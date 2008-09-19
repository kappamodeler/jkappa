package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.SimulatorManager;

public class CAgent implements IAgent {
	/**
	 * idInConnectedComponent is the unique id in ConnectedComponent
	 * id is an unique id for agent
	 */
	private String name;
	private int idInConnectedComponent;
	private long id;

	private HashMap<String,CSite> siteMap = new HashMap<String, CSite>();
	

	public CAgent(String name) {
		this.name = name;
		id = SimulatorManager.getInstance().generateNextAgenId();
	}

	@Override
	public void addSite(CSite site) {
		site.setAgentLink(this);
		siteMap.put(site.getName(),site);
	}

	@Override
	public int getIdInConnectedComponent() {
		return idInConnectedComponent;
	}

	public void setIdInConnectedComponent(int index) {
		idInConnectedComponent = index;
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
	public String getSiteInternalState(ISite internal_state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CLinkState getSiteLinkState(ISite site) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<CSite> getSites() {
		return siteMap.values();
	}

	@Override
	public void setSiteInternalState(ISite site, String internal_state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSiteLinkState(ISite site, CLinkState link_state) {
		// TODO Auto-generated method stub

	}

	public long getId() {
		return id;
	}
	
//	public IAgent cloneAgent() {
//		CAgent agent=new CAgent(this.getName());
//		for(ISite site:listSite){
//			CSite siteAdd = new CSite(site.getName());
//		}
//		
//		return agent;
//	}
	
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
