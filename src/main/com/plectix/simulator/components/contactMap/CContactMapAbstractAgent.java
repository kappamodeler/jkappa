package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IAbstractAgent;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CContactMapAbstractAgent implements IContactMapAbstractAgent {
	private Map<Integer, List<IContactMapAbstractSite>> siteMap;
	private long id = -1;
	private int nameID = -1;
	private final CContactMapAbstractSite myEmptySite = new CContactMapAbstractSite(this);

	@Override
	public String toString() {
		String st = getName();
		return st;
	}
	
	public final List<IContactMapAbstractSite> getSites(){
		List<IContactMapAbstractSite> list = new ArrayList<IContactMapAbstractSite>();
		Iterator<Integer> iterator = siteMap.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			List<IContactMapAbstractSite> siteList = siteMap.get(key);
			list.addAll(siteList);
		}
		return list;
	}
	
	public final Map<Integer, List<IContactMapAbstractSite>> getSitesMap(){
		return this.siteMap;
	}
	
	public final boolean containsSite(IContactMapAbstractSite site){
		List<IContactMapAbstractSite> list = siteMap.get(site.getNameId());
		if(list==null)
			return false;
		for(IContactMapAbstractSite s : list)
			if(s.equalz(site))
				return true;
		return false;
	}
	
	public CContactMapAbstractAgent(IAgent agent) {
		this.nameID = agent.getNameId();
		this.siteMap = new HashMap<Integer, List<IContactMapAbstractSite>>();
	}
	
	public CContactMapAbstractAgent(int nameID) {
		this.nameID = nameID;
		this.siteMap = new HashMap<Integer, List<IContactMapAbstractSite>>();
	}

	public final boolean addSites(IAgent agent) {
		boolean wasAdded = false;
		for (ISite site : agent.getSites()) {
			if (addSite(site))
				wasAdded = true;
		}
		return wasAdded;
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

		return nameID == agent.nameID;
	}

	public boolean addSite(ISite site) {
		Integer key = site.getNameId();
		List<IContactMapAbstractSite> list = siteMap.get(key);
		if (list == null) {
			list = new ArrayList<IContactMapAbstractSite>();
			siteMap.put(key, list);
		}

		CContactMapAbstractSite cMAS = new CContactMapAbstractSite(site, this);
		if (!cMAS.includedInCollection(list)) {
			list.add(cMAS);
			return true;
		}
		return false;
	}

	public long getHash() {
		return id;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		if(nameID==-1)
			return "-1";
		return ThreadLocalData.getNameDictionary().getName(nameID);
	}

	public int getNameId() {
		return nameID;
	}

	public void print(){
		System.out.println("agent = "+ this.toString());
		Iterator<Integer> Iter = this.siteMap.keySet().iterator();
		while (Iter.hasNext()) {
			Integer Key = Iter.next();
			List<IContactMapAbstractSite> cMASList = siteMap.get(Key);
			for(IContactMapAbstractSite site: cMASList){
				((CContactMapAbstractSite)site).print();
			}
		}
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		
	}
	
}
