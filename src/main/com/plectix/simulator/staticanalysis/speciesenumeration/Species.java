package com.plectix.simulator.staticanalysis.speciesenumeration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.ConnectedComponent;
import com.plectix.simulator.staticanalysis.InternalState;
import com.plectix.simulator.staticanalysis.Link;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;

public class Species {
	private static final int NOT_INITIALIZE = -1;
	// all agents
	// numberOfInitiateAgent
	// all after numberOfInitiatedAgent with one site
	private final List<Agent> instance;
	// list of localViews by type of agent
	private final Map<String, List<AbstractAgent>> availableListOfViews;
	private final String hashCode;
	private int numberOfInitiatedAgent;

	public Species(Map<String, List<AbstractAgent>> availableListOfViews,
			AbstractAgent origin) {
		this.availableListOfViews = availableListOfViews;
		instance = new ArrayList<Agent>();
		numberOfInitiatedAgent = 0;
		Agent first = new Agent(origin.getName(), NOT_INITIALIZE);
		instance.add(first);
		initiateAgent(first, origin);
		hashCode = (new ConnectedComponent(instance)).getSmilesString();
	}

	public Species(ArrayList<Agent> cloneInstance,
			Map<String, List<AbstractAgent>> availableListOfViews2,
			int numberOfInitiatedAgent2, AbstractAgent mask) {
		instance = cloneInstance;
		this.availableListOfViews = availableListOfViews2;
		numberOfInitiatedAgent = numberOfInitiatedAgent2;
		initiateAgent(instance.get(numberOfInitiatedAgent), mask);
		hashCode = (new ConnectedComponent(instance)).getSmilesString();
	}

	// add tentacles (almost empty agents)
	private final void propagateTentacles(Agent rootOfPlague, AbstractAgent archetype) {
		for (AbstractSite abstractSite : archetype.getSitesMap().values()) {

			// agent has the same site
			Site old = rootOfPlague.getSiteByName(abstractSite.getName());
			if (old != null) {
				old.setInternalState(abstractSite.getInternalState());
				continue;
			}

			Site site = new Site(abstractSite.getName(), rootOfPlague);

			// set internal state to site from abstract site
			if (abstractSite.getInternalState() != InternalState.EMPTY_STATE) {
				site.setInternalState(abstractSite.getInternalState());
			}

			rootOfPlague.addSite(site);

			// set link state
			if (abstractSite.getLinkState().getStatusLink() == LinkStatus.FREE) {
				// site.getLinkState().setStatusLink(CLinkStatus.FREE);
				site.getLinkState().setFree();
				continue;
			}

			// if there is link....
			Agent linkedAgent = new Agent(abstractSite.getLinkState()
					.getAgentName(), NOT_INITIALIZE);
			Site linkedSite = new Site(abstractSite.getLinkState()
					.getConnectedSiteName(), linkedAgent);

			linkedAgent.addSite(linkedSite);

			site.getLinkState().connectSite(linkedSite);
			linkedSite.getLinkState().connectSite(site);

			instance.add(linkedAgent);

		}
	}

	private final void initiateAgent(Agent agent, AbstractAgent archetype) {
		if (agent.getId() != NOT_INITIALIZE) {
			System.out.println("adfhgh");
		}
		agent.setId(numberOfInitiatedAgent);
		numberOfInitiatedAgent++;
		propagateTentacles(agent, archetype);
	}

	public final String getHashCode() {
		return hashCode;
	}

	public final List<Species> propagate() {
		if (instance.size() == numberOfInitiatedAgent) {
			return null;
		}
		List<Species> answer = new LinkedList<Species>();
		List<AbstractAgent> masks = getMasks(instance
				.get(numberOfInitiatedAgent));

		// for example this agent doesn't presence
		if (masks == null || masks.isEmpty()) {
			return null;
		}
		for (AbstractAgent mask : masks) {
			answer.add(clone(this, mask));
		}
		return answer;
	}

	private final Species clone(Species species, AbstractAgent mask) {
		return new Species(cloneInstance(), availableListOfViews,
				numberOfInitiatedAgent, mask);
	}

	private final ArrayList<Agent> cloneInstance() {
		ArrayList<Agent> newAgentsList = new ArrayList<Agent>();
		for (Agent agent : instance) {
			Agent newAgent = new Agent(agent.getName(), agent.getId());
			for (Site site : agent.getSites()) {
				Site newSite = new Site(site.getName(), newAgent);
				newSite.setLinkIndex(site.getLinkIndex());
				newSite.setInternalState(new InternalState(site
						.getInternalState().getName()));
				newAgent.addSite(newSite);
			}
			newAgentsList.add(newAgent);
		}
		for (int i = 0; i < newAgentsList.size(); i++) {
			for (Site siteNew : newAgentsList.get(i).getSites()) {
				Link lsNew = siteNew.getLinkState();
				Link lsOld = instance.get(i).getSiteByName(
						siteNew.getName()).getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getConnectedSite() != null) {
					Site siteOldLink = lsOld.getConnectedSite();
					int j = 0;
					for (; j < instance.size(); j++) {
						if (instance.get(j) == siteOldLink.getParentAgent())
							break;
					}
					int index = j;
					lsNew.connectSite(newAgentsList.get(index).getSiteByName(
							siteOldLink.getName()));
				}
			}
		}
		return newAgentsList;
	}

	private final List<AbstractAgent> getMasks(Agent agent) {
		List<AbstractAgent> list = new LinkedList<AbstractAgent>();
		AbstractAgent abstractAgent = new AbstractAgent(agent.getName());
		abstractAgent.addSite(new AbstractSite(agent.getSites().iterator()
				.next(), abstractAgent));

		if (availableListOfViews.get(agent.getName()) == null) {
			return null;
		}
		for (AbstractAgent view : availableListOfViews.get(agent.getName())) {
			if (abstractAgent.isFit(view)) {
				list.add(view);
			}
		}
		return list;

	}

	public final boolean isComplete() {
		return (instance.size() == numberOfInitiatedAgent);
	}
}
