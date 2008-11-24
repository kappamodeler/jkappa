package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.runner.notification.StoppedByUserException;

import com.plectix.simulator.components.CNetworkNotation.AgentSites;

public class CStories extends CObservables {

	public static int numberOfSimulations = 10;

	List<CStory> stories;

	List<NetworkNotationForCurrentStory> networkNotationForCurrentStory;

	HashMap<Integer, CStoryTrees> trees;

	class CStoryTrees {
		HashMap<Integer, List<Integer>> contiguityList;

		public CStoryTrees(List<CNetworkNotation> commonList) {
			contiguityList = new HashMap<Integer, List<Integer>>();

			for (int i = 0; i < commonList.size(); i++) {
				CNetworkNotation nnCurrent = commonList.get(i);
				int key = nnCurrent.getRule().getRuleID();
				List<Integer> list = contiguityList.get(key);

				if (list == null) {
					list = new ArrayList<Integer>();
					contiguityList.put(key, list);
				}
				if (i > 0)
					list.add(commonList.get(i + 1).getRule().getRuleID());
			}
		}

		public void addListToTree(List<CNetworkNotation> commonList) {
			for (int i = 0; i < commonList.size(); i++) {
				CNetworkNotation nnCurrent = commonList.get(i);
				int key = nnCurrent.getRule().getRuleID();
				List<Integer> list = contiguityList.get(key);

				if (list == null) {
					list = new ArrayList<Integer>();
					contiguityList.put(key, list);
				}
				if (i > 0)
					addToList(list, commonList.get(i + 1).getRule().getRuleID());
			}
		}

		private void addToList(List<Integer> list, int index) {
			if (!list.contains(index))
				list.add(index);
		}

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

			boolean is = false;
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
		
		/*for (int i = 0; i < networkNotationForCurrentStory.size(); i++) {
			int key = networkNotationForCurrentStory.get(i) CStoryTrees .getRuleID();
			List<Integer> list = contiguityList.get(key);

			if (list == null) {
				list = new ArrayList<Integer>();
				contiguityList.put(key, list);
			}
			if (i > 0)
				list.add(commonList.get(i + 1).getRule().getRuleID());
		}
*/	}
}
