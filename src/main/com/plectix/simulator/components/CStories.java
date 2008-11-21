package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class CStories extends CObservables {

	public static int numberOfSimulations = 10;

	List<CStory> stories;

	List<NetworkNotationForCurrentStory> networkNotationForCurrentStory;

	class NetworkNotationForCurrentStory {
		List<CNetworkNotation> networkNotationList;

		public NetworkNotationForCurrentStory() {
			networkNotationList = new ArrayList<CNetworkNotation>();
		}

		public void addToNetworkNotationList(CNetworkNotation networkNotation) {
			networkNotationList.add(networkNotation);
		}

		public void handling() {
		}
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
		this.networkNotationForCurrentStory = new ArrayList<NetworkNotationForCurrentStory>();
		for (int i = 0; i < numberOfSimulations; i++)
			this.networkNotationForCurrentStory
					.add(new NetworkNotationForCurrentStory());
	}

	public boolean checkRule(int checkRuleID, int number) {
		for (CStory story : this.stories)
			if (story.ruleID == checkRuleID)
				return true;
		return false;
	}
}
