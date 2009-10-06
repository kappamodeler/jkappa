package com.plectix.simulator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.LinkRank;
import com.plectix.simulator.component.LinkStatus;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;

public class Converter {
	public static String toString(Site site) {
		StringBuffer sb = new StringBuffer();
		sb.append(site.getName());
		if (!site.getInternalState().hasDefaultName()) {
			sb.append("~" + site.getInternalState().getName());
		}
		if (site.getLinkState().getStatusLinkRank() == LinkRank.SEMI_LINK) {
			sb.append("!_");
		} else if (site.getLinkIndex() != -1) {
			sb.append("!" + site.getLinkIndex());
		} else if (site.getLinkState().getStatusLink() == LinkStatus.WILDCARD) {
			sb.append("?");
		}
		return sb.toString();
	}

	public static String toString(Agent agent) {
		StringBuffer sb = new StringBuffer();
		sb.append(agent.getName());
		sb.append("(");
		boolean first = true;

		TreeMap<String, Site> sites = new TreeMap<String, Site>();
		for (Site site : agent.getSites()) {
			sites.put(site.getName(), site);
		}

		for (Site site : sites.values()) {
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

	public static String toString(ConnectedComponentInterface c) {
		StringBuffer sb = new StringBuffer();
		if (c == null)
			return "null";
		boolean first = true;
		Map<String, List<Agent>> agents = new TreeMap<String, List<Agent>>();
		Agent empty = new Agent();
		List<Agent> list;
		String agentString;
		for (Agent agent : c.getAgents()) {
			if (empty.equalz(agent)) {
				return "";
			}

			agentString = toString(agent).intern();

			list = agents.get(agentString);
			if (list == null) {
				list = new ArrayList<Agent>();
				agents.put(agentString, list);
			}
			list.add(agent);

		}

		for (List<Agent> agentList : agents.values()) {
			for (Agent agent : agentList) {
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
