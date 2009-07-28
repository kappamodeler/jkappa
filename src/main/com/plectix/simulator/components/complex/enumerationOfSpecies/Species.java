package com.plectix.simulator.components.complex.enumerationOfSpecies;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLink;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;

public class Species {

	private static final int NOT_INITIALIZE = -1;
	private String hashCode = "";

	// all agents
	// numberOfInitiateAgent
	// all after numberOfInitiatedAgent with one site
	private List<CAgent> instance;

	private int numberOfInitiatedAgent;

	// list of localViews by type of agent

	private Map<Integer, List<CAbstractAgent>> availableListOfViews;

	public Species(
			Map<Integer, List<CAbstractAgent>> availableListOfViews,
			CAbstractAgent origin){
			
		this.availableListOfViews = availableListOfViews;
		instance = new ArrayList<CAgent>();
		numberOfInitiatedAgent = 0;

		CAgent first = new CAgent(origin.getNameId(), NOT_INITIALIZE);
		instance.add(first);
		initiateAgent(first, origin);
		hashCode = (new CConnectedComponent(instance)).getHash();

	}

	public Species(ArrayList<CAgent> cloneInstance,
			Map<Integer, List<CAbstractAgent>> availableListOfViews2,
			int numberOfInitiatedAgent2, CAbstractAgent mask) {
		instance = cloneInstance;
		this.availableListOfViews = availableListOfViews2;
		numberOfInitiatedAgent = numberOfInitiatedAgent2;
		initiateAgent(instance.get(numberOfInitiatedAgent), mask);
		hashCode = (new CConnectedComponent(instance)).getHash();

	}

	// add tentacles (almost empty agents)
	private void propagateTentacles(CAgent rootOfPlague,
			CAbstractAgent archetype) {

		for (CAbstractSite abstractSite : archetype.getSitesMap().values()) {

			// agent has the same site
			CSite old = rootOfPlague.getSiteByNameId(abstractSite.getNameId());
			if (old != null) {
				old.setInternalState(abstractSite.getInternalState());
				continue;
			}

			CSite site = new CSite(abstractSite.getNameId(),rootOfPlague);
			
			// set internal state to site from abstract site
			if (abstractSite.getInternalState() != CInternalState.EMPTY_STATE) {
				site.setInternalState(abstractSite.getInternalState());
			}

			rootOfPlague.addSite(site);

			// set link state
			if (abstractSite.getLinkState().getStatusLink() == CLinkStatus.FREE) {
//				site.getLinkState().setStatusLink(CLinkStatus.FREE);
				site.getLinkState().setFree();
				continue;
			}

			// if there is link....
			CAgent linkedAgent = new CAgent(abstractSite.getLinkState()
					.getAgentNameID(), NOT_INITIALIZE);
			CSite linkedSite = new CSite(abstractSite.getLinkState()
					.getLinkSiteNameID(), linkedAgent);

			linkedAgent.addSite(linkedSite);
			
			site.getLinkState().connectSite(linkedSite);
			linkedSite.getLinkState().connectSite(site);

			instance.add(linkedAgent);

		}
	}

	private void initiateAgent(CAgent agent, CAbstractAgent archetype) {
		if (agent.getId() != NOT_INITIALIZE) {
			System.out.println("adfhgh");
		}
		agent.setId(numberOfInitiatedAgent);
		numberOfInitiatedAgent++;
		propagateTentacles(agent, archetype);
	}

	
	public String getHashCode() {
		return hashCode;
	}

	public List<Species> propagate() {
		if (instance.size() == numberOfInitiatedAgent) {
			return null;
		}
		List<Species> answer = new LinkedList<Species>();
		List<CAbstractAgent> masks = getMasks(instance
				.get(numberOfInitiatedAgent));

		//for example this agent doesn't presence
		if(masks==null|| masks.isEmpty()){
			return null;
		}
		for (CAbstractAgent mask : masks) {
			answer.add(clone(this, mask));
		}
		return answer;
	}

	private Species clone(Species species, CAbstractAgent mask) {
		return new Species(cloneInstance(), availableListOfViews,
				numberOfInitiatedAgent, mask);
	}

	private ArrayList<CAgent> cloneInstance() {
		ArrayList<CAgent> newAgentsList = new ArrayList<CAgent>();
		for (CAgent agent : instance) {
			CAgent newAgent = new CAgent(agent.getNameId(), agent.getId());
			for (CSite site : agent.getSites()) {
				CSite newSite = new CSite(site.getNameId(), newAgent);
				newSite.setLinkIndex(site.getLinkIndex());
				newSite.setInternalState(new CInternalState(site
						.getInternalState().getNameId()));
				newAgent.addSite(newSite);
			}
			newAgentsList.add(newAgent);
		}
		for (int i = 0; i < newAgentsList.size(); i++) {
			for (CSite siteNew : newAgentsList.get(i).getSites()) {
				CLink lsNew = siteNew.getLinkState();
				CLink lsOld = instance.get(i).getSiteByNameId(
						siteNew.getNameId()).getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getConnectedSite() != null) {
					CSite siteOldLink = lsOld.getConnectedSite();
					int j = 0;
					for (; j < instance.size(); j++) {
						if (instance.get(j) == siteOldLink.getAgentLink())
							break;
					}
					int index = j;
					lsNew.connectSite(newAgentsList.get(index).getSiteByNameId(
							siteOldLink.getNameId()));
				}
			}
		}
		return newAgentsList;
	}

	private List<CAbstractAgent> getMasks(CAgent agent) {
		List<CAbstractAgent> list = new LinkedList<CAbstractAgent>();
		CAbstractAgent abstractAgent = new CAbstractAgent(agent);
		abstractAgent.addSite(new CAbstractSite(agent.getSites().iterator()
				.next(), abstractAgent));

		if(availableListOfViews.get(agent.getNameId())==null){
			return null;
		}
		for (CAbstractAgent view : availableListOfViews.get(agent.getNameId())) {
			if (abstractAgent.isFit(view)) {
				list.add(view);
			}
		}
		return list;

	}

	public boolean isComplete() {
		return (instance.size() == numberOfInitiatedAgent);
	}
	
	public List<CAgent> getAgents(){
		return instance;
	}
}
