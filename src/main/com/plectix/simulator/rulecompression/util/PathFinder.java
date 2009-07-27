package com.plectix.simulator.rulecompression.util;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.rulecompression.SitePath;

public class PathFinder {
	private final PathNode root;
	private Set<Integer> spottedLinkIndexes = new LinkedHashSet<Integer>();
	
	public PathFinder(CAgent root) {
		this.root = new PathNode(root, this);
	}

	public SitePath getPath(CAgent agent) {
		return root.getPath(agent);
	}

	public SitePath getPath(CSite site) {
		return root.getPath(site);
	}

	public Set<Integer> getSpottedAgents() {
		return spottedLinkIndexes;
	}

	public void spotLinkIndex(int index) {
		spottedLinkIndexes.add(index);
	}
}
