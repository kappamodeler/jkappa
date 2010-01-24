package com.plectix.simulator.staticanalysis;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.injections.LiftElement;

/**
 * This class implements Site entity.
 * 
 * @author avokhmin
 */
public final class Site extends NamedEntity {
	public static final String DEFAULT_NAME = "SITE_DEFAULT_NAME";

	private final String name;
	private final Link linkState;
	private InternalState internalState = InternalState.EMPTY_STATE;
	private Agent parentAgent = null;
	private int linkIndex = -1;
	private final Set<LiftElement> liftElements = new LinkedHashSet<LiftElement>();

	/**
	 * Constructor by id
	 * @param name name of new site.
	 */
	public Site(String name) {
		this.name = name.intern();
		linkState = new Link();
	}

	//------------------------GETTERS AND SETTERS------------------------------
	
	/**
	 * Constructor by id and "parent" agent
	 * @param id name of new site.
	 * @param agent "parent" agent 
	 */
	public Site(String name, Agent agent) {
		this.name = name.intern();
		linkState = new Link();
		parentAgent = agent;
	}

	/**
	 * Adds <code>liftElement</code> to <code>liftList</code>.
	 * @param liftElement lift element to add
	 */
	public final void addToLift(LiftElement liftElement) {
		this.liftElements.add(liftElement);
	}

	/**
	 * This method returns list of lift elements of this site
	 * @return list of lift elements of this site
	 */
	public final Set<LiftElement> getLift() {
		return liftElements;
	}

	/**
	 * This method returns list of injections from given connected component, which point to this site
	 * @param component given connected component
	 * @return list of injections from given connected component, which point to this site
	 */
	public final List<Injection> getInjectionFromLift(ConnectedComponentInterface component) {
		List<Injection> list = new ArrayList<Injection>();
		for (LiftElement liftElement : this.liftElements)
			if (liftElement.getConnectedComponent() == component)
				list.add(liftElement.getInjection());
		return list;
	}

	/**
	 * Returns link state of this site.
	 * @return link state of this site.
	 */
	public final Link getLinkState() {
		return linkState;
	}

	/**
	 * This method sets link to the "parent" agent. 
	 * @param agent "parent" agent
	 */
	public final void setParentAgent(Agent agent) {
		if (agent == null)
			return;
		this.parentAgent = agent;
	}

	/**
	 * This method returns agent, which is parent for this site
	 * @return agent, which is parent for this site 
	 */
	public final Agent getParentAgent() {
		return parentAgent;
	}

	/**
	 * This method sets internal state for current site.
	 * @param internalState new value
	 */
	public final void setInternalState(InternalState internalState) {
		this.internalState = internalState;
	}

	/**
	 * This method returns internal state for current site.
	 * @return internal state for current site.
	 */
	public final InternalState getInternalState() {
		return internalState;
	}

	/**
	 * This method returns <tt>true</tt>, if current site equals 
	 * to given site (by name and {@link Agent#equalz(Agent) equalz}, 
	 * otherwise <tt>false</tt>).
	 * @param site given site 
	 * @return <tt>true</tt>, if current site equals to given site,
	 * otherwise <tt>false</tt>)
	 */
	public final boolean equalz(Site site) {
		if (this == site) {
			return true;
		}

		if (site == null) {
			return false;
		}

		if (!name.equals(site.name)) {
			return false;
		}

		if (parentAgent == null) {
			return site.getParentAgent() == null;
		} else {
			return parentAgent.equalz(site.getParentAgent());
		}
	}


	/**
	 * This method compares this site to a given one, according to it's internal states,
	 * link states. This one has boolean flag which is working mode for this method.  
	 * @param solutionSite given site
	 * @param completeComparision working mode of this method
	 * @return <tt>true</tt> if current site equals given site, otherwise <tt>false</tt>.
	 */
	public final boolean expandedEqualz(Site solutionSite, boolean completeComparision) {
		Link currentLinkState = linkState;
		Link solutionLinkState = solutionSite.getLinkState();

		InternalState currentInternalState = internalState;
		InternalState solutionInternalState = solutionSite.getInternalState();

		if (!completeComparision)
			return (currentLinkState.compare(solutionLinkState) && currentInternalState
					.compareInternalStates(solutionInternalState));
		else
			return (currentLinkState.equalz(solutionLinkState) && currentInternalState
					.equalz(solutionInternalState));

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
	 * This method returns name of this site
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name of this site
	 */
	@Override
	public final String getName() {
		return name;
	}

	/**
	 * This method clears list of lift elements
	 */
	public final void clearLifts() {
		this.liftElements.clear();
	}

	/**
	 * This method clears all injections, pointing to this site except one.
	 * @param incomingInjection excepted injection
	 */
	public final void clearIncomingInjections(Injection incomingInjection) {
		for (LiftElement liftElement : this.liftElements) {
			Injection injection = liftElement.getInjection();
			if (injection != incomingInjection) {
				for (Site site : injection.getSiteList()) {
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
	public final void removeInjectionFromLift(Injection injection) {
		for (LiftElement liftElement : this.liftElements) {
			if (injection == liftElement.getInjection()) {
				this.liftElements.remove(liftElement);
				return;
			}
		}
	}

	@Override
	public final Site clone(){
		Site site = new Site(name, null);
		site.setInternalState((new InternalState(this.internalState.getName())));
		site.linkState.setStatusLink(this.linkState.getStatusLink());
		site.setLinkIndex(this.getLinkIndex());
		return site;
	}
	
	@Override
	public final String toString() {
		return parentAgent.getName() + "(" + getName() + ")";
	}

	@Override
	protected String getDefaultName() {
		return DEFAULT_NAME;
	}
}
