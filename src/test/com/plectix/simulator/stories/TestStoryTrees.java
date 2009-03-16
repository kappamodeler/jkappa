package com.plectix.simulator.stories;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.stories.CStoryTrees;


@RunWith(value = Parameterized.class)
public class TestStoryTrees extends InitStoriesTests{

	private TreeMap<Integer,List<Integer>> trace; 
	private static final String testFileNamePrefix = "test.data/stories/";
	private String filePath = "";


	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(testFileNamePrefix);
	}

	public TestStoryTrees(String testFilePath) {
		super(testFileNamePrefix, testFilePath);
		filePath = testFilePath;
	}
	
	@Test
	public void testStoryTrees(){
		
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
