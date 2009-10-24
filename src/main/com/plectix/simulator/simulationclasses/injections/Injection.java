package com.plectix.simulator.simulationclasses.injections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.probability.WeightedItem;
import com.plectix.simulator.simulationclasses.solution.SuperSubstance;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.ConnectedComponent;
import com.plectix.simulator.staticanalysis.Site;

/**
 * Class implements Injection.
 * 
 * @author avokhmin
 */
@SuppressWarnings("serial")
public final class Injection implements Serializable, WeightedItem {

	// idInConnectedComponent -> agent
	private final Map<Integer, Agent> agentsCorrespondence;
	private final List<Site> sites;
	private final List<Site> changedSites;
	private final ConnectedComponentInterface connectedComponent;
	private SuperSubstance superSubstanceImage = null;
	private long power = 1;

	public Injection() {
		sites = new LinkedList<Site>();
		changedSites = null;
		agentsCorrespondence = null;
		connectedComponent = null;
	}

	public Injection(ConnectedComponent connectedComponent,
			List<Site> sitesList, Map<Integer, Agent> agentLinkList) {
		this.connectedComponent = connectedComponent;
		this.sites = sitesList;
		this.agentsCorrespondence = agentLinkList;
		this.changedSites = new ArrayList<Site>();
	}

	public final void removeSiteFromSitesList(Site site) {
		sites.remove(site);
	}

	/**
	 * This method adds given site to util list.<br>
	 * This list uses in NegativeUpdate.
	 * 
	 * @param site
	 *            given site
	 */
	public final void addToChangedSites(Site site) {
		if (!(checkSiteExistanceAmongChangedSites(site)))
			this.changedSites.add(site);
	}

	public final boolean checkSiteExistanceAmongChangedSites(Site site) {
		for (Site chSite : this.changedSites)
			if (chSite == site)
				return true;
		return false;
	}

	public final Agent getAgentFromImageById(int id) {
		for (Map.Entry<Integer, Agent> agentLink : agentsCorrespondence.entrySet()) {
			if (agentLink.getKey() == id) {
				return agentLink.getValue();
			}
		}
		return null;
	}

	public final List<Site> getChangedSites() {
		return changedSites;
	}

	/**
	 * This method is required for tests only
	 * @return
	 */
	public final Map<Integer, Agent> getCorrespondence() {
		return agentsCorrespondence;
	}

	public final Collection<Site> getSiteList() {
		return sites;
	}

	public final ConnectedComponentInterface getConnectedComponent() {
		return connectedComponent;
	}

	public final void setSuperSubstance(SuperSubstance substance) {
		this.superSubstanceImage = substance;
		power = substance.getQuantity();
	}

	public final SuperSubstance getSuperSubstance() {
		return superSubstanceImage;
	}

	public final boolean isEmpty() {
		return this == ThreadLocalData.getEmptyInjection();
	}

	public final Agent getImageAgent() {
		if (agentsCorrespondence != null) {
			for (Agent targetAgent : agentsCorrespondence.values()) {
				return targetAgent;
			}
		}
		return null;
	}

	public final boolean findInCollection(List<Injection> injections) {
		int counter = 0;
		for (Injection injection : injections) {
			if (injection.getSiteList().size() == sites.size()) {
				for (Site site : injection.getSiteList()) {
					if (sites.contains(site))
						counter++;
					else {
						counter = 0;
						break;
					}
				}
				if (counter == injection.getSiteList().size())
					return true;
				counter = 0;
			}
		}
		return false;
	}

	@Override
	public final double getWeight() {
		return power;
	}

	public final boolean isSuper() {
		return this.superSubstanceImage != null;
	}

	public final void setSimple() {
		if (isSuper()) {
			superSubstanceImage = null;
			connectedComponent.updateInjection(this, 1);
		}
	}

	public final void eliminate() {
		connectedComponent.updateInjection(this, 0);
	}

	public final void incPower() {
		connectedComponent.updateInjection(this, power + 1);
	}

	public final void setPower(long i) {
		power = i;
	}
}
