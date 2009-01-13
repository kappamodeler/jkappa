package com.plectix.simulator.util;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import java.util.*;

public class Converter {
	public static String toString(ISite site) {
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
	
	public static String toString(IAgent agent) {
		StringBuffer sb = new StringBuffer();
		sb.append(agent.getName());
		sb.append("(");
		boolean first = true;
		
		TreeMap<String, ISite> sites = new TreeMap<String, ISite>();
		for (ISite site : agent.getSites()) {
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
	
	public static String toString(IConnectedComponent c) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		TreeMap<String, List<IAgent>> agents = new TreeMap<String, List<IAgent>>();
		IAgent empty = new CAgent(CAgent.EMPTY, CAgent.EMPTY);
		List<IAgent> list;
		String agentString; 
		for (IAgent agent : c.getAgents()) {
			if (empty.equalz(agent)) {
				return "";
			}
			
			agentString = toString(agent).intern();
			
			list = agents.get(agentString);
			if (list == null) {
				list = new ArrayList<IAgent>();
				agents.put(agentString, list);
			}
			list.add(agent);
			
		}
		
		for (String name : agents.keySet()) {
			for (IAgent agent : agents.get(name)) {
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
