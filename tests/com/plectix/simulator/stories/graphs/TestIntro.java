package com.plectix.simulator.stories.graphs;

import com.plectix.simulator.staticanalysis.stories.graphs.StoriesGraphs;

public class TestIntro {

	private StoriesGraphs storiesGraph;

	TestIntro(StoriesGraphs storiesGraph) {
		this.storiesGraph = storiesGraph;
	}

	public void test() {
		// buildIntroMap(storyTree);
	}

	// private final void buildIntroMap(CStoryTrees storyTree) {
	// LinkedHashMap<Integer, List<CStoryType>> allLevels = new
	// LinkedHashMap<Integer, List<CStoryType>>();
	// LinkedHashMap<Integer, List<CStoryType>> traceIdToStoryTypeIntro = new
	// LinkedHashMap<Integer, List<CStoryType>>();
	//
	// int counter = 0;
	// int depth = storyTree.getLevelToTraceID().size();
	//
	// List<CStoryIntro> storyIntroList = storyTree.getStoryIntros();
	// for (CStoryIntro stIntro : storyIntroList) {
	// for (Integer traceID : stIntro.getTraceIDs()) {
	//
	// List<CStoryType> introList = traceIdToStoryTypeIntro
	// .get(traceID);
	//
	// if (introList == null) {
	// introList = new ArrayList<CStoryType>();
	// traceIdToStoryTypeIntro.put(traceID, introList);
	// }
	// int level = storyTree.getTraceIDToLevel().get(traceID);
	// CStoryType stT = new CStoryType(StoryOutputType.INTRO, traceID,
	// counter, "intro:" + stIntro.getNotation(), "", depth
	// - level - 1);
	// introList.add(stT);
	//
	// List<CStoryType> listST = allLevels.get(level);
	// if (listST == null) {
	// listST = new ArrayList<CStoryType>();
	// allLevels.put(level, listST);
	// }
	// listST.add(stT);
	// }
	// counter++;
	// }
	//		
	// // checkIntroId(traceIdToStoryTypeIntro);
	// checkLeaves(traceIdToStoryTypeIntro);
	// }
	//
	// private void checkLeaves(LinkedHashMap<Integer, List<CStoryType>>
	// introMap) {
	// for (Map.Entry<Integer, List<Integer>> entry : trace.entrySet()) {
	// if (entry.getValue().isEmpty()) {
	// if (!introMap.keySet().contains(entry.getKey()))
	// fail("leaf is empty");
	// }
	// }
	//
	// }
	//
	// private void checkIntroId(LinkedHashMap<Integer, List<CStoryType>>
	// introMap) {
	// List<Integer> introList = new ArrayList<Integer>();
	// for (List<CStoryType> introValue : introMap.values()) {
	// for (CStoryType intro : introValue) {
	// if (!introList.contains(intro.getId())) {
	// introList.add(intro.getId());
	// } else{
	// fail("intro id");
	// }
	// }
	// }
	// }

}
