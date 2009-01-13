package com.plectix.simulator.stories;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.CStoryTrees;
import com.plectix.simulator.components.CStoryType;

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
				
				TestTree tree = new TestTree(trace);
				tree.test();
					
				TestTransitivity transitivity = new TestTransitivity(trace);
				transitivity.test();
				
				TestIntro intros = new TestIntro(storyTrees, trace);
				intros.test();
			}
		}
	}
}
