package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractSite;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ class SubstanceBuilder {
	private final KappaSystem myKappaSystem;

	public SubstanceBuilder(KappaSystem system) {
		myKappaSystem = system;
	}

	private CAgent buildAgent(AbstractAgent agent) {
		CAgent resultAgent = new CAgent(agent.getNameId(), myKappaSystem.generateNextAgentId());
		for (AbstractSite site : agent.getSites()) {
			CSite newSite = buildSite(site);
			resultAgent.addSite(newSite);
		} 
		
		return resultAgent;
	}

	public List<CAgent> buildAgents(List<AbstractAgent> agents) {
		if (agents == null) {
			return null;
		}

		List<CAgent> result = new LinkedList<CAgent>();
		
		for (AbstractAgent agent : agents) {
			CAgent newAgent = buildAgent(agent);
			result.add(newAgent);
		}

		Map<Integer, CSite> map = new HashMap<Integer, CSite>();
		for (CAgent agent : result) {
			for (CSite site : agent.getSites()) {
				int index = site.getLinkIndex();
				if (index == -1) {
					continue;
				}
				CSite connectedSite = map.get(index);
				if (connectedSite == null) {
					map.put(index, site);
				} else {
					connectedSite.getLinkState().connectSite(site);
					site.getLinkState().connectSite(connectedSite);
					map.remove(site.getLinkIndex());
				}
			}
		}
		
		return result;
	}

	private CSite buildSite(AbstractSite site) {
		CSite newSite = new CSite(site.getNameId());
		newSite.getLinkState().setStatusLink(site.getLinkState().getStatusLink());
		newSite.setLinkIndex(site.getLinkIndex());
		int internalStateNameId = site.getInternalStateNameId();
		if (internalStateNameId != CSite.NO_INDEX) {
			newSite.setInternalState(new CInternalState(internalStateNameId));
		}
		return newSite;
	}
}
