package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractSite;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ class SubstanceBuilder {
	private final KappaSystem myKappaSystem;

	public SubstanceBuilder(KappaSystem system) {
		myKappaSystem = system;
	}

	private IAgent buildAgent(AbstractAgent agent) {
		CAgent resultAgent = new CAgent(agent.getNameId(), myKappaSystem.generateNextAgentId());
		for (AbstractSite site : agent.getSites()) {
			ISite newSite = buildSite(site);
			resultAgent.addSite(newSite);
		} 
		
		return resultAgent;
	}

	public List<IAgent> buildAgents(List<AbstractAgent> agents) {
		if (agents == null) {
			return null;
		}

		List<IAgent> result = new LinkedList<IAgent>();
		
		for (AbstractAgent agent : agents) {
			IAgent newAgent = buildAgent(agent);
			result.add(newAgent);
		}

		Map<Integer, ISite> map = new HashMap<Integer, ISite>();
		for (IAgent agent : result) {
			for (ISite site : agent.getSiteMap().values()) {
				int index = site.getLinkIndex();
				if (index == -1) {
					continue;
				}
				ISite connectedSite = map.get(index);
				if (connectedSite == null) {
					map.put(index, site);
				} else {
					connectedSite.getLinkState().setSite(site);
					site.getLinkState().setSite(connectedSite);
					map.remove(site.getLinkIndex());
				}
			}
		}
		
		return result;
	}

	private ISite buildSite(AbstractSite site) {
		ISite newSite = new CSite(site.getNameId());
		newSite.getLinkState().setStatusLink(site.getLinkState().getStatusLink());
		newSite.setLinkIndex(site.getLinkIndex());
		int internalStateNameId = site.getInternalStateNameId();
		if (internalStateNameId != CSite.NO_INDEX) {
			newSite.setInternalState(new CInternalState(internalStateNameId));
		}
		return newSite;
	}
}
