package com.plectix.simulator.components.injections;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CAgentLink;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.solution.SolutionUtils;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.*;

/**
 * Class implements Injection.
 * @author avokhmin
 */
public class CInjection implements Serializable {

	public static final CInjection EMPTY_INJECTION = new CInjection();

	private List<CAgentLink> agentLinkList;
	private List<CSite> sitesList = new ArrayList<CSite>();
	private List<CSite> changedSites;
	private int myId = 0;
	private CConnectedComponent connectedComponent;
	private SuperSubstance myImageComponent = null;
	
	private CInjection() {

	}

	public CInjection(CConnectedComponent connectedComponent,
			List<CSite> sitesList, List<CAgentLink> agentLinkList) {
		this.connectedComponent = connectedComponent;
		this.sitesList = sitesList;
		this.agentLinkList = agentLinkList;
		this.changedSites = new ArrayList<CSite>();
	}

	public final void removeSiteFromSitesList(CSite site) {
		int index = 0;
		for (CSite siteL : this.sitesList) {
			if (site == siteL) {
				this.sitesList.remove(index);
				return;
			}
			index++;
		}
	}

	public final void addToChangedSites(CSite site) {
		if (!(checkSiteExistanceAmongChangedSites(site)))
			this.changedSites.add(site);
	}

	public final void clearChangedSites() {
		changedSites.clear();
	}

	public final boolean checkSiteExistanceAmongChangedSites(CSite site) {
		for (CSite chSite : this.changedSites)
			if (chSite == site)
				return true;
		return false;
	}

	public final void setId(int id) {
		myId = id;
	}

	public final int getId() {
		return myId;
	}

	public final CAgent getAgentFromImageById(int id) {
		for (CAgentLink agentL : agentLinkList)
			if (agentL.getIdAgentFrom() == id)
				return agentL.getAgentTo();
		return null;
	}
	
	public final List<CSite> getChangedSites() {
		return Collections.unmodifiableList(changedSites);
	}

	public final void setChangedSites(List<CSite> changedSites) {
		this.changedSites = changedSites;
	}

	public final List<CAgentLink> getAgentLinkList() {
		return Collections.unmodifiableList(agentLinkList);
	}

	public final List<CSite> getSiteList() {
		return Collections.unmodifiableList(sitesList);
	}

	public final void setSiteList(List<CSite> siteList) {
		this.sitesList = siteList;
	}

	public final IConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}
	
	public void setSuperSubstance(SuperSubstance substance) {
		this.myImageComponent = substance;
	}
	
	public final SuperSubstance getSuperSubstance() {
		return myImageComponent;
	}
	
	public boolean isEmpty() {
		return this == CInjection.EMPTY_INJECTION;
	}
	
	public CAgent getImageAgent() {
		if (agentLinkList != null) {
			for (CAgentLink agentL : agentLinkList) {
				return agentL.getAgentTo();
			}
		}
		return null;
	}

	public boolean compareInjectedLists(List<CInjection> list) {
		int counter = 0;
		for (CInjection injection : list) {
			if (injection.getSiteList().size() == sitesList.size()) {
				for (CSite site : injection.getSiteList()) {
					if (sitesList.contains(site))
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
	
	public boolean isSuper() {
		return this.myImageComponent != null;
	}
}
