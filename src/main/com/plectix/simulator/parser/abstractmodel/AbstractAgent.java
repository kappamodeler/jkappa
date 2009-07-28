package com.plectix.simulator.parser.abstractmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.parser.util.StringUtil;
import com.plectix.simulator.simulator.ThreadLocalData;

public class AbstractAgent implements Comparable<AbstractAgent> {
	private final int myNameId;
	private List<AbstractSite> mySites = new ArrayList<AbstractSite>();
	private final String myName;
	
	public AbstractAgent(int nameId) {
		myNameId = nameId;
		//TODO remove this code, use name instead nameId here
		myName = ThreadLocalData.getNameDictionary().getName(nameId);
	}
	
	public int getNameId() {
		return myNameId;
	}

	public void addSite(AbstractSite site) {
		mySites.add(site);
	}

	public List<AbstractSite> getSites() {
		return mySites;
	}

	public int compareTo(AbstractAgent o) {
		return myName.compareTo(o.myName);
	}
	
	//-------------------------------toString-----------------
	
	
	//TODO remove this code and implement AbstractSite 
	private static class ComparableSite implements Comparable<ComparableSite>{
		private final AbstractSite mySite;
		
		public ComparableSite(AbstractSite site) {
			mySite = site;
		}
		
		public AbstractSite getSite() {
			return mySite;
		}
		
		public int compareTo(ComparableSite o) {
			return mySite.getName().compareTo(o.mySite.getName());
		}
		
		public String toString() {
			return mySite.toString();
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(myName + "(");
		
		List<ComparableSite> sites = new ArrayList<ComparableSite>();
		
		for (AbstractSite site : mySites) {
			sites.add(new ComparableSite(site));
		}
		Collections.sort(sites);

		sb.append(StringUtil.listToString(sites));
		
		sb.append(")");
		return sb.toString();
	}
}
