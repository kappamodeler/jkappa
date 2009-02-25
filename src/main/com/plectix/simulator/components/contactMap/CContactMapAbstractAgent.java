package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAbstractAgent;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CContactMapAbstractAgent implements IContactMapAbstractAgent {
	private Map<Integer, IContactMapAbstractSite> sitesMap;
	private long id = -1;
	private int nameID = -1;
	private final IContactMapAbstractSite myEmptySite;

	public void setSitesMap(Map<Integer, IContactMapAbstractSite> sitesMap) {
		this.sitesMap = sitesMap;
	}

	@Override
	public String toString() {
		String st = getName();
		return st;
	}

	public IContactMapAbstractSite getEmptySite() {
		return myEmptySite;
	}

	public final Map<Integer, IContactMapAbstractSite> getSitesMap() {
		return this.sitesMap;
	}

	public CContactMapAbstractAgent(IAgent agent) {
		this.nameID = agent.getNameId();
		this.myEmptySite = new CContactMapAbstractSite(this);
		this.sitesMap = new HashMap<Integer, IContactMapAbstractSite>();
	}

	public CContactMapAbstractAgent(int nameID) {
		this.nameID = nameID;
		this.myEmptySite = new CContactMapAbstractSite(this);
		this.sitesMap = new HashMap<Integer, IContactMapAbstractSite>();
	}

	public CContactMapAbstractAgent(IContactMapAbstractAgent agentLink) {
		this.myEmptySite = new CContactMapAbstractSite(this);
		this.nameID = agentLink.getNameId();		 
		
		Iterator<Integer> iterator = agentLink.getSitesMap().keySet().iterator();
		while (iterator.hasNext()){
			int key = iterator.next();
			IContactMapAbstractSite abstractSite = agentLink.getSitesMap().get(key);
			IContactMapAbstractSite newSite = abstractSite.clone();
			newSite.setAgentLink(this);
			sitesMap.put(key, newSite);			
		}
	}

	public final IContactMapAbstractSite getSite(int nameID){
        return this.sitesMap.get(nameID);
   }

	public final void addSites(IAgent agent,
			Map<Integer, IContactMapAbstractAgent> agentNameIdToAgent) {

		IContactMapAbstractAgent abstractModelAgent = agentNameIdToAgent.get(agent.getNameId());
		
		for (ISite site : agent.getSites()) {
			Integer key = site.getNameId();
			IContactMapAbstractSite abstractSite = new CContactMapAbstractSite(site, this);
				sitesMap.put(key, abstractSite);
		}

		Iterator<Integer> iterator = abstractModelAgent.getSitesMap().keySet().iterator();
		while (iterator.hasNext()){
			int key = iterator.next();
			IContactMapAbstractSite abstractSite = this.sitesMap.get(key);
			if(abstractSite==null){
				IContactMapAbstractSite modelSite = abstractModelAgent.getSitesMap().get(key);
				this.sitesMap.put(key, modelSite);
			}
		}
	}

	public final void addModelSite(IContactMapAbstractSite siteToAdd) {
		int nameID = siteToAdd.getNameId();
		IContactMapAbstractSite site = this.sitesMap.get(nameID);
		if (site == null) {
			this.sitesMap.put(nameID, siteToAdd);
		}
	}

	public final boolean equalz(IAbstractAgent obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof CContactMapAbstractAgent)) {
			return false;
		}

		CContactMapAbstractAgent agent = (CContactMapAbstractAgent) obj;

		if (nameID != agent.nameID)
			return false;

		if (this.sitesMap.size() != agent.getSitesMap().size())
			return false;

		if (!isEqualSitesMaps(sitesMap, agent.getSitesMap()))
			return false;
		return true;
	}

	private boolean isEqualSitesMaps(
			Map<Integer, IContactMapAbstractSite> sitesMap1,
			Map<Integer, IContactMapAbstractSite> sitesMap2) {

		Iterator<Integer> iterator = sitesMap1.keySet().iterator();
		while (iterator.hasNext()) {
			int siteKey = iterator.next();
			IContactMapAbstractSite site1 = sitesMap1.get(siteKey);
			IContactMapAbstractSite site2 = sitesMap2.get(siteKey);
			if (site2 == null)
				return false;
			if (!site1.equalz(site2))
				return false;
		}

		return true;
	}

	public long getHash() {
		return id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		if (nameID == -1)
			return "-1";
		return ThreadLocalData.getNameDictionary().getName(nameID);
	}

	public int getNameId() {
		return nameID;
	}

	public void print() {
		System.out.println("agent = " + this.toString());
		Iterator<Integer> Iter = this.sitesMap.keySet().iterator();
		while (Iter.hasNext()) {
			Integer Key = Iter.next();
			IContactMapAbstractSite site = sitesMap.get(Key);
				((CContactMapAbstractSite) site).print();
		}
		System.out
				.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

	}

	public void modify(IContactMapAbstractSite s) {
		IContactMapAbstractSite site = sitesMap.get(s.getNameId());
		if(s.getInternalState()!= CInternalState.EMPTY_STATE)
			site.setInternalState(new CInternalState(s.getInternalState().getNameId()));
		site.setLinkState(s.getLinkState().clone());
	}

}
