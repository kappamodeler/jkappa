package com.plectix.simulator.components.stories.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.stories.AgentSites;
import com.plectix.simulator.components.stories.CNetworkNotation;
import com.plectix.simulator.interfaces.IStoriesSiteStates;

public class CWireMap {
	private HashMap<UHashKey, CWire> wiresMap;

	public CWireMap(List<CNetworkNotation> listNetworkNotations) {
		this.wiresMap = new HashMap<UHashKey, CWire>();
		for (CNetworkNotation nn : listNetworkNotations) {
			initNetworcNotation(nn);
		}

	}

	private void initNetworcNotation(CNetworkNotation nn) {
		for (Map.Entry entryAgent : nn.getChangesOfAllUsedSites().entrySet()) {
			Long agentNameId = (Long) entryAgent.getKey();
			AgentSites as = (AgentSites) entryAgent.getValue();
			for (Map.Entry enterySite : as.getSites().entrySet()) {
				Integer siteNameId = (Integer) enterySite.getKey();
				IStoriesSiteStates siteStates = (IStoriesSiteStates) enterySite
						.getValue();
				checkInternalState(nn.getStep(), siteStates, agentNameId,
						siteNameId);
				checkLinkState(nn.getStep(), siteStates, agentNameId,
						siteNameId);
			}
		}
	}

	private void checkLinkState(int step, IStoriesSiteStates siteStates,
			Long agentNameId, Integer siteNameId) {
		UHashKey key = new UHashKey(agentNameId, siteNameId,
				EKeyOfState.LINK_STATE);
		CWire wire = wiresMap.get(key);
		if (wire == null) {
			wire = new CWire(agentNameId, siteNameId, EKeyOfState.LINK_STATE);
			wiresMap.put(key, wire);
		}
		wire.addEvent(new CEventLS(step, siteStates));
	}

	private void checkInternalState(long step, IStoriesSiteStates siteStates,
			Long agentNameId, Integer siteNameId) {
		if (((siteStates.getBeforeState() != null) && (siteStates
				.getBeforeState().getIdInternalState() != -1))
				|| ((siteStates.getAfterState() != null) && (siteStates
						.getAfterState().getIdInternalState() != -1))) {
			UHashKey key = new UHashKey(agentNameId, siteNameId,
					EKeyOfState.INTERNAL_STATE);
			CWire wire = wiresMap.get(key);
			if (wire == null) {
				wire = new CWire(agentNameId, siteNameId,
						EKeyOfState.INTERNAL_STATE);
				wiresMap.put(key, wire);
			}
			wire.addEvent(new CEventIS(step, siteStates));
		}
	}
}
