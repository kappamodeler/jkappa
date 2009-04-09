package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLink;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAbstractAgent;
import com.plectix.simulator.interfaces.IAbstractSite;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CContactMapAbstractSite implements IContactMapAbstractSite {
	public static final int NO_INDEX = -1;

	private final int nameId;
	private CContactMapLinkState linkState;

	private CInternalState internalState = CInternalState.EMPTY_STATE;
	private IContactMapAbstractAgent linkAgent = null;
	private int linkIndex = NO_INDEX;

	public CContactMapAbstractSite(CSite site, IContactMapAbstractAgent agent) {
		this.nameId = site.getNameId();
		this.linkAgent = agent;
		if (site.getInternalState() != CInternalState.EMPTY_STATE)
			this.internalState = new CInternalState(site.getInternalState()
					.getNameId());
		this.linkState = new CContactMapLinkState(site.getLinkState());
	}

	public CContactMapAbstractSite(CSite site) {
		this.nameId = site.getNameId();
		this.linkState = new CContactMapLinkState();
	}

	public CContactMapAbstractSite(IContactMapAbstractSite site) {
		this.nameId = site.getNameId();
		this.linkAgent = site.getAgentLink();
		if (site.getInternalState() != CInternalState.EMPTY_STATE)
			this.internalState = new CInternalState(site.getInternalState()
					.getNameId());
		this.linkState = new CContactMapLinkState(site.getLinkState());
	}

	public IContactMapAbstractSite clone() {
		IContactMapAbstractSite siteOut = new CContactMapAbstractSite(this);
		return siteOut;
	}

	public static List<IContactMapAbstractSite> cloneAll(
			List<IContactMapAbstractSite> listIn) {
		List<IContactMapAbstractSite> listOut = new ArrayList<IContactMapAbstractSite>();
		for (IContactMapAbstractSite s : listIn)
			listOut.add(s.clone());
		return listOut;
	}

	public CContactMapAbstractSite(IContactMapAbstractAgent agent) {
		this.nameId = NO_INDEX;
		this.linkAgent = agent;
	}

	public CInternalState getInternalState() {
		return internalState;
	}

	public int getLinkIndex() {
		return linkIndex;
	}

	public CContactMapLinkState getLinkState() {
		return linkState;
	}

	public String getName() {
		if (nameId == CSite.NO_INDEX)
			return "EMPTY_SITE";
		return ThreadLocalData.getNameDictionary().getName(nameId);
	}

	public int getNameId() {
		return nameId;
	}

	public void setInternalState(CInternalState internalState) {
		this.internalState = internalState;
	}

	public void setLinkIndex(int valueOf) {
		this.linkIndex = valueOf;
	}

	public final IContactMapAbstractAgent getAgentLink() {
		return linkAgent;
	}

	public final void setAgentLink(IContactMapAbstractAgent linkAgent) {
		this.linkAgent = linkAgent;
	}

	public final boolean equalsNameId(IContactMapAbstractSite site) {
		if (nameId != site.getNameId())
			return false;
		return true;
	}

	public final boolean equalsLinkAgent(IContactMapAbstractSite site) {
		if (linkAgent.getNameId() != site.getAgentLink().getNameId())
			return false;
		return true;
	}

	public final boolean equalsInternalState(IContactMapAbstractSite site) {
		if (internalState.getNameId() != site.getInternalState().getNameId())
			return false;
		return true;
	}

	public final boolean equalsLinkState(IContactMapAbstractSite site) {
		if (!linkState.equalz(site.getLinkState()))
			return false;
		return true;
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

		if (internalState.getNameId() != site.getInternalState().getNameId())
			return false;

		if (!linkState.equalz(site.getLinkState()))
			return false;

		return true;
	}

	public final boolean equalByName(IAbstractSite obj) {
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

		return true;
	}

	public void print() {
////		System.out.println("site = " + getName());
////		System.out.println("internal state = " + internalState.getName());
//		if (linkState.getLinkSiteNameID() != -1) {
//			System.out.println("link agent = "
//					+ ThreadLocalData.getNameDictionary().getName(
//							linkState.getAgentNameID()));
//			System.out.println("link site = "
//					+ ThreadLocalData.getNameDictionary().getName(
//							linkState.getLinkSiteNameID()));
//			if (linkState.getInternalStateNameID() != -1)
//				System.out.println("link istate = "
//						+ ThreadLocalData.getNameDictionary().getName(
//								linkState.getInternalStateNameID()));
//		}
//		System.out
//				.println("__________________________________________________________________________");

	}

	public String toString() {
		String st = "site = " + getName();
		st += " from agent = " + linkAgent.getName();
		if (nameId == NO_INDEX)
			return st;

		if (internalState.getNameId() != -1)
			st += " internal state = " + internalState.getName();
		if (linkState != null && linkState.getLinkSiteNameID() != -1) {
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
			if (this.equalByName(site)) {
				return true;
			}
		}
		return false;
	}

	public boolean isFit(IContactMapAbstractSite s) {
		if (nameId == CSite.NO_INDEX)
			return true;
		if (!internalState.compareInternalStates(s.getInternalState()))
			return false;
		if (!linkState.compareLinkStates(s.getLinkState()))
			return false;

		return true;
	}

	public boolean isFit(int agentId, int siteId, int internalStateId,
			int agentLinkId, int siteLinkId, int internalStateLinkId) {
		if (linkAgent.getNameId() != agentId)
			return false;
		if (nameId != siteId)
			return false;
		if (internalState.getNameId() != internalStateId)
			return false;
		if (linkState.getAgentNameID() != agentLinkId)
			return false;
		if (linkState.getLinkSiteNameID() != siteLinkId)
			return false;
		if (linkState.getInternalStateNameID() != internalStateLinkId)
			return false;
		return true;
	}

	public void setLinkState(CContactMapLinkState linkState) {
		this.linkState = linkState;
	}
}
