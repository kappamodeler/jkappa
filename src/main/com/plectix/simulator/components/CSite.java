package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.interfaces.ILiftElement;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.ThreadLocalData;

public final class CSite implements ISite, Serializable {
	public static final int NO_INDEX = -1;

	// TODO move to CInternalState?
	private static final CInternalState EMPTY_STATE = new CInternalState(
			NO_INDEX);

	private final int nameId;
	private final ILinkState linkState;
	private IInternalState internalState = EMPTY_STATE;
	private boolean changed;
	private IAgent linkAgent = null;
	private int linkIndex = NO_INDEX;

	private List<ILiftElement> liftList = new ArrayList<ILiftElement>();

	public CSite(int id) {
		this.nameId = id;
		linkState = new CLinkState(CLinkState.STATUS_LINK_FREE);
	}

	public CSite(int id, IAgent agent) {
		this.nameId = id;
		linkState = new CLinkState(CLinkState.STATUS_LINK_FREE);
		linkAgent = agent;
	}

	public final void setLift(List<ILiftElement> lift) {
		this.liftList = lift;
	}

	// TODO
	public final void addToLift(ILiftElement liftElement) {
		this.liftList.add(liftElement);
	}

	public final List<ILiftElement> getLift() {
		return Collections.unmodifiableList(liftList);
	}

	public final void clearLift() {
		liftList.clear();
	}

	public final boolean isConnectedComponentInLift(IConnectedComponent inCC) {
		for (ILiftElement liftElement : this.liftList)
			if (liftElement.getConnectedComponent() == inCC)
				return true;
		return false;
	}

	public final List<IInjection> getInjectionFromLift(IConnectedComponent inCC) {
		List<IInjection> list = new ArrayList<IInjection>();
		for (ILiftElement liftElement : this.liftList)
			if (liftElement.getConnectedComponent() == inCC)
				list.add(liftElement.getInjection());
		return Collections.unmodifiableList(list);
	}

	public final ILinkState getLinkState() {
		return linkState;
	}

	public final void setAgentLink(IAgent agent) {
		if (agent == null)
			return;
		this.linkAgent = agent;
	}

	public final IAgent getAgentLink() {
		return linkAgent;
	}

	public final void setInternalState(IInternalState internalState) {
		this.internalState = internalState;
	}

	public final IInternalState getInternalState() {
		return internalState;
	}

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
		if (!(this.linkAgent.equals(site.getAgentLink())))
			return false;
		return true;
	}

	public final void setLinkIndex(int index) {
		this.linkIndex = index;
	}

	public final int getLinkIndex() {
		return linkIndex;
	}

	public final int getNameId() {
		return nameId;
	}

	public final void clearLiftList() {
		this.liftList.clear();
	}

	public final void removeInjectionsFromCCToSite(IInjection inInjection) {

		for (ILiftElement liftElement : this.liftList) {
			IInjection injection = liftElement.getInjection();
			if (injection != inInjection) {
				for (ISite site : injection.getSiteList()) {
					if (this != site)
						site.removeInjectionFromLift(injection);
				}
				liftElement.getConnectedComponent().removeInjection(injection);
			}
		}
		/*
		 * for (CLiftElement liftElement : this.liftList) { CInjection injection
		 * = liftElement.getInjection(); if (injection != inInjection) { for
		 * (CSite site : injection.getChangedSites()) { if (this != site)
		 * site.removeInjectionFromLift(injection); }
		 * liftElement.getConnectedComponent().getInjectionsList().remove(
		 * injection); } }
		 */
	}

	public final void removeInjectionFromLift(IInjection injection) {
		for (ILiftElement liftElement : this.liftList)
			if (injection == liftElement.getInjection()) {
				this.liftList.remove(liftElement);
				return;
			}
	}

	public final String getName() {
		return ThreadLocalData.getNameDictionary().getName(nameId);
	}
}
