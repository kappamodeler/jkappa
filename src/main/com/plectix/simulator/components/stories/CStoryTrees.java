package com.plectix.simulator.components.stories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.events.CWireMap;

import com.plectix.simulator.interfaces.IStates;
import com.plectix.simulator.interfaces.IStoriesSiteStates;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;

public final class CStoryTrees {
	public static final byte IS_CAUSE = 2;
	public static final byte IS_NONE = 1;
	public static final byte IS_NOT_CAUSE = 0;
	private int isomorphicCount = 1;
	private double averageTime;
	private int ruleId;
	private int mainTraceID;
	private HashMap<Integer, List<Integer>> ruleIDToTraceID;
	private TreeMap<Integer, Integer> traceIDToLevel;
	private TreeMap<Integer, Integer> traceIDToRuleID;
	private TreeMap<Integer, List<Integer>> levelToTraceID;
	private HashMap<Integer, String> traceIDToData;
	private HashMap<Integer, String> traceIDToText;
	private TreeMap<Integer, List<Integer>> traceIDToTraceID;
	private TreeMap<Integer, List<Integer>> traceIDToTraceIDWeak;
	private List<CNetworkNotation> networkNotationsList;

	public int getMainTraceID() {
		return mainTraceID;
	}

	private SimulationArguments.StorifyMode compressionMode;

	public double getAverageTime() {
		return averageTime;
	}

	public void setAverageTime(double averageTime) {
		this.averageTime = averageTime;
	}

	public TreeMap<Integer, List<Integer>> getLevelToTraceID() {
		return levelToTraceID;
	}

	public HashMap<Integer, String> getTraceIDToData() {
		return traceIDToData;
	}

	public HashMap<Integer, String> getTraceIDToText() {
		return traceIDToText;
	}

	public final int getIsomorphicCount() {
		return isomorphicCount;
	}

	public CStoryTrees(int ruleId, double time,
			SimulationArguments.StorifyMode compressionMode,
			boolean isOcamlStyleObsName) {
		this.networkNotationsList = new ArrayList<CNetworkNotation>();
		this.ruleId = ruleId;
		this.averageTime = time;
		this.compressionMode = compressionMode;
		this.isOcamlStyleObsName = isOcamlStyleObsName;
	}

	public final int getRuleID() {
		return this.ruleId;
	}

	private void resetParameters(List<CNetworkNotation> commonList) {
		int index = 0;
		traceIDToLevel = new TreeMap<Integer, Integer>();
		this.ruleIDToTraceID = new HashMap<Integer, List<Integer>>();
		this.traceIDToTraceID = new TreeMap<Integer, List<Integer>>();
		this.traceIDToTraceIDWeak = new TreeMap<Integer, List<Integer>>();
		this.traceIDToRuleID = new TreeMap<Integer, Integer>();
		CNetworkNotation newNN = commonList.get(index);
		traceIDToLevel.put(newNN.getStep(), index);
		List<Integer> list = new ArrayList<Integer>();
		list.add(newNN.getStep());
		this.ruleIDToTraceID.put(newNN.getRule().getRuleID(), list);
		this.mainTraceID = commonList.get(0).getStep();
//		this.testSet = new HashSet<Integer>();
//		this.key = 0;
	}

	private Map<Integer, Integer> traceIDToIndex;

	private List<CNetworkNotation> updateMainList(
			List<CNetworkNotation> commonList) {
		List<CNetworkNotation> updatedList = new ArrayList<CNetworkNotation>();

		for (CNetworkNotation nn : commonList) {
			List<Integer> curList = traceIDToTraceID.get(nn.getStep());
			if (curList != null) {
				if (updatedList.size() <= 1)
					updatedList.add(nn);
				else {
					CNetworkNotation newNN = nn.isNotOpposite(updatedList);
					if (newNN == null)
						updatedList.add(nn);
				}
			}
		}

		updateTraceIDToIndex(updatedList);
		return updatedList;
	}

	private void addToWeakCompressionHelpMap(
			Long agentID,
			Integer siteID,
			int traceID,
			IStates siteState,
			Map<Long, Map<Integer, StoryChangeStateWithTrace>> agentIDSiteIDToTraceID) {

		Map<Integer, StoryChangeStateWithTrace> sitesMap = agentIDSiteIDToTraceID
				.get(agentID);
		if (sitesMap == null) {
			sitesMap = new HashMap<Integer, StoryChangeStateWithTrace>();
			agentIDSiteIDToTraceID.put(agentID, sitesMap);
		}

		StoryChangeStateWithTrace sCSWT = sitesMap.get(siteID);
		if (sCSWT == null) {
			sCSWT = new StoryChangeStateWithTrace();
			sitesMap.put(siteID, sCSWT);
		}
		sCSWT.addToStoryChangeStateWithTraceLists(traceID, siteState);
	}

	private void createWeakCompressionHelpMap(
			List<CNetworkNotation> commonList,
			Map<Long, Map<Integer, StoryChangeStateWithTrace>> agentIDSiteIDToTraceID,
			Map<Integer, Integer> traceIDToNextTraceID) {

		for (int i = commonList.size() - 1; i >= 0; i--) {
			CNetworkNotation nn = commonList.get(i);
			Map<Long, AgentSites> changesOfAllUsedSites = nn
					.getChangesOfAllUsedSites();

			if (i > 0)
				traceIDToNextTraceID.put(nn.getStep(), commonList.get(i - 1)
						.getStep());

			for (Map.Entry<Long, AgentSites> entry : changesOfAllUsedSites.entrySet()) {
				AgentSites as = entry.getValue();
				Map<Integer, IStoriesSiteStates> sitesMap = as.getSites();
				Iterator<Integer> siteIterator = sitesMap.keySet().iterator();
				while (siteIterator.hasNext()) {
					Integer keySite = siteIterator.next();
					IStoriesSiteStates sSS = sitesMap.get(keySite);
					addToWeakCompressionHelpMap(entry.getKey(), keySite,
							nn.getStep(), sSS.getBeforeState(),
							agentIDSiteIDToTraceID);

				}
			}
		}
	}

	private boolean checkFullHorizontalWires(List<IStates> list1,
			List<IStates> list2) {
		for (int i = 0; i < list1.size(); i++) {
			IStates states1 = list1.get(i);
			IStates states2 = list2.get(i);

			if (states1 == null && states2 == null)
				continue;
			if (states1 == null || states2 == null)
				return false;

			if (states1 != CStoryState.EMPTY_STATE
					&& states2 != CStoryState.EMPTY_STATE
					&& !states1.equalz(states2)) {
				return false;
			}
		}
		return true;
	}

	private List<IStates> getFullHorizontalWire(
			List<CNetworkNotation> commonList,
			int traceID,
			int nextTraceID,
			Map<Long, Map<Integer, StoryChangeStateWithTrace>> agentIDSiteIDToTraceID) {

		List<IStates> list = new ArrayList<IStates>();
		
		for (Map.Entry<Long, Map<Integer, StoryChangeStateWithTrace>> entry :  
			agentIDSiteIDToTraceID.entrySet()) {
			Map<Integer, StoryChangeStateWithTrace> storyChangeStateWithTraceMap = entry.getValue();
			Iterator<Integer> siteIterator = storyChangeStateWithTraceMap
					.keySet().iterator();

			while (siteIterator.hasNext()) {
				Integer keySite = siteIterator.next();
				StoryChangeStateWithTrace scswt = storyChangeStateWithTraceMap
						.get(keySite);
				boolean isEmpty = false;
				if (isEmptyIntersection(commonList, entry.getKey(), keySite, traceID)) {
					isEmpty = true;
				}
				list.add(scswt.getStoryStateByTraceID(nextTraceID, isEmpty));

			}
		}

		return list;
	}

	private boolean isEmptyIntersection(List<CNetworkNotation> commonList,
			Long agentKey, Integer siteKey, Integer traceID) {
		CNetworkNotation nn = commonList.get(this.traceIDToIndex.get(traceID));
		AgentSites as = nn.getChangesOfAllUsedSites().get(agentKey);
		if (as == null)
			return true;

		IStoriesSiteStates sss = as.getSites().get(siteKey);
		if (sss == null)
			return true;

		return false;
	}

	private void convertTree(
			Map<Integer, List<Integer>> reversedTraceIDToTraceID) {
		Iterator<Integer> iterator = traceIDToTraceID.keySet().iterator();

		while (iterator.hasNext()) {
			Integer key = iterator.next();
			List<Integer> listOfSons = traceIDToTraceID.get(key);
			Collections.sort(listOfSons);
			for (Integer son : listOfSons) {
				List<Integer> list = reversedTraceIDToTraceID.get(son);
				if (list == null) {
					list = new ArrayList<Integer>();
					reversedTraceIDToTraceID.put(son, list);
				}
				list.add(key);
			}
		}

	}

	private List<Integer> listToDelete;

	private void createListToDelete(
			List<CNetworkNotation> commonList,
			Map<Integer, List<Integer>> reversedTraceIDToTraceID,
			int minTraceID,
			int currentTraceID,
			int traceIDLastRule,
			List<IStates> minTraceIDStatesList,

			Map<Integer, Integer> traceIDToNextTraceID,
			Map<Long, Map<Integer, StoryChangeStateWithTrace>> agentIDSiteIDToTraceID) {

		List<Integer> traceIDListOfFathers = reversedTraceIDToTraceID
				.get(currentTraceID);

		if (traceIDListOfFathers == null)
			return;

		if (listToDelete.get(0) == -2)
			return;

		for (Integer idOfFather : traceIDListOfFathers) {
			List<Integer> traceIDListOfSons = traceIDToTraceID.get(idOfFather);
			if (listToDelete.get(0) == -2 && listToDelete.get(1) < idOfFather)
				return;
			if (traceIDLastRule == idOfFather.intValue()) {
				listToDelete = new ArrayList<Integer>();
				listToDelete.add(-2);
				listToDelete.add(-2);
				return;
			}

			for (Integer idOfSon : traceIDListOfSons) {
				if (listToDelete.get(0) == -2 && listToDelete.get(1) < idOfSon)
					return;
				if (!listToDelete.contains(idOfSon)) {
					if (idOfSon > minTraceID) {
						/*
						 * listToDelete = new ArrayList(); listToDelete.add(-2);
						 * listToDelete.add(-2); return;
						 */

						listToDelete.add(idOfSon);
						if (listToDelete.get(1) < idOfSon) {
							listToDelete.set(1, idOfSon);
							List<IStates> maxTraceIDStatesList = getFullHorizontalWire(
									commonList, idOfSon, traceIDToNextTraceID
											.get(idOfSon).intValue(),
									agentIDSiteIDToTraceID);
							if (checkFullHorizontalWires(minTraceIDStatesList,
									maxTraceIDStatesList)) {
								listToDelete.set(0, -2);
								return;
							}
						}
						createListToDelete(commonList,
								reversedTraceIDToTraceID, minTraceID, idOfSon,
								traceIDLastRule, minTraceIDStatesList,
								traceIDToNextTraceID, agentIDSiteIDToTraceID);
					}
				}
			}
			if (listToDelete.get(0) == -2 && listToDelete.get(1) < idOfFather)
				return;
			if (!listToDelete.contains(idOfFather)
					&& (traceIDLastRule != idOfFather.intValue())) {
				if (listToDelete.get(1) < idOfFather)
					listToDelete.set(1, idOfFather);
				listToDelete.add(idOfFather);
				List<IStates> maxTraceIDStatesList = getFullHorizontalWire(
						commonList, idOfFather, traceIDToNextTraceID.get(
								idOfFather).intValue(), agentIDSiteIDToTraceID);
				if (checkFullHorizontalWires(minTraceIDStatesList,
						maxTraceIDStatesList)) {
					listToDelete.set(0, -2);
					return;
				}
				createListToDelete(commonList, reversedTraceIDToTraceID,
						minTraceID, idOfFather, traceIDLastRule,
						minTraceIDStatesList, traceIDToNextTraceID,
						agentIDSiteIDToTraceID);
			}
		}
		return;
	}

	private List<Integer> getListToRemove(
			List<CNetworkNotation> commonList,
			int minTraceID,
			int traceIDLastRule,
			Map<Long, Map<Integer, StoryChangeStateWithTrace>> agentIDSiteIDToTraceID,
			Map<Integer, Integer> traceIDToNextTraceID) {
		List<IStates> minTraceIDStatesList = getFullHorizontalWire(commonList,
				minTraceID, minTraceID, agentIDSiteIDToTraceID);

		Map<Integer, List<Integer>> reversedTraceIDToTraceID = new TreeMap<Integer, List<Integer>>();
		listToDelete = new ArrayList<Integer>();
		listToDelete.add(-1);
		listToDelete.add(minTraceID);
		listToDelete.add(minTraceID);
		convertTree(reversedTraceIDToTraceID);
		createListToDelete(commonList, reversedTraceIDToTraceID, minTraceID,
				minTraceID, traceIDLastRule, minTraceIDStatesList,
				traceIDToNextTraceID, agentIDSiteIDToTraceID);

		return listToDelete;
	}

	private List<CNetworkNotation> tryToRemoveBlock(
			List<CNetworkNotation> commonList) {

		Map<Integer, Integer> traceIDToNextTraceID = new HashMap<Integer, Integer>();
		Map<Long, Map<Integer, StoryChangeStateWithTrace>> agentIDSiteIDToTraceID = new HashMap<Long, Map<Integer, StoryChangeStateWithTrace>>();
		int traceIDLastRule = commonList.get(0).getStep();
		int listToDeleteSize = 0;
		agentIDSiteIDToTraceID = new HashMap<Long, Map<Integer, StoryChangeStateWithTrace>>();
		traceIDToNextTraceID = new HashMap<Integer, Integer>();
		createWeakCompressionHelpMap(commonList, agentIDSiteIDToTraceID,
				traceIDToNextTraceID);
		while (true) {
			for (int i = commonList.size() - 1; i > 0; i--) {
				CNetworkNotation nn = commonList.get(i);
				List<Integer> listToDelete = getListToRemove(commonList, nn
						.getStep(), traceIDLastRule, agentIDSiteIDToTraceID,
						traceIDToNextTraceID);
				listToDeleteSize = listToDelete.size();
				if (listToDelete.size() > 2 && listToDelete.get(0) == -2) {
					listToDelete.remove(0);
					listToDelete.remove(0);
					commonList = removeFromCommonList(listToDelete, commonList);

					int mainSize = commonList.size();
					int weakSize = commonList.size() - 1;

					while (mainSize != weakSize) {
						mainSize = commonList.size();
						commonList = noneCompressStoryTrace(commonList);
						commonList = updateMainList(commonList);
						weakSize = commonList.size();
					}
					agentIDSiteIDToTraceID = new HashMap<Long, Map<Integer, StoryChangeStateWithTrace>>();
					traceIDToNextTraceID = new HashMap<Integer, Integer>();
					createWeakCompressionHelpMap(commonList,
							agentIDSiteIDToTraceID, traceIDToNextTraceID);
					break;
				}
			}
			if (listToDeleteSize <= 2)
				return commonList;
		}
	}

	private List<CNetworkNotation> removeFromCommonList(
			List<Integer> traceIDList, List<CNetworkNotation> commonList) {
		List<CNetworkNotation> updatedList = new ArrayList<CNetworkNotation>();
		for (CNetworkNotation nn : commonList) {
			if (!traceIDList.contains(nn.getStep()))
				updatedList.add(nn);
		}
		return updatedList;
	}

	private List<CNetworkNotation> doPermutation(
			List<CNetworkNotation> commonList) {
		List<CNetworkNotation> newList = new ArrayList<CNetworkNotation>();
		int size = commonList.size() - 1;

		boolean fullPermutation = false;
		for (int i = size; i > 1; i--) {
			int index = i;
			boolean wasPermutation = true;
			while (wasPermutation) {
				CNetworkNotation nn = commonList.get(index);
				CNetworkNotation nnNext = commonList.get(index - 1);

				if ((traceIDToTraceID.get(nnNext.getStep()) != null && traceIDToTraceID
						.get(nnNext.getStep()).contains(nn.getStep()))
						|| (traceIDToTraceIDWeak.get(nnNext.getStep()) != null && traceIDToTraceIDWeak
								.get(nnNext.getStep()).contains(nn.getStep()))) {
					wasPermutation = false;
					break;
				}
				fullPermutation = true;
				commonList.set(index - 1, nn);
				commonList.set(index, nnNext);
				index++;

				if (index == size + 1)
					break;
			}
		}

		if (fullPermutation) {
			for (CNetworkNotation nn : commonList) {
				nn.setStep(size--);
				newList.add(nn);
			}
			this.mainTraceID = commonList.get(0).getStep();
		}

		return newList;
	}

	private List<CNetworkNotation> weakCompressStoryTrace(
			List<CNetworkNotation> commonList) {
		List<CNetworkNotation> weakCompressedList = new ArrayList<CNetworkNotation>();
		commonList = noneCompressStoryTrace(commonList);
		weakCompressedList = updateMainList(commonList);

		if (weakCompressedList.size() > 1) {
			int mainSize = weakCompressedList.size();
			int weakSize = weakCompressedList.size() - 1;

			while (true) {

				while (mainSize != weakSize) {
					mainSize = weakCompressedList.size();
					weakCompressedList = noneCompressStoryTrace(weakCompressedList);
					weakCompressedList = updateMainList(weakCompressedList);
					weakSize = weakCompressedList.size();
				}

				weakCompressedList = tryToRemoveBlock(weakCompressedList);
				weakSize = weakCompressedList.size();

				List<CNetworkNotation> currentList = doPermutation(weakCompressedList);

				if (currentList.size() > 0) {
					weakCompressedList = currentList;
					mainSize = weakCompressedList.size();
					weakCompressedList = noneCompressStoryTrace(weakCompressedList);
					weakCompressedList = updateMainList(weakCompressedList);
					weakSize = weakCompressedList.size();
					if (weakSize == mainSize) {
						weakCompressedList = tryToRemoveBlock(weakCompressedList);
						weakSize = weakCompressedList.size();
					}
				}
				if (weakSize == mainSize)
					break;

			}
			return weakCompressedList;

		}
		return weakCompressedList;
	}

	private List<CNetworkNotation> strongCompressStoryTrace(
			List<CNetworkNotation> commonList) {
		List<CNetworkNotation> strongCompressedList = new ArrayList<CNetworkNotation>();
		strongCompressedList = weakCompressStoryTrace(commonList);

		int counter = 0;
		while (true) {
			Map<Long, List<CNetworkNotation>> agentToNNs = new HashMap<Long, List<CNetworkNotation>>();
			Map<Integer, List<Long>> agentNameIDToAgentID = new HashMap<Integer, List<Long>>();

			for (CNetworkNotation nn : strongCompressedList) {

				Iterator<Long> agentIDIterator = nn.getUsedAgentsFromRules()
						.keySet().iterator();
				while (agentIDIterator.hasNext()) {
					long agentID = agentIDIterator.next();
					AgentSitesFromRules asFR = nn.getUsedAgentsFromRules().get(
							agentID);

					List<CNetworkNotation> nnList = agentToNNs.get(agentID);
					if (nnList == null) {
						nnList = new ArrayList<CNetworkNotation>();
						agentToNNs.put(agentID, nnList);
					}
					nnList.add(nn);

					int agentNameID = asFR.getAgentNameID();
					List<Long> agentIDsList = agentNameIDToAgentID
							.get(agentNameID);
					if (agentIDsList == null) {
						agentIDsList = new ArrayList<Long>();
						agentNameIDToAgentID.put(agentNameID, agentIDsList);
					}
					if (!agentIDsList.contains(agentID))
						agentIDsList.add(agentID);
				}
			}
			Iterator<Integer> iterator = agentNameIDToAgentID.keySet()
					.iterator();
			counter = 0;
			while (iterator.hasNext()) {
				int key = iterator.next();
				List<Long> agentIDs = agentNameIDToAgentID.get(key);
				if (agentIDs.size() > 1) {
					List<CNetworkNotation> currentList = canChange(agentIDs,
							strongCompressedList, agentToNNs);
					if (currentList.size() != 0) {
						strongCompressedList = weakCompressStoryTrace(currentList);
						break;
					}
				}
				counter++;
			}
			if (agentNameIDToAgentID.size() == counter)
				break;
		}

		return strongCompressedList;
	}

	private List<CNetworkNotation> canChange(List<Long> agentIDs,
			List<CNetworkNotation> commonList,
			Map<Long, List<CNetworkNotation>> agentToNNs) {

		List<CNetworkNotation> currentList = new ArrayList<CNetworkNotation>();
		int size = agentIDs.size();
		for (int i = 0; i < size - 1; i++) {
			Long agentIDToDelete = agentIDs.get(i);
			for (int j = i + 1; j < size; j++) {
				Long agentID = agentIDs.get(j);

				List<CNetworkNotation> nnListForChange = agentToNNs
						.get(agentID);
				if (hasLink(nnListForChange, agentIDToDelete))
					return currentList;

				List<CNetworkNotation> clonedList = new ArrayList<CNetworkNotation>();
				for (CNetworkNotation nn : commonList) {
					clonedList.add(nn.cloneNetworkNotation());
				}

				TreeMap<Integer, List<Integer>> traceIDToTraceIDCloned = new TreeMap<Integer, List<Integer>>();
				TreeMap<Integer, List<Integer>> traceIDToTraceIDWeakCloned = new TreeMap<Integer, List<Integer>>();
				TreeMap<Integer, Integer> traceIDToLevelCloned = new TreeMap<Integer, Integer>();

				cloneMap(traceIDToTraceIDCloned, traceIDToTraceID);
				cloneMap(traceIDToTraceIDWeakCloned, traceIDToTraceIDWeak);

				Iterator<Integer> iterator = traceIDToLevel.keySet().iterator();
				while (iterator.hasNext()) {
					int key = iterator.next();
					int value = traceIDToLevel.get(key);
					traceIDToLevelCloned.put(key, value);
				}

				currentList = replaceAgentsInTrace(clonedList, agentIDToDelete,
						agentID, agentToNNs.get(agentIDToDelete),
						nnListForChange);
				if (currentList.size() > 0)
					return currentList;
				else {
					traceIDToTraceID = traceIDToTraceIDCloned;
					traceIDToTraceIDWeak = traceIDToTraceIDWeakCloned;
					traceIDToLevel = traceIDToLevelCloned;
					updateMainList(commonList);
				}
			}
		}
		return currentList;
	}

	private void cloneMap(TreeMap<Integer, List<Integer>> map,
			TreeMap<Integer, List<Integer>> mapToClone) {
		for (Map.Entry<Integer, List<Integer>> entry : mapToClone.entrySet()) {
			List<Integer> newList = new ArrayList<Integer>();
			for (int index : entry.getValue()) {
				newList.add(index);
			}
			map.put(entry.getKey(), newList);
		}
	}

	private List<CNetworkNotation> replaceAgentsInTrace(
			List<CNetworkNotation> commonList, long agentIDToDelete,
			long agentID, List<CNetworkNotation> nnListForDelete,
			List<CNetworkNotation> nnListForChange) {
		List<CNetworkNotation> listToReturn = new ArrayList<CNetworkNotation>();

		for (CNetworkNotation nn : commonList) {
			Map<Long, AgentSitesFromRules> usedAgentsFromRules = nn
					.getUsedAgentsFromRules();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(agentIDToDelete);
			if (aSFR != null) {

				usedAgentsFromRules.put(agentID, aSFR);
				usedAgentsFromRules.remove(agentIDToDelete);

				Map<Long, AgentSites> changesOfAllUsedSites = nn
						.getChangesOfAllUsedSites();
				Iterator<Long> iterator = changesOfAllUsedSites.keySet()
						.iterator();
				while (iterator.hasNext()) {
					long key = iterator.next();
					AgentSites aSCh = changesOfAllUsedSites.get(key);
					if (key != agentIDToDelete) {
						Map<Integer, IStoriesSiteStates> sitesMap = aSCh
								.getSites();

						Iterator<Integer> siteIterator = sitesMap.keySet()
								.iterator();
						while (siteIterator.hasNext()) {
							int siteKey = siteIterator.next();
							IStoriesSiteStates sss = sitesMap.get(siteKey);
//							if (sss.getAfterState().getIdLinkAgent() == agentIDToDelete)
							if (sss.getAfterState()!= null && sss.getAfterState().getIdLinkAgent() == agentIDToDelete)
								sss.getAfterState().setIdLinkAgent(agentID);
							if (sss.getBeforeState().getIdLinkAgent() == agentIDToDelete)
								sss.getBeforeState().setIdLinkAgent(agentID);
						}
					}
				}

				AgentSites aSCh = changesOfAllUsedSites.get(agentIDToDelete);
				changesOfAllUsedSites.put(agentID, aSCh);
				changesOfAllUsedSites.remove(agentIDToDelete);

				List<Map<Long, List<Integer>>> introMapList = nn
						.getIntroCCMap();
				for (Map<Long, List<Integer>> introMap : introMapList) {
					List<Integer> introSitesList = introMap
							.get(agentIDToDelete);
					if (introSitesList != null) {
						introMap.put(agentID, introSitesList);
						introMap.remove(agentIDToDelete);
					}
				}
			}
		}

		listToReturn = hasViolationInTrace(commonList, agentID);

		return listToReturn;
	}

	private List<CNetworkNotation> hasViolationInTrace(
			List<CNetworkNotation> currentList, long agentID) {
		List<CNetworkNotation> newList = new ArrayList<CNetworkNotation>();
		currentList = weakCompressStoryTrace(currentList);

		// trying to remove equals network notations
		List<Integer> equalNNsToDelete = new ArrayList<Integer>();
		for (int i = currentList.size() - 1; i > 0; i--) {
			CNetworkNotation nn = currentList.get(i);
			for (int j = i - 1; j >= 0; j--) {
				CNetworkNotation nnNext = currentList.get(j);
				if (nn.isEqualsNetworkNotation(nnNext)) {
					equalNNsToDelete.add(j);
				}
			}
		}

		Collections.sort(equalNNsToDelete);
		for (int j = equalNNsToDelete.size() - 1; j >= 0; j--) {
			currentList.remove(equalNNsToDelete.get(j).intValue());
		}

		int index = currentList.size() - 1;
		int indexNext = currentList.size() - 1;
		while (true) {
			index = indexNext;
			CNetworkNotation nn = currentList.get(index);
			AgentSites aS = nn.getChangesOfAllUsedSites().get(agentID);
			if (aS != null)
				index--;
			else
				while (aS == null && index > 0) {
					nn = currentList.get(index);
					aS = nn.getChangesOfAllUsedSites().get(agentID);
					index--;
				}
			if (index < 0)
				break;

			CNetworkNotation nnNext = currentList.get(index);
			AgentSites aSNext = nnNext.getChangesOfAllUsedSites().get(agentID);

			if (aSNext != null)
				indexNext = index;
			else {
				while (aSNext == null && index > 0) {
					nnNext = currentList.get(index);
					aSNext = nnNext.getChangesOfAllUsedSites().get(agentID);
					index--;
				}
				indexNext = index + 1;
			}
			if (aSNext == null)
				break;

			if (!isSequentialTwoSiteMaps(aS, aSNext))
				return newList;
		}

		for (CNetworkNotation nn : currentList)
			newList.add(nn);

		return newList;
	}

	private boolean isSequentialTwoSiteMaps(AgentSites aS, AgentSites aSNext) {
		Iterator<Integer> iterator = aS.getSites().keySet().iterator();

		while (iterator.hasNext()) {
			Integer key = iterator.next();

			if (aSNext.getSites().containsKey(key)) {
				IStoriesSiteStates sss = aS.getSites().get(key);
				IStoriesSiteStates sssNext = aSNext.getSites().get(key);

				IStates state = sss.getAfterState();
				IStates stateNext = sssNext.getBeforeState();

				if (state.getIdInternalState() != CSite.NO_INDEX
						&& stateNext.getIdInternalState() != CSite.NO_INDEX
						&& state.getIdInternalState() != stateNext
								.getIdInternalState())
					return false;

				if (state.getIdLinkSite() != CSite.NO_INDEX
						&& stateNext.getIdLinkSite() == CSite.NO_INDEX)
					return false;

				if (state.getIdLinkSite() == CSite.NO_INDEX
						&& stateNext.getIdLinkSite() != CSite.NO_INDEX)
					return false;
			}
		}

		return true;
	}

	private boolean hasLink(List<CNetworkNotation> nnList, long agentID) {

		for (CNetworkNotation nn : nnList) {
			if (nn.getUsedAgentsFromRules().containsKey(agentID))
				return true;
		}

		return false;
	}

	private void updateTraceIDToIndex(List<CNetworkNotation> nnList) {
		traceIDToIndex = new HashMap<Integer, Integer>();
		ruleIDToTraceID = new HashMap<Integer, List<Integer>>();

		int ind = 0;
		for (CNetworkNotation nn : nnList) {
			traceIDToIndex.put(nn.getStep(), ind);
			ind++;
			int ruleID = nn.getRule().getRuleID();
			List<Integer> list = ruleIDToTraceID.get(ruleID);
			if (list == null) {
				list = new ArrayList<Integer>();
				ruleIDToTraceID.put(ruleID, list);
			}
			list.add(nn.getStep());
		}
		
		boolean delete = true;
		while(delete){
			delete = false;
			Iterator<Integer> iterator = traceIDToTraceIDWeak.keySet().iterator();
			while (iterator.hasNext()) {
				int key = iterator.next();
				List<Integer> weakList = traceIDToTraceIDWeak.get(key);
				if (weakList == null || weakList.size() == 0){
					traceIDToTraceIDWeak.remove(key);	
					delete = true;
					break;
				}
				else {
					List<Integer> deletingList = new ArrayList<Integer>();
					List<Integer> strongList = traceIDToTraceID.get(key);
					for(int i=0; i< weakList.size();i++){
						if(strongList.contains(weakList.get(i)))
							deletingList.add(i);
					}
					if(deletingList.size()>0){
						for(int i = deletingList.size()-1;i>=0;i--){
							weakList.remove(deletingList.get(i));
						}
						delete = true;
					}
				}
			}
		}
		
	}

	private List<CNetworkNotation> updateOnlyMainList(
			List<CNetworkNotation> commonList) {
		List<CNetworkNotation> updatedList = new ArrayList<CNetworkNotation>();

		for (CNetworkNotation nn : commonList) {
			List<Integer> curList = traceIDToTraceID.get(nn.getStep());
			if (curList != null)
				updatedList.add(nn);
		}
		updateTraceIDToIndex(updatedList);
		return updatedList;
	}

	private List<CNetworkNotation> noneCompressStoryTrace(
			List<CNetworkNotation> commonList) {

		resetParameters(commonList);
		isCausing(commonList.get(0), commonList, 1, 0);
		pushTree();
		return commonList;
	}

	public final void getTreeFromList(List<CNetworkNotation> commonList) {

//		CWireMap cw = new CWireMap(commonList);
		if (compressionMode == SimulationArguments.StorifyMode.NONE) {
			networkNotationsList = noneCompressStoryTrace(commonList);
			networkNotationsList = updateOnlyMainList(networkNotationsList);
		} else if (compressionMode == SimulationArguments.StorifyMode.WEAK) {
			networkNotationsList = weakCompressStoryTrace(commonList);
		} else if (compressionMode == SimulationArguments.StorifyMode.STRONG) {
			networkNotationsList = strongCompressStoryTrace(commonList);
		} else {
			throw new IllegalArgumentException("Unknown StorifyMode: "
					+ compressionMode);
		}
		fillMaps();
	}

//	private HashSet<Integer> testSet;
//	private int key = 0;
	
	
	private void isCausing(CNetworkNotation newNN,
			List<CNetworkNotation> commonList, int begin, int level) {
		Iterator<Long> agentIterator = newNN.getUsedAgentsFromRules().keySet()
				.iterator();

		if (begin >= commonList.size()) {
			addToMapRuleIDToTraceID(newNN, level);
			return;
		}
		
//		if(testSet.contains(newNN.getStep()))
//			return;
		

		while (agentIterator.hasNext()) {
			Long agentKey = agentIterator.next();
			AgentSitesFromRules aSFR = newNN.getUsedAgentsFromRules().get(
					agentKey);
			Iterator<Integer> siteIterator = aSFR.getSites().keySet()
					.iterator();
			int leafIndex = 0;
			while (siteIterator.hasNext()) {
				Integer siteKey = siteIterator.next();
				SitesFromRules sFR = aSFR.getSites().get(siteKey);
				boolean isLink = true;
				List<Integer> weakTraceIDs = new ArrayList<Integer>();
				byte isCause = isCausing(newNN, commonList, begin, isLink,
						agentKey, siteKey, sFR, level, weakTraceIDs);
				if (isCause == IS_NOT_CAUSE) {
					leafIndex++;
				}
				if (isCause == IS_CAUSE)
					putToWeakRelationMap(newNN.getStep(), weakTraceIDs);
				isLink = false;

				weakTraceIDs = new ArrayList<Integer>();
				isCause = isCausing(newNN, commonList, begin, isLink, agentKey,
						siteKey, sFR, level, weakTraceIDs);
				if (isCause == IS_NOT_CAUSE) {
					leafIndex++;
				}
				if (isCause == IS_CAUSE)
					putToWeakRelationMap(newNN.getStep(), weakTraceIDs);
			}
			if (aSFR.getSites().size() * 2 == leafIndex) {
				addToMapRuleIDToTraceID(newNN, level);
			}
		}
//		testSet.add(newNN.getStep());
		return;
	}

	private byte isCausing(CNetworkNotation newNN,
			List<CNetworkNotation> commonList, int begin, boolean isLink,
			Long agentKey, int siteKey, SitesFromRules sFR, int level,
			List<Integer> weakTraceIDs) {

		for (int i = begin; i < commonList.size(); i++) {
			CNetworkNotation comparableNN = commonList.get(i);

			AgentSitesFromRules aSFRComparable = comparableNN
					.getUsedAgentsFromRules().get(agentKey);
			if (aSFRComparable != null) {
				SitesFromRules sFRComparable = aSFRComparable.getSites().get(
						siteKey);
				if (sFRComparable != null) {
					if (sFRComparable.isCausing(sFR, isLink)) {
						List<Integer> helpList = traceIDToTraceID.get(newNN
								.getStep());
						level++;
						if (helpList == null
								|| !helpList.contains(comparableNN.getStep())) {
							addToConList(newNN, comparableNN, i, level);
							if (weakTraceIDs.contains(comparableNN.getStep()))
								weakTraceIDs.remove(comparableNN.getStep());
						}
						isCausing(comparableNN, commonList, i + 1, level);
						return IS_CAUSE;
					}
					if (!isLink
							&& sFRComparable.getInternalStateMode() == NetworkNotationMode.TEST
							&& sFR.getInternalStateMode() == NetworkNotationMode.TEST_OR_MODIFY) {
						List<Integer> helpList = traceIDToTraceID.get(newNN
								.getStep());
						if (helpList == null
								|| (helpList != null && !helpList
										.contains(comparableNN.getStep())))
							if (!weakTraceIDs.contains(comparableNN.getStep())) {
								weakTraceIDs.add(comparableNN.getStep());
							}
					}
					if (isLink
							&& sFRComparable.getLinkStateMode() == NetworkNotationMode.TEST
							&& sFR.getLinkStateMode() == NetworkNotationMode.TEST_OR_MODIFY) {
						List<Integer> helpList = traceIDToTraceID.get(newNN
								.getStep());
						if (helpList == null
								|| (helpList != null && !helpList
										.contains(comparableNN.getStep())))
							if (!weakTraceIDs.contains(comparableNN.getStep())) {
								weakTraceIDs.add(comparableNN.getStep());
							}
					}
				}
			}
		}
		return IS_NOT_CAUSE;
	}

	private void putToWeakRelationMap(int traceIDKey, List<Integer> traceIDList) {
		List<Integer> list = traceIDToTraceIDWeak.get(traceIDKey);
		if (list == null) {
			list = new ArrayList<Integer>();
			traceIDToTraceIDWeak.put(traceIDKey, traceIDList);
		}
		for (int traceID : traceIDList)
			if (!list.contains(traceID))
				list.add(traceID);
	}

	public TreeMap<Integer, List<Integer>> getTraceIDToTraceIDWeak() {
		return traceIDToTraceIDWeak;
	}

	private boolean checkTransitivity(int checkingTraceID,
			int beginningTraceID, boolean transitivity) {
		List<Integer> traceIDs = traceIDToTraceID.get(beginningTraceID);

		if (!transitivity)
			for (int id : traceIDs) {
				if (id == checkingTraceID) {
					transitivity = true;
					break;
				} else
					transitivity = checkTransitivity(checkingTraceID, id,
							transitivity);
			}

		return transitivity;
	}

	private void pushTree() {
		Iterator<Integer> traceIterator = traceIDToTraceID.keySet().iterator();

		while (traceIterator.hasNext()) {
			int key = traceIterator.next();
			List<Integer> currentTraceIDList = traceIDToTraceID.get(key);

			List<Integer> curList = new ArrayList<Integer>();

			if (currentTraceIDList.size() > 1) {
				for (Integer checkingTraceID : currentTraceIDList) {
					int counter = 0;
					for (Integer currentTraceID : currentTraceIDList) {
						if (checkingTraceID != currentTraceID) {
							if (!checkTransitivity(checkingTraceID,
									currentTraceID, false))
								counter++;
						}
					}
					if (counter == currentTraceIDList.size() - 1)
						curList.add(checkingTraceID);
				}
				traceIDToTraceID.put(key, curList);
			}
			currentTraceIDList = traceIDToTraceID.get(key);
			List<Integer> weakList = traceIDToTraceIDWeak.get(key);
			List<Integer> helpList = new ArrayList<Integer>();

			if (weakList != null) {
				for (Integer traceID : currentTraceIDList) {
					weakList.remove(traceID);
				}
				for (Integer traceID : weakList) {
					if (traceIDToTraceID.keySet().contains(traceID))
						helpList.add(traceID);
				}

				if (weakList.size() == 0)
					traceIDToTraceIDWeak.remove(key);
				else
					traceIDToTraceIDWeak.put(key, helpList);
			}
		}
	}

	public String getText(int index) {
		return networkNotationsList.get(index).getRule().getName();
	}

	private boolean isOcamlStyleObsName;

	private void fillAllAddedAgentIDs(List<Long> list,
			List<CNetworkNotation> nnList) {
		for (CNetworkNotation nn : nnList) {
			List<Long> addedAgents = nn.getAddedAgentsID();
			if (addedAgents != null)
				list.addAll(addedAgents);
		}
	}

	List<CStoryIntro> storyIntros;

	private boolean isEqualLists(List<Integer> addedSites,
			List<Integer> introSites) {
		int counter = 0;
		for (Integer nameID : introSites) {
			if (addedSites.contains(nameID))
				counter++;
			else
				addedSites.add(nameID);

		}
		if (counter == introSites.size())
			return true;

		return false;
	}

	private void newForAddedAgentsMap(Map<Long, List<Integer>> addedAgentsMap,
			Map<Long, List<Integer>> introAgentsMap,
			Map<Long, CStoryIntro> addedIntros, String introString,
			Integer traceID) {
		for (Map.Entry<Long, List<Integer>> entry : introAgentsMap.entrySet()) {
			Long key = entry.getKey();
			List<Integer> introSites = entry.getValue();
			List<Integer> addedSites = addedAgentsMap.get(key);

			if (addedSites != null) {
				if (!isEqualLists(addedSites, introSites)) {
					CStoryIntro intro = addedIntros.get(key);
					boolean isFather = false;
					for (Integer ids : intro.getTraceIDs())
						if (checkTransitivity(ids, traceID, false)) {
							isFather = true;
							break;
						}
					if (!isFather)
						intro.addToTraceID(traceID);
				}
			} else {
				addedSites = new ArrayList<Integer>();
				addedSites.addAll(introSites);
				addedAgentsMap.put(key, addedSites);

				CStoryIntro intro = new CStoryIntro(introString);
				intro.addToTraceID(traceID);
				addedIntros.put(key, intro);
				storyIntros.add(intro);
			}
		}
	}

	private void fillMaps() {

		levelToTraceID = new TreeMap<Integer, List<Integer>>();
		traceIDToData = new HashMap<Integer, String>();
		traceIDToText = new HashMap<Integer, String>();

		storyIntros = new ArrayList<CStoryIntro>();

		List<Long> addedAgents = new ArrayList<Long>();
		fillAllAddedAgentIDs(addedAgents, networkNotationsList);

		Map<Long, List<Integer>> addedAgentsMap = new HashMap<Long, List<Integer>>();
		Map<Long, CStoryIntro> addedIntros = new HashMap<Long, CStoryIntro>();

		for (int i = networkNotationsList.size() - 1; i >= 0; i--) {
			int index = 0;
			CNetworkNotation nn = networkNotationsList.get(i);

			for (Map<Long, List<Integer>> introAgentsMap : nn.getIntroCCMap()) {
				newForAddedAgentsMap(addedAgentsMap, introAgentsMap,
						addedIntros, nn.getAgentsNotation().get(index), nn
								.getStep());
				index++;
			}
		}

		Iterator<Integer> iterator = traceIDToLevel.keySet().iterator();
		while (iterator.hasNext()) {
			int traceID = iterator.next();
			int level = traceIDToLevel.get(traceID);
			List<Integer> list = levelToTraceID.get(level);
			if (list == null) {
				list = new ArrayList<Integer>();
				levelToTraceID.put(level, list);
			}
			list.add(traceID);

			CNetworkNotation nn = networkNotationsList.get(traceIDToIndex.get(
					traceID).intValue());
			this.traceIDToRuleID.put(traceID, nn.getRule().getRuleID());
			CRule rule = nn.getRule();
			traceIDToData.put(traceID, SimulationData.getData(rule, isOcamlStyleObsName));
			traceIDToText.put(traceID, rule.getName());
		}
	}

	public List<CStoryIntro> getStoryIntros() {
		return storyIntros;
	}

	private void addToMapRuleIDToTraceID(CNetworkNotation nn, int level) {
		int indexInTrace = nn.getStep();

		List<Integer> traceIDsList = traceIDToTraceID.get(nn.getStep());
		if (traceIDsList == null) {
			traceIDsList = new ArrayList<Integer>();
			traceIDToTraceID.put(nn.getStep(), traceIDsList);
		}

		Integer levelIn = traceIDToLevel.get(indexInTrace);
		if ((levelIn == null) || (levelIn != null && levelIn < level))
			traceIDToLevel.put(nn.getStep(), level);
	}

	private void addToConList(CNetworkNotation nn, CNetworkNotation nnToAdd,
			int indexInTrace, int level) {
		List<Integer> traceIDsList = traceIDToTraceID.get(nn.getStep());
		if (traceIDsList == null) {
			traceIDsList = new ArrayList<Integer>();
			traceIDToTraceID.put(nn.getStep(), traceIDsList);
		}

		if (!traceIDsList.contains(nnToAdd.getStep()))
			traceIDsList.add(nnToAdd.getStep());

		Integer levelIn = traceIDToLevel.get(nnToAdd.getStep());
		if ((levelIn == null) || (levelIn != null && levelIn < level))
			traceIDToLevel.put(nnToAdd.getStep(), level);
	}

	public HashMap<Integer, List<Integer>> getRuleIDToTraceID() {
		return ruleIDToTraceID;
	}

	public TreeMap<Integer, Integer> getTraceIDToLevel() {
		return traceIDToLevel;
	}

	public TreeMap<Integer, List<Integer>> getTraceIDToTraceID() {
		return traceIDToTraceID;
	}

	public final boolean isIsomorphic(CStoryTrees treeIn) {
		if (this.levelToTraceID.size() != treeIn.getLevelToTraceID().size())
			return false;

		if (!isIsomorphic(treeIn, mainTraceID, treeIn.getMainTraceID()))
			return false;

		this.isomorphicCount++;
		return true;
	}

	private boolean isContainsRuleID(CStoryTrees treeIn,
			List<Integer> childTraceIDListIn, int ruleID) {
		for (Integer traceIDIn : childTraceIDListIn) {
			int ruleIDIn = treeIn.traceIDToRuleID.get(traceIDIn);
			if (ruleID == ruleIDIn)
				return true;
		}
		return false;
	}

	private List<Integer> getTraceIDFrom(CStoryTrees treeIn, Integer traceID,
			List<Integer> childTraceIDListIn) {
		List<Integer> listIn = new ArrayList<Integer>();
		int ruleID = this.traceIDToRuleID.get(traceID);
		for (Integer traceIDIn : childTraceIDListIn) {
			int ruleIDIn = treeIn.traceIDToRuleID.get(traceIDIn);
			if (ruleID == ruleIDIn)
				listIn.add(traceIDIn);
		}
		return listIn;
	}

	private boolean isEqualWeakLists(CStoryTrees treeIn,
			List<Integer> childTraceIDListWeak,
			List<Integer> childTraceIDListWeakIn) {

		if (childTraceIDListWeak == null && childTraceIDListWeakIn == null)
			return true;

		if (childTraceIDListWeak == null && childTraceIDListWeakIn != null)
			return false;

		if (childTraceIDListWeak != null && childTraceIDListWeakIn == null)
			return false;

		if (childTraceIDListWeak.size() != childTraceIDListWeakIn.size())
			return false;

		for (Integer traceID : childTraceIDListWeak) {
			Integer ruleID = traceIDToRuleID.get(traceID);
			if (!isContainsRuleID(treeIn, childTraceIDListWeakIn, ruleID))
				return false;
		}

		return true;
	}

	private boolean isIsomorphic(CStoryTrees treeIn, Integer rootTraceID,
			Integer rootTraceIDIn) {
		List<Integer> childTraceIDList = traceIDToTraceID.get(rootTraceID);
		List<Integer> childTraceIDListIn = treeIn.getTraceIDToTraceID().get(
				rootTraceIDIn);

		if (childTraceIDList.size() != childTraceIDListIn.size())
			return false;

		for (Integer traceID : childTraceIDList) {
			Integer ruleID = traceIDToRuleID.get(traceID);
			if (!isContainsRuleID(treeIn, childTraceIDListIn, ruleID))
				return false;
		}

		if (!isEqualsIntro(childTraceIDList, childTraceIDListIn, treeIn))
			return false;

		List<Integer> childTraceIDListWeak = traceIDToTraceIDWeak
				.get(rootTraceID);
		List<Integer> childTraceIDListWeakIn = treeIn.getTraceIDToTraceIDWeak()
				.get(rootTraceIDIn);

		if (!isEqualWeakLists(treeIn, childTraceIDListWeak,
				childTraceIDListWeakIn))
			return false;

		for (Integer traceID : childTraceIDList) {
			List<Integer> traceIDInList = getTraceIDFrom(treeIn, traceID,
					childTraceIDListIn);
			int counter = 0;
			for (Integer traceIDIn : traceIDInList) {
				if (isIsomorphic(treeIn, traceID, traceIDIn))
					counter++;
			}
			if (counter == 0)
				return false;
		}

		return true;
	}

	private boolean isEqualsIntro(List<Integer> list, List<Integer> listIn,
			CStoryTrees treeIn) {
		if (this.storyIntros.size() != treeIn.getStoryIntros().size())
			return false;
		return true;
	}

	public TreeMap<Integer, Integer> getTraceIDToRuleID() {
		return traceIDToRuleID;
	}
}