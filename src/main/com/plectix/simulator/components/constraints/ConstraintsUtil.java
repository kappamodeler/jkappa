package com.plectix.simulator.components.constraints;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.*;

/*package*/ class ConstraintsUtil {
	public static boolean agentsNamesAreNotIntersected(IConnectedComponent cc1, 
			IConnectedComponent cc2) {
		Collection<CAgent> cc1Agents = cc1.getAgents(); 
		Collection<CAgent> cc2Agents = cc2.getAgents();
		Set<String> cc1AgentsNames = new HashSet<String>();
		Set<String> cc2AgentsNames = new HashSet<String>();
		
		for (CAgent agent1 : cc1Agents) {
			cc1AgentsNames.add(agent1.getName());
		}
		
		for (CAgent agent2 : cc2Agents) {
			cc2AgentsNames.add(agent2.getName());
		}
		
		cc1AgentsNames.retainAll(cc2AgentsNames);
		
		return cc1AgentsNames.isEmpty(); 
	}
}
