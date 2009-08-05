package com.plectix.simulator.stories.graphs;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

public class TestTransitivity {
	private TreeMap<Long,Set<Long>> trace; 
	private TreeMap<Long,Set<Long>> transitiveTrace; 
		
	public TestTransitivity(TreeMap<Long,Set<Long>> edges) {
		trace = edges;
	}
	
	public void test(){
		transitiveTrace = copyTrace(trace);
		transitiveTrace = warshall(transitiveTrace);
		checkTransitivity(trace, transitiveTrace);

	}

	private TreeMap<Long,Set<Long>> copyTrace(TreeMap<Long, Set<Long>> trace2) {
		TreeMap<Long,Set<Long>> newTrace = new TreeMap<Long,Set<Long>>();
		Set<Long> set;
		for (Entry<Long, Set<Long>> traceEntry : trace2.entrySet()) {
			set = new LinkedHashSet<Long>();
			for (Long i : traceEntry.getValue()) {
				set.add(i);
			}
			newTrace.put(traceEntry.getKey(), set);
		}
		return newTrace;
	}

	private TreeMap<Long,Set<Long>> warshall(TreeMap<Long,Set<Long>> trace) {
		Set<Long> set = new LinkedHashSet<Long>();
		for (Entry<Long, Set<Long>> traceEntry2 : trace.entrySet()) {
			for (Entry<Long, Set<Long>> traceEntry : trace.entrySet()) {
				for (Long w : trace.keySet()) {
					set = traceEntry.getValue();
					if(!set.contains(w) && set.contains(traceEntry2.getKey()) 
						&& traceEntry2.getValue().contains(w)){
						set.add(w);
						trace.put(traceEntry.getKey(), set);
					}
				}
			}
		}
		return trace;
	}

	private void checkTransitivity(TreeMap<Long,Set<Long>> trace2, 
								   TreeMap<Long,Set<Long>> transitiveTrace2) {
		for (Long k : trace2.keySet()) {
			for (Entry<Long, Set<Long>> traceEntry : trace2.entrySet()) {
				for (Long w : traceEntry.getValue()) {
					if(transitiveTrace2.get(traceEntry.getKey()).contains(k)
						&&transitiveTrace2.get(k).contains(w)){
							fail("the graph has a transitive relation");
					}
				}
			}
		}
	}
}
