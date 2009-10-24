package com.plectix.simulator.stories.graphs;

import java.io.File;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.staticanalysis.stories.graphs.MergeStoriesGraphs;
import com.plectix.simulator.staticanalysis.stories.graphs.UniqueGraph;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.stories.InitStoriesTests;

@RunWith(value = Parameterized.class)
public class TestStoryTrees extends InitStoriesTests {

	private static final String separator = File.separator;
	private static final String testFileNamePrefix = "test.data" + separator
			+ "stories" + separator;

	// private String filePath = "";

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(testFileNamePrefix);
	}

	public TestStoryTrees(String testFilePath) {
		super(testFileNamePrefix, testFilePath, false, false, false, true);
	}

	@Test
	public void testStoryTrees() throws StoryStorageException {
		MergeStoriesGraphs merging = new MergeStoriesGraphs(getStories());
		merging.merge();

		AbstractList<UniqueGraph> graphs = merging.getListUniqueGraph();
		TreeMap<Long, Set<Long>> edges;
		for (UniqueGraph g : graphs) {
			edges = g.getGraph().getConnections2().getAdjacentEdges();

			TestTree tree = new TestTree(edges);
			tree.test();

			TestTransitivity transitivity = new TestTransitivity(edges);
			transitivity.test();

			TestIntro intros = new TestIntro(g.getGraph());
			intros.test();
		}
	}

}
