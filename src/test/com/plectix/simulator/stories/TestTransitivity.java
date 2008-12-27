package com.plectix.simulator.stories;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
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
		Integer a;
		List<Integer> list;
		for (Integer key : trace.keySet()) {
			a = key;
			list = new ArrayList<Integer>();
			for (Integer i : trace.get(key)) {
				list.add(i);
			}
			newTrace.put(a, list);
		}
		return newTrace;
	}

	private TreeMap<Integer, List<Integer>> warshall(TreeMap<Integer,List<Integer>> trace) {
		List<Integer> list = new ArrayList<Integer>();
		for (Integer k : trace.keySet()) {
			for (Integer key : trace.keySet()) {
				for (Integer w : trace.keySet()) {
					list = new ArrayList<Integer>();
					list = trace.get(key);
					if(!list.contains(w)&&
						list.contains(k) && trace.get(k).contains(w)){
							list.add(w);
							trace.put(key, list);
					}
							
				}
			}
		}
		return trace;
	}

	private void checkTransitivity(TreeMap<Integer, List<Integer>> trace, 
								   TreeMap<Integer, List<Integer>> transitiveTrace) {
		for (Integer k : trace.keySet()) {
			for (Integer key : trace.keySet()) {
				for (Integer w : trace.get(key)) {
					if(transitiveTrace.get(key).contains(k)
						&&transitiveTrace.get(k).contains(w)){
							fail("the graph has a transitive relation");
					}
				}
			}
		}
	}
}
