package com.plectix.simulator.stories.graphs;

import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;


public class TestTree {

	private TreeMap<Long, Set<Long>> trace; 
	private LinkedHashMap<Long, Byte> color;
	
	private Byte white = 0x00;
	private Byte grey = 0x01;
	private Byte black = 0x02;
	
	
	TestTree(TreeMap<Long, Set<Long>> edges){
		trace = edges;
		color = new LinkedHashMap<Long, Byte>();
	}
	

	public void test() {
		
		dfs();
		if (color.containsValue(white))
			fail("the graph has an isolated point");
		if (color.containsValue(grey))
			fail("the graph has a loop or nondirectional edge");
			
	}

	private void dfs(){
		for (Long key: trace.keySet() ) {
			color.put(key, white);
		}
		dfsVisit(trace.lastKey());
	}

	private void dfsVisit(Long key) {
		color.put(key, grey);
		for(Long w: trace.get(key)){
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
