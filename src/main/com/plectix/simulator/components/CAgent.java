package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;

public class CAgent implements IAgent{
	private String name;
	private Long dbId;
	private List<ISite> listSite=new ArrayList<ISite>(1);
	
	public CAgent(String name){
		this.name=name;
	}

	@Override
	public void addSite(ISite site){
		if((site != null) && ( !listSite.contains(site) )){
			listSite.add(site);
			((CSite)site).setAgentLink(this);
		}
	}
	
	@Override
	public Long getDBId() {
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
	public List<ISite> getSites() {
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
	public IAgent cloneAgent() {
		CAgent agent=new CAgent(this.getName());
		for(ISite site:listSite){
			CSite siteAdd = new CSite(site.getName());
		}
		
		return agent;
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

}
