package com.plectix.simulator.components.contactMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.interfaces.IAbstractAgent;
import com.plectix.simulator.interfaces.IAbstractSite;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CContactMapAbstractSite implements IContactMapAbstractSite {
	public static final int NO_INDEX = -1;

	private final int nameId;
	private CContactMapLinkState linkState;

	private IInternalState internalState = CInternalState.EMPTY_STATE;
	private IContactMapAbstractAgent linkAgent = null;
	private int linkIndex = NO_INDEX;

	public CContactMapAbstractSite(ISite site, IContactMapAbstractAgent agent) {
		this.nameId = site.getNameId();
		this.linkAgent = agent;
		this.internalState = site.getInternalState();
		this.linkState = new CContactMapLinkState(site.getLinkState());
	}

	public CContactMapAbstractSite(IContactMapAbstractAgent agent) {
		this.nameId = NO_INDEX;
		this.linkAgent = agent;
	}

	public IInternalState getInternalState() {
		return internalState;
	}

	public int getLinkIndex() {
		return linkIndex;
	}

	public CContactMapLinkState getLinkState() {
		return linkState;
	}

	public String getName() {
		return ThreadLocalData.getNameDictionary().getName(nameId);
	}

	public int getNameId() {
		return nameId;
	}

	public void setInternalState(IInternalState internalState) {
		this.internalState = internalState;
	}

	public void setLinkIndex(int valueOf) {
		this.linkIndex = valueOf;
	}

	public final IContactMapAbstractAgent getAgentLink() {
		return linkAgent;
	}

	public final boolean equalz(IAbstractSite obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof CContactMapAbstractSite)) {
			return false;
		}

		CContactMapAbstractSite site = (CContactMapAbstractSite) obj;

		if (nameId != site.nameId)
			return false;

		if (!linkAgent.equalz(site.getAgentLink()))
			return false;

		if (internalState.getNameId() != site.getInternalState().getNameId())
			return false;

		if (!linkState.equalz(site.getLinkState()))
			return false;

		return true;
	}

	public void print() {
		System.out.println("site = " + getName());
		System.out.println("internal state = " + internalState.getName());
		if (linkState.getLinkSiteNameID() != -1) {
			System.out.println("link agent = "
					+ ThreadLocalData.getNameDictionary().getName(
							linkState.getAgentNameID()));
			System.out.println("link site = "
					+ ThreadLocalData.getNameDictionary().getName(
							linkState.getLinkSiteNameID()));
			if (linkState.getInternalStateNameID() != -1)
				System.out.println("link istate = "
						+ ThreadLocalData.getNameDictionary().getName(
								linkState.getInternalStateNameID()));
		}
		System.out
				.println("__________________________________________________________________________");

	}

	@Override
	public String toString() {
		String st = "site = " + getName();
		if (internalState.getNameId() != -1)
			st += " internal state = " + internalState.getName();
		if (linkState.getLinkSiteNameID() != -1) {
			st += " link agent = "
					+ ThreadLocalData.getNameDictionary().getName(
							linkState.getAgentNameID());
			st += " link site = "
					+ ThreadLocalData.getNameDictionary().getName(
							linkState.getLinkSiteNameID());
			if (linkState.getInternalStateNameID() != -1)
				st += " link istate = "
						+ ThreadLocalData.getNameDictionary().getName(
								linkState.getInternalStateNameID());
		}
		return st;
	}

	public boolean includedInCollection(
			Collection<IContactMapAbstractSite> collection) {
		for (IContactMapAbstractSite site : collection) {
			if (this.equalz(site)) {
				return true;
			}
		}
		return false;
	}

}
