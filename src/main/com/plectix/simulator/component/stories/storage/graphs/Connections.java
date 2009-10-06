package com.plectix.simulator.component.stories.storage.graphs;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

public final class Connections {

	private final Set<Connection> connections = new LinkedHashSet<Connection>();
	private final TreeMap<Long, Set<Long>> adjacentEdges = new TreeMap<Long, Set<Long>>();

	public Connections() {
	}

	public final void addConnection(long source, long target) {
		if (checkTansitive(source, target)) {
			connections.add(new Connection(source, target));
			if (!adjacentEdges.containsKey(source)) {
				adjacentEdges.put(source, new LinkedHashSet<Long>());
			}
			adjacentEdges.get(source).add(target);
		}
	}

	private final boolean checkTansitive(long source, long target) {
		if (!adjacentEdges.containsKey(source))
			return true;
		if (adjacentEdges.get(source).contains(target))
			return false;
		for (Long toNode : adjacentEdges.get(source)) {
			if (!checkTansitive(toNode, target))
				return false;
		}
		return true;
	}

	public final Set<Connection> getConnections() {
		return connections;
	}

	public final TreeMap<Long, Set<Long>> getAdjacentEdges() {
		return adjacentEdges;
	}
}
