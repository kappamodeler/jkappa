package com.plectix.simulator.util;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISite;

public class CComponentComparator {
	
	public boolean compareListsCC(List<IConnectedComponent> list1, 
			List<IConnectedComponent> list2){
		IConnectedComponent foundedCC = null;
		if (list1 != null){
			if (list1.size() != list2.size())
				return false;
			
			for (IConnectedComponent cc : list1) {
				foundedCC = findCC(cc,list2);
				if (foundedCC == null)
					return false;
				else {
					list2.remove(foundedCC);
					continue;
				}
			}
		} else if(list2 == null)
			return true;
		
		if (list2.isEmpty())
			return true;
		return false;
	}
	
	
	public static IConnectedComponent findCC(IConnectedComponent c,
			List<IConnectedComponent> list) {
		int size = c.getAgents().size();
		for (IConnectedComponent tmpC : list) {
			if (tmpC.getAgents().size() == size) {
				if (compareCC(c, tmpC))
					return tmpC;
			}
		}
		return null;
	}

	private static boolean compareCC(IConnectedComponent cCRight,
			IConnectedComponent cc) {
		List<IAgent> listCC = new ArrayList<IAgent>();
		listCC = cCRight.getAgents();
		for (IAgent agent : listCC) {
			if (!findAgentInCC(agent, cc)) {
				return false;
			}
		}
		listCC = cc.getAgents();
		for (IAgent agent : listCC) {
			if (!findAgentInCC(agent, cCRight)) {

				return false;
			}
		}
		return true;
	}

	private static boolean findAgentInCC(IAgent agentToFind, IConnectedComponent cc) {
		String name = agentToFind.getName();
		for (IAgent agent2 : cc.getAgents()) {
			if (name.equals(agent2.getName())) {
				if (checkSites(agentToFind, agent2)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkSites(IAgent agent1, IAgent agent2) {
		boolean flag = false;
		Collection<ISite> agent1Sites = new ArrayList<ISite>();
		Collection<ISite> agent2Sites = new ArrayList<ISite>();
		agent1Sites = agent1.getSites();
		agent2Sites = agent2.getSites();
		String sName;
		int sIntState;
		for (ISite site1 : agent1Sites) {
			flag = false;
			sName = site1.getName();
			sIntState = site1.getInternalState().getNameId();
			for (ISite site2 : agent2Sites) {
				if (sName.equals(site2.getName())
						&& (site2.getInternalState().getNameId() == sIntState)
						&& (site2.getLinkIndex() == site1.getLinkIndex())) {
					flag = true;
					break;
				}
			}
			if (!flag)
				return false;
		}
		return flag;
	}
}
