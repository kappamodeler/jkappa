package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.plectix.simulator.components.CNetworkNotation.AgentSites;

public class CStories extends CObservables {

	public static int numberOfSimulations = 10;

	List<CStory> stories;

	List<NetworkNotationForCurrentStory> networkNotationForCurrentStory;

	HashMap<Integer, CStoryTrees> trees;

	public final Collection<CStoryTrees> getTrees() {
		return this.trees.values();
	}

	class NetworkNotationForCurrentStory {
		List<CNetworkNotation> networkNotationList;
		private boolean isEndOfStory;

		public NetworkNotationForCurrentStory() {
			networkNotationList = new ArrayList<CNetworkNotation>();
			isEndOfStory = false;
		}

		public void addToNetworkNotationList(CNetworkNotation networkNotation) {
			if (networkNotation.isOpposite(networkNotationList))
				networkNotationList.add(networkNotation);
		}

		class CStoryVertexes {
			AgentSites aSites;

		}

		public void handling() {
			List<CNetworkNotation> nnList = new ArrayList<CNetworkNotation>();
			nnList.add(networkNotationList.get(networkNotationList.size() - 1));
			for (int i = networkNotationList.size() - 2; i >= 0; i--) {
				CNetworkNotation nn = networkNotationList.get(i);
				if (isIntersects(nn, nnList)) {
					nnList.add(nn);
				}
			}
			this.networkNotationList = nnList;
		}

		private final boolean isIntersects(CNetworkNotation nn,
				List<CNetworkNotation> nnList) {

			for (CNetworkNotation nnFromList : nnList) {
				if (!((nnFromList.isIntersects(nn) == CNetworkNotation.HAS_NO_INTERSECTION) && (nn
						.isIntersects(nnFromList) == CNetworkNotation.HAS_NO_INTERSECTION)))
					return true;
			}

			return false;
		}

		public final void clearList() {
			networkNotationList.clear();
		}

	}

	public final void handling(int index) {
		if (networkNotationForCurrentStory.get(index).isEndOfStory)
			networkNotationForCurrentStory.get(index).handling();
		else
			networkNotationForCurrentStory.get(index).clearList();
	}

	class CStory {
		private int ruleID;

		public CStory(int ruleID) {
			this.ruleID = ruleID;
		}
	}

	public void addToStories(List<Integer> ruleIDs) {
		for (int i = 0; i < ruleIDs.size(); i++) {
			this.stories.add(new CStory(ruleIDs.get(i)));
		}
	}

	public void addToNetworkNotationStory(int index,
			CNetworkNotation networkNotation) {
		this.networkNotationForCurrentStory.get(index)
				.addToNetworkNotationList(networkNotation);
	}

	public CStories() {
		this.stories = new ArrayList<CStory>();
		this.trees = new HashMap<Integer, CStoryTrees>();
		this.networkNotationForCurrentStory = new ArrayList<NetworkNotationForCurrentStory>();
		for (int i = 0; i < numberOfSimulations; i++)
			this.networkNotationForCurrentStory
					.add(new NetworkNotationForCurrentStory());
	}

	public boolean checkRule(int checkRuleID, int index) {
		for (CStory story : this.stories)
			if (story.ruleID == checkRuleID) {
				this.networkNotationForCurrentStory.get(index).isEndOfStory = true;
				return true;
			}
		return false;
	}

	public void merge() {

		for (int i = 0; i < stories.size(); i++) {
			int key = stories.get(i).ruleID;
			CStoryTrees tree = trees.get(key);

			if (tree == null) {
				for (NetworkNotationForCurrentStory nnCS : networkNotationForCurrentStory) {
					if (nnCS.networkNotationList.size() != 0)
						if (nnCS.networkNotationList.get(0).getRule()
								.getRuleID() == key) {
							if (trees.get(key) == null) {
								tree = new CStoryTrees(key);
								tree.getTreeFromList(nnCS.networkNotationList);
								trees.put(key, tree);
							} else {
								CStoryTrees newTree = new CStoryTrees(key);
								newTree
										.getTreeFromList(nnCS.networkNotationList);
								//tree.merge(newTree.getMap(), key);
							}
						}
				}
			}
		}
	}
}
