package com.plectix.simulator.stories;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TestTransitivity {
	private TreeMap<Integer,List<Integer>> trace; 
	private TreeMap<Integer,List<Integer>> transitiveTrace; 
		
	public TestTransitivity(TreeMap<Integer,List<Integer>> traceIdToTraceId) {
		trace = traceIdToTraceId;
	}
	
	public void test(){
		transitiveTrace = copyTrace(trace);
		transitiveTrace = warshall(transitiveTrace);
		checkTransitivity(trace, transitiveTrace);

	}

	private TreeMap<Integer,List<Integer>> copyTrace(TreeMap<Integer,List<Integer>> trace) {
		TreeMap<Integer,List<Integer>> newTrace = new TreeMap<Integer, List<Integer>>();
		List<Integer> list;
		for (Map.Entry<Integer, List<Integer>> traceEntry : trace.entrySet()) {
			list = new ArrayList<Integer>();
			for (Integer i : traceEntry.getValue()) {
				list.add(i);
			}
			newTrace.put(traceEntry.getKey(), list);
		}
		return newTrace;
	}

	private TreeMap<Integer, List<Integer>> warshall(TreeMap<Integer,List<Integer>> trace) {
		List<Integer> list = new ArrayList<Integer>();
		for (Map.Entry<Integer, List<Integer>> traceEntry2 : trace.entrySet()) {
			for (Map.Entry<Integer, List<Integer>> traceEntry : trace.entrySet()) {
				for (Integer w : trace.keySet()) {
					list = traceEntry.getValue();
					if(!list.contains(w) && list.contains(traceEntry2.getKey()) 
						&& traceEntry2.getValue().contains(w)){
						list.add(w);
						trace.put(traceEntry.getKey(), list);
						
					}
				}
			}
		}
		return trace;
	}

	private void checkTransitivity(TreeMap<Integer, List<Integer>> trace, 
								   TreeMap<Integer, List<Integer>> transitiveTrace) {
		for (Integer k : trace.keySet()) {
			for (Map.Entry<Integer, List<Integer>> traceEntry : trace.entrySet()) {
				for (Integer w : traceEntry.getValue()) {
					if(transitiveTrace.get(traceEntry.getKey()).contains(k)
						&&transitiveTrace.get(k).contains(w)){
							fail("the graph has a transitive relation");
					}
				}
			}
		}
	}
}
