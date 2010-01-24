package com.plectix.simulator.parser.abstractmodel;

import com.plectix.simulator.staticanalysis.InternalState;
import com.plectix.simulator.staticanalysis.LinkRank;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.util.NameDictionary;

public final class ModelSite {
	private final ModelLink linkState; 
	private final String name;
	private int linkIndex = -1;
	private String internalStateName = InternalState.DEFAULT_NAME;
	
	public ModelSite(String name) {
		this.name = name;
		this.linkState = new ModelLink();
	}
	
	public final String getName() {
		return name;
	}

	public final void setInternalState(String internalState) {
		internalStateName = internalState;
	}

	public final ModelLink getLinkState() {
		return linkState;
	}

	public final void setLinkIndex(int index) {
		linkIndex = index;
	}
	
	public final int getLinkIndex() {
		return linkIndex;
	}

	public final String getInternalStateName() {
		return internalStateName;
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		if (!NameDictionary.isDefaultInternalStateName(internalStateName)) {
			sb.append("~" + internalStateName);
		}
		if (linkState.getStatusLinkRank() == LinkRank.SEMI_LINK) {
			sb.append("!_");
		} else if (linkIndex != -1) {
			sb.append("!" + linkIndex);
		} else if (linkState.getStatusLink() == LinkStatus.WILDCARD) {
			sb.append("?");
		}
		return sb.toString();
	}
}
