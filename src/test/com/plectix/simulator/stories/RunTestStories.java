package com.plectix.simulator.stories;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.stories.CStoryTrees;
import com.plectix.simulator.interfaces.IRule;


@RunWith(value = Parameterized.class)
public class RunTestStories extends InitStoriesTests{
	private TreeMap<Integer,List<Integer>> trace; 
	private List<IRule> rules;
	
	private static final String testFileNamePrefix = "test.data/stories/";
	private static final String results = "results/";
	private String FilePath = "";
	private static String mode = "simple/";
	@Override
	public String getPrefixFileName() {
		return testFileNamePrefix + mode;
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(testFileNamePrefix + mode);
	}

	public RunTestStories(String testFilePath) {
		super(testFilePath);
		FilePath = testFilePath;
	}
	
	@Test
	public void testTree(){
		Collection<List<CStoryTrees>> trees = getStories().getTrees();
		rules=getRules();
		TestStories stories = new TestStories(trees, rules, testFileNamePrefix + results + FilePath);
		stories.test();
		
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
