package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.ISite;

public class CSite implements ISite {
	public static final int NO_INDEX = -1;

	private static final CInternalState EMPTY_STATE = new CInternalState(
			NO_INDEX);

	private int nameId;
	private CLinkState linkState;
	private CInternalState internalState = EMPTY_STATE;
	private boolean changed;
	private CAgent linkAgent = null;
	private int linkIndex = NO_INDEX;

	private List<CLiftElement> liftList = new ArrayList<CLiftElement>();

	public CSite(int id) {
		this.nameId = id;
		linkState = new CLinkState(CLinkState.STATUS_LINK_FREE);
	}

	public CSite(int id, CAgent agent) {
		this.nameId = id;
		linkState = new CLinkState(CLinkState.STATUS_LINK_FREE);
		linkAgent = agent;
	}

	public void setLift(List<CLiftElement> lift) {
		this.liftList = lift;
	}

	public void addToLift(CLiftElement liftElement) {
		this.liftList.add(liftElement);
	}

	public List<CLiftElement> getLift() {
		return liftList;
	}

	public boolean isConnectedComponentInLift(CConnectedComponent inCC) {
		for (CLiftElement liftElement : this.liftList)
			if (liftElement.getConnectedComponent() == inCC)
				return true;
		return false;
	}

	@Override
	public final CLinkState getLinkState() {
		return linkState;
	}

	public final void setAgentLink(CAgent agent) {
		if (agent == null)
			return;
		this.linkAgent = agent;
	}

	public final CAgent getAgentLink() {
		return linkAgent;
	}

	public final void setInternalState(CInternalState internalState) {
		this.internalState = internalState;
	}

	public final CInternalState getInternalState() {
		return internalState;
	}

	@Override
	public final boolean isChanged() {
		return changed;
	}

	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof CSite))
			return false;
		CSite site = (CSite) obj;
		if (!(nameId == site.nameId))
			return false;
		//if (internalState == null)
		//	return true;
		// return internalState.equals(site.internalState);
		return true;
	}

	public final void setLinkIndex(int index) {
		this.linkIndex = index;
	}

	public final int getLinkIndex() {
		return linkIndex;
	}

	public final Integer getNameId() {
		return nameId;
	}
	
	public final void removeInjectionsFromCCToSite(CInjection inInjection) {

		// for (CLiftElement liftElement : this.lift){
		// this.lift.remove(index);

		for (CLiftElement liftElement : this.liftList) {
			CInjection injection = liftElement.getInjection();
			if (injection != inInjection) {
				for (CSite site : injection.getSiteList()) {
					// site.getLift().remove(injection);
					if(this!=site)
					site.removeInjectionFromLift(injection);
				}
				liftElement.getConnectedComponent().getInjectionsList().remove(
						injection);
			}
		}
	}

	private final void removeInjectionFromLift(CInjection injection) {
		for (CLiftElement liftElement : this.liftList)
			if (injection == liftElement.getInjection()) {
				this.liftList.remove(liftElement);
				return;
			}
	}

}
