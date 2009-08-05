package com.plectix.simulator.components.stories.storage.graphs;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

public class Connections {
	
	private Set<Connection> connections;
	private TreeMap<Long, Set<Long>> adjacentEdges;
	
	
	public Connections() {
		connections = new LinkedHashSet<Connection>();
		adjacentEdges = new TreeMap<Long, Set<Long>>();
	}
	
	public void addConnection(Long from, Long to){
		if (checkTansitive(from, to)){
			connections.add(new Connection(from, to));
			if (!adjacentEdges.containsKey(from)){
				adjacentEdges.put(from, new LinkedHashSet<Long>());
			}
			adjacentEdges.get(from).add(to);
		}
	}

	private boolean checkTansitive(Long from, Long to) {
		if(!adjacentEdges.containsKey(from))
			return true;
		if(adjacentEdges.get(from).contains(to))
			return false;
		for (Long toNode : adjacentEdges.get(from)) {
			if (!checkTansitive(toNode, to))
				return false;
		}
		return true;
	}

	
	public Set<Connection> getConnections() {
		return connections;
	}
	public TreeMap<Long, Set<Long>> getAdjacentEdges() {
		return adjacentEdges;
	}
}
