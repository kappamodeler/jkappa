package com.plectix.simulator.parser.abstractmodel;

import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.ThreadLocalData;

//TODO implement!
public class AbstractSite {
	private final int myNameId;
	private int myInternalStateNameId = CSite.NO_INDEX;
	private final AbstractLinkState myLinkState; 
	private final String myName;
	private int myLinkIndex = -1;
	
	public AbstractSite(int nameId) {
		myNameId = nameId;
		myName = ThreadLocalData.getNameDictionary().getName(nameId);
		myLinkState = new AbstractLinkState(CLinkStatus.FREE);
	}
	
	public String getName() {
		return ThreadLocalData.getNameDictionary().getName(myNameId);
	}

	public void setInternalState(int internalState) {
		myInternalStateNameId = internalState;
	}

	public AbstractLinkState getLinkState() {
		return myLinkState;
	}

	public void setLinkIndex(int index) {
		myLinkIndex = index;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(myName);
		if (myInternalStateNameId != CSite.NO_INDEX) {
			sb.append("~" + ThreadLocalData.getNameDictionary().getName(myInternalStateNameId));
		}
		if (myLinkState.getStatusLinkRank() == CLinkRank.SEMI_LINK) {
			sb.append("!_");
		} else if (myLinkIndex != -1) {
			sb.append("!" + myLinkIndex);
		} else if (myLinkState.getStatusLink() == CLinkStatus.WILDCARD) {
			sb.append("?");
		}
		return sb.toString();
	}

	public int getNameId() {
		return myNameId;
	}

	public int getLinkIndex() {
		return myLinkIndex;
	}

	public int getInternalStateNameId() {
		return myInternalStateNameId;
	}
}
