package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.injections.CLiftElement;

/**
 * This class implements Site entity.
 * 
 * @author avokhmin
 */
@SuppressWarnings("serial")
public final class CSite implements Serializable {
	public static final int NO_INDEX = -1;

	private final int nameId;
	private final CLink linkState;
	private CInternalState internalState = CInternalState.EMPTY_STATE;
//	private boolean changed;
	private CAgent linkAgent = null;
	private int linkIndex = NO_INDEX;

	private List<CLiftElement> liftList = new ArrayList<CLiftElement>();

	/**
	 * Constructor by id
	 * @param nameId nameId of new site.
	 */
	public CSite(int nameId) {
		this.nameId = nameId;
		linkState = new CLink(CLinkStatus.FREE);
	}

	//------------------------GETTERS AND SETTERS------------------------------
	
	/**
	 * Constructor by id and "parent" agent
	 * @param id nameId of new site.
	 * @param agent "parent" agent 
	 */
	public CSite(int id, CAgent agent) {
		this.nameId = id;
		linkState = new CLink(CLinkStatus.FREE);
		linkAgent = agent;
	}

	/**
	 * This method sets list of lift elements for this site
	 * @param lift new value
	 */
	public final void setLift(List<CLiftElement> lift) {
		this.liftList = lift;
	}

	/**
	 * Adds <code>liftElement</code> to <code>liftList</code>.
	 * @param liftElement lift element to add
	 */
	public final void addToLift(CLiftElement liftElement) {
		this.liftList.add(liftElement);
	}

	/**
	 * This method returns list of lift elements of this site
	 * @return list of lift elements of this site
	 */
	public final List<CLiftElement> getLift() {
		return Collections.unmodifiableList(liftList);
	}

	/**
	 * This method returns list of injections from given connected component, which point to this site
	 * @param inCC given connected component
	 * @return list of injections from given connected component, which point to this site
	 */
	public final List<CInjection> getInjectionFromLift(IConnectedComponent inCC) {
		List<CInjection> list = new ArrayList<CInjection>();
		for (CLiftElement liftElement : this.liftList)
			if (liftElement.getConnectedComponent() == inCC)
				list.add(liftElement.getInjection());
		return Collections.unmodifiableList(list);
	}

	/**
	 * Returns link state of this site.
	 * @return link state of this site.
	 */
	public final CLink getLinkState() {
		return linkState;
	}

	/**
	 * This method sets link to the "parent" agent. 
	 * @param agent "parent" agent
	 */
	public final void setAgentLink(CAgent agent) {
		if (agent == null)
			return;
		this.linkAgent = agent;
	}

	/**
	 * This method returns agent, which is parent for this site
	 * @return agent, which is parent for this site 
	 */
	public final CAgent getAgentLink() {
		return linkAgent;
	}

	/**
	 * This method sets internal state for current site.
	 * @param internalState new value
	 */
	public final void setInternalState(CInternalState internalState) {
		this.internalState = internalState;
	}

	/**
	 * This method returns internal state for current site.
	 * @return internal state for current site.
	 */
	public final CInternalState getInternalState() {
		return internalState;
	}

	/**
	 * This method returns <tt>true</tt>, if current site equals 
	 * to given site (by nameId and {@link CAgent#equalz(CAgent) equalz}, 
	 * otherwise <tt>false</tt>).
	 * @param site given site 
	 * @return <tt>true</tt>, if current site equals to given site,
	 * otherwise <tt>false</tt>)
	 */
	public final boolean equalz(CSite site) {
		if (this == site) {
			return true;
		}

		if (site == null) {
			return false;
		}

		if (nameId != site.nameId) {
			return false;
		}

		if (linkAgent == null) {
			return site.getAgentLink() == null;
		} else {
			return linkAgent.equalz(site.getAgentLink());
		}
	}


	/**
	 * This method compares this site to a given one, according to it's internal states,
	 * link states. This one has boolean flag which is working mode for this method.  
	 * @param solutionSite given site
	 * @param fullEquality working mode of this method
	 * @return <tt>true</tt> if current site equals given site, otherwise <tt>false</tt>.
	 */
	public final boolean expandedEqualz(CSite solutionSite, boolean fullEquality) {
		CLink currentLinkState = linkState;
		CLink solutionLinkState = solutionSite.getLinkState();

		CInternalState currentInternalState = internalState;
		CInternalState solutionInternalState = solutionSite.getInternalState();

		if (!fullEquality)
			return (currentLinkState.compare(solutionLinkState) && currentInternalState
					.compareInternalStates(solutionInternalState));
		else
			return (currentLinkState.equalz(solutionLinkState) && currentInternalState
					.equalz(solutionInternalState));

	}
	
	/**
	 * This method is some kind of override {@link Collection#contains(Object) contains}.
	 * We need it just because we haven't override default {@link Object#equals(Object) equals},
	 * but we use our own {@link CSite#equalz(CSite) equalz}. So we had to create util
	 * method for checking current agent in given collection 
	 * @param collection given collection
	 */
	public final boolean includedInCollection(Collection<CSite> collection) {
		for (CSite agent : collection) {
			if (this.equalz(agent)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method sets link index to this site<br>
	 * For example site "x" of agent C in (A(x!1), B(y!2, y!1), C(x!2)) has link index == 2
	 * @param index new value
	 */
	public final void setLinkIndex(int index) {
		this.linkIndex = index;
	}

	/**
	 * This method returns link index to this site<br>
	 * For example site "x" of agent C in (A(x!1), B(y!2, y!1), C(x!2)) has link index == 2
	 * @return link index to this site
	 */
	public final int getLinkIndex() {
		return linkIndex;
	}

	/**
	 * This method returns name-id of this site
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name-id of this site
	 */
	public final int getNameId() {
		return nameId;
	}

	/**
	 * This method returns name of this site
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name of this site
	 */
	public final String getName() {
		return ThreadLocalData.getNameDictionary().getName(nameId);
	}

	/**
	 * This method clears list of lift elements
	 */
	public final void clearLiftList() {
		this.liftList.clear();
	}

	/**
	 * This method clears all injections, pointing to this site except one.
	 * @param inInjection excepted injection
	 */
	public final void clearIncomingInjections(CInjection inInjection) {
		for (CLiftElement liftElement : this.liftList) {
			CInjection injection = liftElement.getInjection();
			if (injection != inInjection) {
				for (CSite site : injection.getSiteList()) {
					if (this != site) {
						site.removeInjectionFromLift(injection);
					}
				}
				liftElement.getConnectedComponent().removeInjection(injection);
			}
		}
	}

	/**
	 * This method finds and removes injection from injections of lift elements 
	 * @param injection injection to remove
	 */
	public final void removeInjectionFromLift(CInjection injection) {
		for (CLiftElement liftElement : this.liftList) {
			if (injection == liftElement.getInjection()) {
				this.liftList.remove(liftElement);
				return;
			}
		}
	}

	public String toString() {
		return linkAgent.getName() + "(" + getName() + ")";
	}
}
