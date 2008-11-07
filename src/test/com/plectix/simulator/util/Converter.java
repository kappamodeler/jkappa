package com.plectix.simulator.util;

import com.plectix.simulator.components.*;
import java.util.*;

public class Converter {
	public static String toString(CSite site) {
		StringBuffer sb = new StringBuffer();
		sb.append(site.getName());
		if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
			sb.append("~" + site.getInternalState().getName());
		}
		if (site.getLinkState().getStatusLinkRank() == CLinkState.RANK_SEMI_LINK) {
			sb.append("!_");
		} else if (site.getLinkIndex() != -1) {
			sb.append("!" + site.getLinkIndex());
		} else if (site.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_WILDCARD) {
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
		
		for (String name : sites.keySet()) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(toString(sites.get(name)));
		}
		sb.append(")");
		return sb.toString();
	}
	
	public static String toString(CConnectedComponent c) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		TreeMap<String, List<CAgent>> agents = new TreeMap<String, List<CAgent>>();
		CAgent empty = new CAgent(CAgent.EMPTY);
		List<CAgent> list;
		String agentString; 
		for (CAgent agent : c.getAgents()) {
			if (empty.equals(agent)) {
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
		
		for (String name : agents.keySet()) {
			for (CAgent agent : agents.get(name)) {
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
