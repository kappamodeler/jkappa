package com.plectix.simulator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.string.ConnectedComponentToSmilesString;
import com.plectix.simulator.interfaces.IConnectedComponent;

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
			Collection<IConnectedComponent> list) {
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
//		List<CAgent> listCC = new ArrayList<CAgent>();
//		listCC = cCRight.getAgents();
//		for (CAgent agent : listCC) {
//			if (!findAgentInCC(agent, cc)) {
//				return false;
//			}
//		}
//		listCC = cc.getAgents();
//		for (CAgent agent : listCC) {
//			if (!findAgentInCC(agent, cCRight)) {
//
//				return false;
//			}
//		}
//		return true;
		String firstString = ConnectedComponentToSmilesString.getInstance().toUniqueString(cCRight);
		String secondString = ConnectedComponentToSmilesString.getInstance().toUniqueString(cc);
		if (firstString != null) {
			return firstString.equals(secondString);
		} else {
			return secondString == null;
		}
	}

	private static boolean findAgentInCC(CAgent agentToFind, IConnectedComponent cc) {
		String name = agentToFind.getName();
		for (CAgent agent2 : cc.getAgents()) {
			if (name.equals(agent2.getName())) {
				if (checkSites(agentToFind, agent2)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkSites(CAgent agent1, CAgent agent2) {
		boolean flag = false;
		Collection<CSite> agent1Sites = new ArrayList<CSite>();
		Collection<CSite> agent2Sites = new ArrayList<CSite>();
		agent1Sites = agent1.getSites();
		agent2Sites = agent2.getSites();
		String sName;
		int sIntState;
		for (CSite site1 : agent1Sites) {
			flag = false;
			sName = site1.getName();
			sIntState = site1.getInternalState().getNameId();
			for (CSite site2 : agent2Sites) {
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
