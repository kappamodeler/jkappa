package com.plectix.simulator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IConnectedComponent;

public class Converter {
	public static String toString(CSite site) {
		StringBuffer sb = new StringBuffer();
		sb.append(site.getName());
		if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
			sb.append("~" + site.getInternalState().getName());
		}
		if (site.getLinkState().getStatusLinkRank() == CLinkRank.SEMI_LINK) {
			sb.append("!_");
		} else if (site.getLinkIndex() != -1) {
			sb.append("!" + site.getLinkIndex());
		} else if (site.getLinkState().getStatusLink() == CLinkStatus.WILDCARD) {
			sb.append("?");
		}
		return sb.toString();
	}
	
	public static String toString(CAgent agent) {
		StringBuffer sb = new StringBuffer();
		sb.append(agent.getName());
		sb.append("(");
		boolean first = true;
		
		TreeMap<String, CSite> sites = new TreeMap<String, CSite>();
		for (CSite site : agent.getSites()) {
			sites.put(site.getName(), site);
		}
		
		for (CSite site : sites.values()) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(toString(site));
		}
		sb.append(")");
		return sb.toString();
	}
	
	public static String toString(IConnectedComponent c) {
		StringBuffer sb = new StringBuffer();
		if (c==null)
			return "null";
		boolean first = true;
		Map<String, List<CAgent>> agents = new TreeMap<String, List<CAgent>>();
		CAgent empty = new CAgent();
		List<CAgent> list;
		String agentString; 
		for (CAgent agent : c.getAgents()) {
			if (empty.equalz(agent)) {
				return "";
			}
			
			agentString = toString(agent).intern();
			
			list = agents.get(agentString);
			if (list == null) {
				list = new ArrayList<CAgent>();
				agents.put(agentString, list);
			}
			list.add(agent);
			
		}
		
		for (List<CAgent> agentList : agents.values()) {
			for (CAgent agent : agentList) {
				if (!first) {
					sb.append(", ");
				} else {
					first = false;
				}
				sb.append(toString(agent));
			}
		}
		return sb.toString();
	}
}
