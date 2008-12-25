package com.plectix.simulator.stories;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


public class TestTree {

	private TreeMap<Integer,List<Integer>> trace; 
	private HashMap<Integer, Integer> color;
	
	private Integer white = 0;
	private Integer grey = 1;
	private Integer black = 2;
	
	
	TestTree(TreeMap<Integer,List<Integer>> traceIdToTraceId){
		trace = traceIdToTraceId;
		color = new HashMap<Integer, Integer>();
	}
	

	public void test() {
		
		dfs();
		if (color.containsValue(white))
			fail("the graph has an isolated point");
		if (color.containsValue(grey))
			fail("the graph has a loop or nondirectional edge");
			
	}

	private void dfs(){
		for (Integer key: trace.keySet() ) {
			color.put(key, white);
		}
		dfsVisit(trace.lastKey());
	}

	private void dfsVisit(Integer key) {
		color.put(key, grey);
		for(Integer w: trace.get(key)){
			if (color.get(w) == white)
				dfsVisit(w);
			else{
				if (color.get(w) == grey) //loop
					fail("the graph has a loop or nondirectional edge");
			}
				
		}
		color.put(key, black);
	}



}
