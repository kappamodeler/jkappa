package com.plectix.simulator.util;
//package com.plectix.simulator.parser;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//
//import com.plectix.simulator.component.Agent;
//import com.plectix.simulator.component.ConnectedComponent;
//import com.plectix.simulator.component.InternalState;
//import com.plectix.simulator.component.LinkStatus;
//import com.plectix.simulator.component.Site;
//import com.plectix.simulator.interfaces.ConnectedComponentInterface;
//
//public class SubstanceConstructor {
//	private int myAgentIndexGenerator;
//
//	public Site createSite(String name, String internalStateName,
//			String linkIndex) {
//		Site site = new Site(name);
//		if (!internalStateName.equals("null")) {
//			site.setInternalState(new InternalState(internalStateName));
//		}
//		if (!linkIndex.equals("null")) {
//			if ("?".equals(linkIndex)) {
//				site.getLinkState().setStatusLink(LinkStatus.WILDCARD);
//			} else {
//				site.getLinkState().setStatusLink(LinkStatus.BOUND);
//				if (!"_".equals(linkIndex)) {
//					site.setLinkIndex(Integer.valueOf(linkIndex));
//				}
//			}
//		}
//		return site;
//	}
//
//	public Agent createAgent(String name, List<Site> sites) {
//		Agent agent = new Agent(name, myAgentIndexGenerator++);
//		for (Site site : sites) {
//			agent.addSite(site);
//		}
//		return agent;
//	}
//
//	private void bound(Site site1, Site site2) {
//		site1.getLinkState().connectSite(site2);
//		site2.getLinkState().connectSite(site1);
//	}
//
//	public ConnectedComponentInterface createCC(List<Agent> list) {
//		ConnectedComponentInterface cc = new ConnectedComponent(list);
//		LinkedHashMap<Integer, Site> map = new LinkedHashMap<Integer, Site>();
//		for (Agent agent : cc.getAgents()) {
//			for (Site site : agent.getSites()) {
//				int linkIndex = site.getLinkIndex();
//				if (linkIndex != -1) {
//					Site boundedSite = map.get(linkIndex);
//					if (boundedSite != null) {
//						bound(site, boundedSite);
//						map.remove(linkIndex);
//					} else {
//						map.put(linkIndex, site);
//					}
//				}
//			}
//		}
//		return cc;
//	}
//}
