package com.plectix.simulator.rulecompression.util;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.rulecompression.SitePath;

/*package*/ class PathNode {
	public final CAgent currentSubtreeRoot;
	public CAgent mainTreeRoot;
	public final List<PathNode> children = new ArrayList<PathNode>();
	
	public PathNode(CAgent root, PathFinder pf) {
		this.currentSubtreeRoot = root;
		this.mainTreeRoot = root;
		for (CSite site : root.getSites()) {
			CSite connectedOne = site.getLinkState().getConnectedSite();
			if (connectedOne != null) {
				CAgent agent = connectedOne.getParentAgent();
				pf.spotLinkIndex(connectedOne.getLinkIndex());
				children.add(new PathNode(mainTreeRoot, agent, pf));
			}
		}
	}
	
	private PathNode(CAgent rootOverAll, CAgent root, PathFinder pf) {
		this.currentSubtreeRoot = root;
		this.mainTreeRoot = rootOverAll;
		for (CSite site : root.getSites()) {
			CSite connectedOne = site.getLinkState().getConnectedSite();
			if (connectedOne != null) {
				CAgent agent = connectedOne.getParentAgent();
				if (!pf.getSpottedAgents().contains(connectedOne.getLinkIndex())) {
					pf.spotLinkIndex(connectedOne.getLinkIndex());
					children.add(new PathNode(mainTreeRoot, agent, pf));
				}
			}
		}
	}
	
	/**
	 * This method searches for the path from the root agent to a fixed site
	 * @param site fixed site
	 * @return a sequence of sites, defining the path from root to the site IN REVERSED ORDER
	 */
	public SitePath getPath(CSite site) {
		if (site.getParentAgent() == this.currentSubtreeRoot) {
			SitePath sp = new SitePath(this.mainTreeRoot);
			sp.addSite(site);
			return sp;
		} else {
			for (PathNode pn : children) {
				SitePath previous = pn.getPath(site);
				if (previous != null) {
					for (CSite siteOfRoot : currentSubtreeRoot.getSites()) {
						CSite connectedOne = siteOfRoot.getLinkState().getConnectedSite();
						if (connectedOne != null && connectedOne.getParentAgent() == pn.currentSubtreeRoot) {
							previous.addSite(connectedOne);
							previous.addSite(siteOfRoot);
							break;
						}
					}
					return previous;
				}
			}
		}
		// == null if and only if root and site are in different components
		return null;
	}
	
	/**
	 * this method finds path to agent
	 * this is the path to it's incoming site, so the last element SiteInfo
	 * should contain name of the agent as agentName field.
	 * if the path is empty then agent equals to one root.
	 * @param agent
	 * @return
	 */
	public SitePath getPath(CAgent agent) {
		SitePath sp = getPath(agent.getDefaultSite());
		return sp;
	}
}
