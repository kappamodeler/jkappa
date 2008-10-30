package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IInjection;

public class CStories extends CObservables {

	public static int numberOfSimulations = 10;

	List<List<Integer>> ways;
	List<List<Integer>> significantWays;

	List<CStory> stories;

	List<CStoryTree> storyTrees;
	
	class CStory {
		private int ruleID;
		private int frequency;

		public CStory(int ruleID) {
			this.ruleID = ruleID;
			this.frequency = 0;
		}
	}

	class CStoryTree {
		private CStory story;
		private List<List<Integer>> treeList;
		
		public CStoryTree(CStory story) {
			this.story = story;
			this.treeList = new ArrayList<List<Integer>>();
		}
		
		public void addValueToTree(int indexOfVertex, int value){
			this.treeList.get(indexOfVertex).add(value);
		}
		
		public void addValueToTree(int value){
			this.treeList.add(new ArrayList<Integer>());
			this.treeList.get(treeList.size()-1).add(value);
		}
	}

	public void addToStories(List<Integer> ruleIDs) {
		for (int i = 0; i < ruleIDs.size(); i++) {
			this.stories.add(new CStory(ruleIDs.get(i)));
		}
	}

	public CStories() {
		this.stories = new ArrayList<CStory>();
		this.storyTrees = new ArrayList<CStoryTree>();
		ways = new ArrayList<List<Integer>>();
		for (int i = 0; i < numberOfSimulations; i++)
			ways.add(new ArrayList<Integer>());
	}

	public boolean checkRule(int checkRuleID, int number) {
		ways.get(number).add(checkRuleID);
		for (CStory story : this.stories)
			if (story.ruleID == checkRuleID)
				return true;
		return false;
	}

	private void mergeTrees() {
		for (CStory story : this.stories) {
			List<List<Integer>> listOfSWays = getListOfSignificantWaysForCurrentStory(story);
			this.storyTrees.add(new CStoryTree(story));
		}
	}

	private void fillListOfStoryTree(CStoryTree storyTree, List<List<Integer>> listOfSWays){
		
		for(int i=0;i<listOfSWays.size();i++){
			//listOfSWays.get(i)
			
		}
		
	}
	
	private List<List<Integer>> getListOfSignificantWaysForCurrentStory(
			CStory story) {
		List<List<Integer>> currentWays = new ArrayList<List<Integer>>();

		for (int i = this.significantWays.size() - 1; i >= 0; i--) {
			List<Integer> way = this.significantWays.get(i);
			if (way.get(way.size() - 1) == story.ruleID)
				currentWays.add(way);
		}

		return currentWays;
	}

	public void handling() {
		significantWays = new ArrayList<List<Integer>>();
		for (int i = 0; i < numberOfSimulations; i++) {
			List<Integer> pathWay = this.ways.get(i);
			if (isEndOfSomeStory(pathWay.get(pathWay.size() - 1)))
				significantWays.add(getSignificantRules(pathWay));
		}
		// this.significantWays = significantWays;
	}

	private boolean isEndOfSomeStory(int ruleID) {
		for (CStory story : this.stories)
			if (story.ruleID == ruleID)
				return true;
		return false;
	}

	private List<Integer> getSignificantRules(List<Integer> pathWay) {
		List<Integer> signRules = new ArrayList<Integer>();
		int k = pathWay.size();
		int indexOfActivatedRule = pathWay.get(k - 1);// ruleID;
		signRules.add(indexOfActivatedRule);
		if (k == 1) {
			return signRules;
		}

		int indexOfCurrentRule;
		for (int i = k - 2; i >= 0; i--) {
			indexOfCurrentRule = pathWay.get(i);
			if (isRuleActivate(indexOfActivatedRule, indexOfCurrentRule)) {
				if (signRules.contains(indexOfCurrentRule))
				 continue;
				signRules.add(indexOfCurrentRule);
				indexOfActivatedRule = indexOfCurrentRule;
			}
		}
		return signRules;
	}

	private boolean isRuleActivate(int indexOfCheckingRule,
			int indexOfCurrentRule) {
		CRule currentRule = SimulationMain.getSimulationManager().getRules()
				.get(indexOfCurrentRule);
		// List<CRule> listRules =
		// SimulationMain.getSimulationManager().getRules
		// ().get(indexOfCheckingRule).getActivatedRule();
		List<CRule> listRules = currentRule.getActivatedRule();

		// for(CRule rule : listRules)
		// if (rule==currentRule)
		// return true;
		for (CRule rule : listRules)
			if (rule.getRuleID() == indexOfCheckingRule)
				return true;

		return false;
	}

}
