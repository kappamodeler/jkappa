package com.plectix.simulator.stories;

import static org.junit.Assert.fail;

import java.util.*;

import com.plectix.simulator.components.stories.*;
import com.plectix.simulator.components.stories.CStoryType.StoryOutputType;

public class TestIntro {

	private TreeMap<Integer, List<Integer>> trace;
	private CStoryTrees storyTree;

	TestIntro(CStoryTrees storyTrees,
			TreeMap<Integer, List<Integer>> traceIdToTraceId) {
		storyTree = storyTrees;
		trace = traceIdToTraceId;
	}

	public void test() {
		buildIntroMap(storyTree);
	}

	private final void buildIntroMap(CStoryTrees storyTree) {
		HashMap<Integer, List<CStoryType>> allLevels = new HashMap<Integer, List<CStoryType>>();
		HashMap<Integer, List<CStoryType>> traceIdToStoryTypeIntro = new HashMap<Integer, List<CStoryType>>();

		int counter = 0;
		int depth = storyTree.getLevelToTraceID().size();

		List<CStoryIntro> storyIntroList = storyTree.getStoryIntros();
		for (CStoryIntro stIntro : storyIntroList) {
			for (Integer traceID : stIntro.getTraceIDs()) {

				List<CStoryType> introList = traceIdToStoryTypeIntro
						.get(traceID);

				if (introList == null) {
					introList = new ArrayList<CStoryType>();
					traceIdToStoryTypeIntro.put(traceID, introList);
				}
				int level = storyTree.getTraceIDToLevel().get(traceID);
				CStoryType stT = new CStoryType(StoryOutputType.INTRO, traceID,
						counter, "intro:" + stIntro.getNotation(), "", depth
								- level - 1);
				introList.add(stT);

				List<CStoryType> listST = allLevels.get(level);
				if (listST == null) {
					listST = new ArrayList<CStoryType>();
					allLevels.put(level, listST);
				}
				listST.add(stT);
			}
			counter++;
		}
		
//		checkIntroId(traceIdToStoryTypeIntro);
		checkLeaves(traceIdToStoryTypeIntro);
	}

	private void checkLeaves(HashMap<Integer, List<CStoryType>> introMap) {
		for (Map.Entry<Integer, List<Integer>> entry : trace.entrySet()) {
			if (entry.getValue().isEmpty()) {
				if (!introMap.keySet().contains(entry.getKey()))
					fail("leaf is empty");
			}
		}

	}

	private void checkIntroId(HashMap<Integer, List<CStoryType>> introMap) {
		List<Integer> introList = new ArrayList<Integer>();
		for (List<CStoryType> introValue : introMap.values()) {
			for (CStoryType intro : introValue) {
				if (!introList.contains(intro.getId())) {
					introList.add(intro.getId());
				} else{
					fail("intro id");
				}
			}
		}
	}

}
