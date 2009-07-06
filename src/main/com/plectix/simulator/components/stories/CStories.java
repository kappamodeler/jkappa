package com.plectix.simulator.components.stories;

import java.util.*;

import com.plectix.simulator.components.stories.newVersion.CEvent;
import com.plectix.simulator.components.stories.newVersion.CWiresStorage;
import com.plectix.simulator.simulator.SimulationData;

public final class CStories {

	private int iterations = 10;

	private final List<Integer> aplliedRulesIds;
	private final List<NetworkNotationForCurrentStory> networkNotationForCurrentStory;
	private final HashMap<Integer,CWiresStorage> eventsMapForCurrentStory;
	private final HashMap<Integer, List<CStoryTrees>> trees;

	private SimulationData simulationData;

	public CStories(SimulationData simData) {
		this.simulationData = simData;
		this.iterations = simData.getSimulationArguments().getIterations();
		this.aplliedRulesIds = new ArrayList<Integer>();
		this.trees = new HashMap<Integer, List<CStoryTrees>>();
		this.networkNotationForCurrentStory = new ArrayList<NetworkNotationForCurrentStory>();
		this.eventsMapForCurrentStory = new HashMap<Integer, CWiresStorage>();
		for (int i = 0; i < iterations; i++) {
			this.networkNotationForCurrentStory
					.add(new NetworkNotationForCurrentStory());
			this.eventsMapForCurrentStory.put(new Integer(i),new CWiresStorage(simulationData
					.getSimulationArguments().getStorifyMode()));
		}
	}

	public int getRuleIdAtStories(int index) {
		if (index >= aplliedRulesIds.size() || index < 0)
			return -1;
		return aplliedRulesIds.get(index);
	}

	public final Collection<List<CStoryTrees>> getTrees() {
		return Collections.unmodifiableCollection(trees.values());
	}

	public final void handling(int index) {
//		if (networkNotationForCurrentStory.get(index).isEndOfStory()){
		if (eventsMapForCurrentStory.get(index).isEndOfStory()){
//			networkNotationForCurrentStory.get(index).handling();
			eventsMapForCurrentStory.get(index).handling();
		}
		else{
//			networkNotationForCurrentStory.get(index).clearList();
			eventsMapForCurrentStory.get(index).clearList();
		}
	}

	public final void addToStories(List<Integer> ruleIDs) {
		for (Integer ruleId : ruleIDs) {
			this.aplliedRulesIds.add(ruleId);
		}
	}

	public final void addToNetworkNotationStory(int index,
			CNetworkNotation networkNotation, CEvent eventContainer) {
//		NetworkNotationForCurrentStory.addToNetworkNotationList(
//				networkNotation, this.networkNotationForCurrentStory.get(index)
//						.getNetworkNotationList());
		eventsMapForCurrentStory.get(Integer.valueOf(index)).addEventContainer(eventContainer);
	}

	public final void addToNetworkNotationStoryStorifyRule(int index,
			CNetworkNotation networkNotation, CEvent eventContainer,
			double currentTime) {
//		this.networkNotationForCurrentStory.get(index)
//				.addToNetworkNotationListStorifyRule(networkNotation);
//		this.networkNotationForCurrentStory.get(index).setAverageTime(
//				currentTime);
		this.eventsMapForCurrentStory.get(Integer.valueOf(index))
				.addLastEventContainer(eventContainer, currentTime);
	}

	public final boolean checkRule(int ruleToBeChecked, int index) {
		if (aplliedRulesIds.contains(ruleToBeChecked)) {
//			networkNotationForCurrentStory.get(index).setEndOfStory();
//			eventsMapForCurrentStory.get(Integer.valueOf(index)).setEndOfStory();
			return true;
		} else {
			return false;
		}
	}

	public final void merge() {
//		for (Integer key : aplliedRulesIds) {
//			List<CStoryTrees> treeList = trees.get(key);
//			if (treeList == null) {
//				treeList = new ArrayList<CStoryTrees>();
//				trees.put(key, treeList);
//			}
//
//			for (NetworkNotationForCurrentStory nnCS : networkNotationForCurrentStory) {
//				if (!nnCS.getNetworkNotationList().isEmpty())
//					if (nnCS.getNetworkNotationList().get(0).getRule()
//							.getRuleID() == key) {
//
//						CStoryTrees tree = new CStoryTrees(key, nnCS
//								.getAverageTime(), simulationData
//								.getSimulationArguments().getStorifyMode(),
//								simulationData.isOcamlStyleObsName());
//						tree.getTreeFromList(nnCS.getNetworkNotationList());
//
//						if (treeList.isEmpty())
//							treeList.add(tree);
//						else {
//							boolean isAdd = true;
//							for (CStoryTrees sTree : treeList)
//								if (sTree.isIsomorphic(tree)) {
//									isAdd = false;
//									break;
//								}
//							if (isAdd)
//								treeList.add(tree);
//						}
//					}
//			}
//
//		}
	}
}
