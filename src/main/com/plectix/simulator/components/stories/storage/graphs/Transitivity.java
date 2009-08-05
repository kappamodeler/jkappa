package com.plectix.simulator.components.stories.storage.graphs;

import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Transitivity {

	
	private final TreeMap<Long,LinkedHashSet<Long>> trace;
	private TreeMap<Long,LinkedHashSet<Long>> transitiveTrace;
	private TreeMap<Long,LinkedHashSet<Long>> newTrace;
	
	
	public Transitivity(TreeMap<Long,LinkedHashSet<Long>> storyTrace) {
		this.trace = storyTrace;
	}
	
	private void cloneTrace() {
		transitiveTrace = new TreeMap<Long, LinkedHashSet<Long>>();
		newTrace = new TreeMap<Long, LinkedHashSet<Long>>();
		LinkedHashSet<Long> list;
		for (Entry<Long, LinkedHashSet<Long>> traceEntry : trace.entrySet()) {
			list = new LinkedHashSet<Long>();
			for (Long i : traceEntry.getValue()) {
				list.add(i);
			}
			transitiveTrace.put(traceEntry.getKey(), list);
			newTrace.put(traceEntry.getKey(), list);
		}
	}

	private TreeMap<Long, LinkedHashSet<Long>> warshall() {
		LinkedHashSet<Long> list = new LinkedHashSet<Long>();
		for (Entry<Long, LinkedHashSet<Long>> traceEntry2 : transitiveTrace.entrySet()) {
			for (Entry<Long, LinkedHashSet<Long>> traceEntry : transitiveTrace.entrySet()) {
				for (Long w : transitiveTrace.keySet()) {
					list = traceEntry.getValue();
					if(!list.contains(w) && list.contains(traceEntry2.getKey()) 
						&& traceEntry2.getValue().contains(w)){
						list.add(w);
						transitiveTrace.put(traceEntry.getKey(), list);
					}
				}
			}
		}
		return transitiveTrace;
	}

	public void killTransitivity() {
		
		for (Long k : trace.keySet()) {
			for (Entry<Long, LinkedHashSet<Long>> traceEntry : trace.entrySet()) {
				for (Long w : traceEntry.getValue()) {
					if(transitiveTrace.get(traceEntry.getKey()).contains(k)
						&& transitiveTrace.get(k).contains(w)){
//							fail("the graph has a transitive relation");
						
					}
				}
			}
		}
	}
}
