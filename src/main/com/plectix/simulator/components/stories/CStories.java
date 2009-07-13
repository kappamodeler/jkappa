package com.plectix.simulator.components.stories;

import java.util.*;

import com.plectix.simulator.components.stories.storage.AbstractStorage;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.simulator.SimulationData;

public final class CStories {

	private int iterations = 10;

	private final List<Integer> aplliedRulesIds;
	private final Map<Integer,IWireStorage> eventsMapForCurrentStory;

	private SimulationData simulationData;

	public CStories(SimulationData simData) {
		this.simulationData = simData;
		this.iterations = simData.getSimulationArguments().getIterations();
		this.aplliedRulesIds = new ArrayList<Integer>();
		this.eventsMapForCurrentStory = new HashMap<Integer, IWireStorage>();
		for (int i = 0; i < iterations; i++) {
			this.eventsMapForCurrentStory.put(new Integer(i),new AbstractStorage(simulationData
					.getSimulationArguments().getStorifyMode()));
		}
	}

	public int getRuleIdAtStories(int index) {
		if (index >= aplliedRulesIds.size() || index < 0)
			return -1;
		return aplliedRulesIds.get(index);
	}

//	public final Collection<List<CStoryTrees>> getTrees() {
//		return Collections.unmodifiableCollection(trees.values());
//	}

	/**
	 * param index ; root of cleaning
	 */
	public final void cleaningStory(int index) throws StoryStorageException{
		if (eventsMapForCurrentStory.get(index).isImportantStory()){
			eventsMapForCurrentStory.get(index).handling();
		}
		else{
			//delete this story
			eventsMapForCurrentStory.get(index).clearList();
		}
	}

	public final void addToStories(List<Integer> ruleIDs) {
		for (Integer ruleId : ruleIDs) {
			this.aplliedRulesIds.add(ruleId);
		}
	}

	public final void addEventToStory(int index,
			CEvent eventContainer) throws StoryStorageException {
		eventsMapForCurrentStory.get(Integer.valueOf(index)).addEventContainer(eventContainer,false);
	}

	public final void addLastEventToStoryStorifyRule(int index,
			CEvent eventContainer,
			double currentTime) throws StoryStorageException  {
		this.eventsMapForCurrentStory.get(Integer.valueOf(index))
				.addLastEventContainer(eventContainer, currentTime);
	}

	public final boolean checkRule(int ruleToBeChecked, int index) {
		if (aplliedRulesIds.contains(ruleToBeChecked)) {
			eventsMapForCurrentStory.get(Integer.valueOf(index)).setEndOfStory();
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
	
	public Map<Integer, IWireStorage> getEventsMapForCurrentStory() {
		// TODO Auto-generated method stub
		return eventsMapForCurrentStory;
	}
	
	
}
