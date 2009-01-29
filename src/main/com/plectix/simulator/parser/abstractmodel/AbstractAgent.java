package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.ISite;

public class AbstractAgent {
	private final int myNameId;
	private List<ISite> mySites = new ArrayList<ISite>();
	
	public AbstractAgent(int nameId) {
		myNameId = nameId;
	}
	
	public int getNameId() {
		return myNameId;
	}

	public void addSite(ISite site) {
		mySites.add(site);
	}

	public List<ISite> getSites() {
		return mySites;
	}
}
