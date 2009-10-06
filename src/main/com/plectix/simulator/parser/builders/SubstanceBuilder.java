package com.plectix.simulator.parser.builders;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.InternalState;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelSite;
import com.plectix.simulator.simulator.KappaSystemInterface;
import com.plectix.simulator.util.NameDictionary;

/*package*/ public final class SubstanceBuilder {
	private final KappaSystemInterface kappaSystem;

	public SubstanceBuilder(KappaSystemInterface system) {
		this.kappaSystem = system;
	}

	public final List<Agent> buildAgents(List<ModelAgent> abstractAgents) {
		if (abstractAgents == null) {
			return null;
		}

		List<Agent> actualAgents = new LinkedList<Agent>();
		
		for (ModelAgent agent : abstractAgents) {
			Agent newAgent = this.convertAgent(agent);
			actualAgents.add(newAgent);
		}

		Map<Integer, Site> map = new LinkedHashMap<Integer, Site>();
		for (Agent agent : actualAgents) {
			for (Site site : agent.getSites()) {
				int index = site.getLinkIndex();
				if (index == -1) {
					continue;
				}
				Site connectedSite = map.get(index);
				if (connectedSite == null) {
					map.put(index, site);
				} else {
					connectedSite.getLinkState().connectSite(site);
					site.getLinkState().connectSite(connectedSite);
					map.remove(site.getLinkIndex());
				}
			}
		}
		
		return actualAgents;
	}

	private final Agent convertAgent(ModelAgent abstractAgent) {
		Agent resultAgent = new Agent(abstractAgent.getName(), kappaSystem.generateNextAgentId());
		for (ModelSite site : abstractAgent.getSites()) {
			Site newSite = convertSite(site);
			resultAgent.addSite(newSite);
		} 
		
		return resultAgent;
	}
	
	private final Site convertSite(ModelSite abstractSite) {
		Site newSite = new Site(abstractSite.getName());
		newSite.getLinkState().setStatusLink(abstractSite.getLinkState().getStatusLink());
		newSite.setLinkIndex(abstractSite.getLinkIndex());
		String internalStateName = abstractSite.getInternalStateName();
		if (!NameDictionary.isDefaultInternalStateName(internalStateName)) {
			newSite.setInternalState(new InternalState(internalStateName));
		}
		return newSite;
	}
}
