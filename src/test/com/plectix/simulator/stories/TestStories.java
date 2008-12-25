package com.plectix.simulator.stories;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.CStoryTrees;

@RunWith(value = Parameterized.class)
public class TestStories extends InitStoriesTests{
	private static final String testFileNamePrefix = "test.data/stories/";
	private String FilePath = "";
	private TreeMap<Integer,List<Integer>> trace; 

	@Override
	public String getPrefixFileName() {
		return testFileNamePrefix;
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(testFileNamePrefix);
	}

	public TestStories(String testFilePath) {
		super(testFilePath);
		FilePath = testFilePath;
	}
	
	@Test
	public void testTree(){
		Collection<List<CStoryTrees>> trees = getStories().getTrees();
		
		for (List<CStoryTrees> list : trees) {
			for (CStoryTrees storyTrees : list) {
				trace = storyTrees.getTraceIDToTraceID();
				
//				createWrongTrace();
				TestTree tree = new TestTree(trace);
				tree.test();
				
				TestTransitivity transitivity = new TestTransitivity(trace);
				transitivity.test();
			}
		}
	}
	
	private void createWrongTrace() {
		trace.clear();
		List<Integer> value = new ArrayList<Integer>();
		value.add(1);
		trace.put(2, value);
		value = new ArrayList<Integer>();
		value.add(2);
		trace.put(3, value);
		value = new ArrayList<Integer>();
		value.add(3);
		value.add(1);
//		value.add(5);
		trace.put(4, value);
		value = new ArrayList<Integer>();
		value.add(4);
		trace.put(5, value);
		value = new ArrayList<Integer>();
		trace.put(1, value);
	}
}
