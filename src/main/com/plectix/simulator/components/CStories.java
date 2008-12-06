package com.plectix.simulator.components;

import java.util.*;

public final class CStories {

	public static final int NUMBER_OF_SIMULATIONS = 10;

	private final List<CStory> stories;
	private final List<NetworkNotationForCurrentStory> networkNotationForCurrentStory;
	private final HashMap<Integer, List<CStoryTrees>> trees;

	// TODO separate
	private class CStory {
		private int ruleID;

		public CStory(int ruleID) {
			this.ruleID = ruleID;
		}
	}

	public CStories() {
		this.stories = new ArrayList<CStory>();
		this.trees = new HashMap<Integer, List<CStoryTrees>>();
		this.networkNotationForCurrentStory = new ArrayList<NetworkNotationForCurrentStory>();
		for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++)
			this.networkNotationForCurrentStory
					.add(new NetworkNotationForCurrentStory());
	}

	public final Collection<List<CStoryTrees>> getTrees() {
		return Collections.unmodifiableCollection(trees.values());
	}

	public final void handling(int index) {
		if (networkNotationForCurrentStory.get(index).isEndOfStory())
			networkNotationForCurrentStory.get(index).handling();
		else
			networkNotationForCurrentStory.get(index).clearList();
	}

	public final void addToStories(List<Integer> ruleIDs) {
		for (int i = 0; i < ruleIDs.size(); i++) {
			this.stories.add(new CStory(ruleIDs.get(i)));
		}
	}

	public final void addToNetworkNotationStory(int index,
			CNetworkNotation networkNotation) {
		this.networkNotationForCurrentStory.get(index)
				.addToNetworkNotationList(networkNotation);
	}

	public final boolean checkRule(int checkRuleID, int index) {
		for (CStory story : this.stories)
			if (story.ruleID == checkRuleID) {
				this.networkNotationForCurrentStory.get(index).setEndOfStory(
						true);
				return true;
			}
		return false;
	}

	public final void merge() {
		for (int i = 0; i < stories.size(); i++) {
			int key = stories.get(i).ruleID;
			List<CStoryTrees> treeList = trees.get(key);
			if (treeList == null) {
				treeList = new ArrayList<CStoryTrees>();
				trees.put(key, treeList);
			}

			for (NetworkNotationForCurrentStory nnCS : networkNotationForCurrentStory) {
				if (nnCS.getNetworkNotationList().size() != 0)
					if (nnCS.getNetworkNotationList().get(0).getRule()
							.getRuleID() == key) {

						CStoryTrees tree = new CStoryTrees(key, nnCS);
						tree.getTreeFromList(nnCS.getNetworkNotationList());

						if (treeList.size() == 0)
							treeList.add(tree);
						else {
							boolean isAdd = true;
							for (CStoryTrees sTree : treeList)
								if (sTree.isIsomorphic(tree)) {
									isAdd = false;
									break;
								}
							if (isAdd)
								treeList.add(tree);
						}
					}
			}

		}
	}
}
