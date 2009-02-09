package com.plectix.simulator.components.stories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.IStates;
import com.plectix.simulator.interfaces.IStoriesSiteStates;
import com.plectix.simulator.simulator.SimulationArguments;

public final class CStoryTrees {
	public static final byte IS_CAUSE = 2;
	public static final byte IS_NONE = 1;
	public static final byte IS_NOT_CAUSE = 0;
	private int isomorphicCount = 1;
	private double averageTime;
	private int ruleId;
	private int mainTraceID;

	public int getMainTraceID() {
		return mainTraceID;
	}

	private NetworkNotationForCurrentStory nnCS;
	private HashMap<Integer, List<Integer>> ruleIDToTraceID;
	private TreeMap<Integer, Integer> traceIDToLevel;
	private TreeMap<Integer, Integer> traceIDToRuleID;
	private TreeMap<Integer, List<Integer>> levelToTraceID;
	private HashMap<Integer, List<String>> traceIDToIntroString;
	private HashMap<Integer, String> traceIDToData;
	private HashMap<Integer, String> traceIDToText;
	private TreeMap<Integer, List<Integer>> traceIDToTraceID;
	private TreeMap<Integer, List<Integer>> traceIDToTraceIDWeak;

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

	public HashMap<Integer, List<String>> getTraceIDToIntroString() {
		return traceIDToIntroString;
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

	public CStoryTrees(int ruleId, NetworkNotationForCurrentStory nnCS,
			SimulationArguments.StorifyMode compressionMode,
			boolean isOcamlStyleObsName) {
		this.nnCS = nnCS;
		this.ruleId = ruleId;
		this.averageTime = nnCS.getAverageTime();
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
	}

	private Map<Integer, Integer> traceIDToIndex;

	private List<CNetworkNotation> updateMainList(
			List<CNetworkNotation> commonList) {
		List<CNetworkNotation> updatedList = new ArrayList<CNetworkNotation>();

		CNetworkNotation lastNN = null;
		int index = 0;

		for (CNetworkNotation nn : commonList) {
			List<Integer> curList = traceIDToTraceID.get(nn.getStep());
			if (curList != null) {
				if (updatedList.size() <= 1) {
					updatedList.add(nn);
					lastNN = nn;
				} else {
					CNetworkNotation newNN = nn.isNotOpposite(updatedList);
					if (newNN == null) {
						updatedList.add(nn);
						lastNN = nn;
					} else {
						pushIntro(updatedList, nn, updatedList.size(),
								new ArrayList<Integer>());
						// pushIntro(updatedList, newNN, 0, new
						// ArrayList<Integer>());
						removeTraceIDsFromMaps(newNN);
						removeTraceIDsFromMaps(nn);
						clearMapsLists(nn);
						clearMapsLists(newNN);
					}
				}
			} else
				pushIntro(updatedList, nn, updatedList.size(),
						new ArrayList<Integer>());

			index++;
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

			Iterator<Long> agentIterator = changesOfAllUsedSites.keySet()
					.iterator();
			while (agentIterator.hasNext()) {
				Long keyAgent = agentIterator.next();
				AgentSites as = changesOfAllUsedSites.get(keyAgent);
				Map<Integer, IStoriesSiteStates> sitesMap = as.getSites();
				Iterator<Integer> siteIterator = sitesMap.keySet().iterator();
				while (siteIterator.hasNext()) {
					Integer keySite = siteIterator.next();
					IStoriesSiteStates sSS = sitesMap.get(keySite);
					addToWeakCompressionHelpMap(keyAgent, keySite,
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

			// if (states1.isEmpty() && states2.isEmpty())
			// continue;
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
		Iterator<Long> agentIterator = agentIDSiteIDToTraceID.keySet()
				.iterator();

		while (agentIterator.hasNext()) {
			Long keyAgent = agentIterator.next();
			Map<Integer, StoryChangeStateWithTrace> storyChangeStateWithTraceMap = agentIDSiteIDToTraceID
					.get(keyAgent);
			Iterator<Integer> siteIterator = storyChangeStateWithTraceMap
					.keySet().iterator();

			while (siteIterator.hasNext()) {
				Integer keySite = siteIterator.next();
				StoryChangeStateWithTrace scswt = storyChangeStateWithTraceMap
						.get(keySite);
				boolean isEmpty = false;
				if (isEmptyIntersection(commonList, keyAgent, keySite, traceID)) {
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

	List<Integer> listToDelete;

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
				listToDelete = new ArrayList();
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
		// List<Integer> listToDelete = new ArrayList<Integer>();
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
			List<CNetworkNotation> commonList,
			Map<Integer, Integer> traceIDToNextTraceID,
			Map<Long, Map<Integer, StoryChangeStateWithTrace>> agentIDSiteIDToTraceID) {
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
					removeFromCommonList(listToDelete, commonList);
					commonList = updateMainList(commonList);

					int mainSize = commonList.size();
					int weakSize = commonList.size() - 1;

					while (mainSize != weakSize) {
						mainSize = commonList.size();
						// doPermutation(commonList);
						noneCompressStoryTrace(commonList);
						commonList = updateMainList(commonList);
						weakSize = commonList.size();
					}
					// noneCompressStoryTrace(commonList);
					// commonList = updateMainList(commonList);
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
		for (Integer traceID : traceIDList) {
			removeTraceIDsFromMaps(traceID, -1);
		}
		for (Integer traceID : traceIDList) {
			clearMap(this.traceIDToTraceID, traceID);
			clearMap(this.traceIDToTraceIDWeak, traceID);
			this.traceIDToLevel.remove(traceID);
		}

		List<CNetworkNotation> updatedList = new ArrayList<CNetworkNotation>();
		for (CNetworkNotation nn : commonList) {
			List<Integer> curList = traceIDToTraceID.get(nn.getStep());
			if (curList != null) {
				updatedList.add(nn);
			} else
				pushIntro(updatedList, nn, updatedList.size(),
						new ArrayList<Integer>());
		}
		updateTraceIDToIndex(updatedList);
		return updatedList;
	}

	private boolean doPermutation(List<CNetworkNotation> commonList) {

		int size = commonList.size() - 1;

		boolean fullPermutation = false;
		for (int i = size; i > 1; i--) {
			int index = i;
			boolean wasPermutation = true;
			while (wasPermutation) {
				CNetworkNotation nn = commonList.get(index);
				CNetworkNotation nnNext = commonList.get(index - 1);

				// if (traceIDToTraceID.get(nnNext.getStep()) == null
				// && traceIDToTraceID.get(nn.getStep()) == null) {
				// wasPermutation = false;
				// break;
				// }
				//
				// if (traceIDToTraceID.get(nnNext.getStep()) == null
				// && traceIDToTraceID.get(nn.getStep()).size() == 0) {
				// wasPermutation = false;
				// break;
				// }
				//
				// if (traceIDToTraceID.get(nn.getStep()) == null
				// && traceIDToTraceID.get(nnNext.getStep()).size() == 0) {
				// wasPermutation = false;
				// break;
				// }
				//
				// if (traceIDToTraceID.get(nnNext.getStep()).size() == 0
				// && traceIDToTraceID.get(nn.getStep()).size() == 0) {
				// wasPermutation = false;
				// break;
				// }

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
			}
			this.mainTraceID = commonList.get(0).getStep();
		}

		return fullPermutation;
	}

	private List<CNetworkNotation> weakCompressStoryTrace(
			List<CNetworkNotation> commonList) {
		List<CNetworkNotation> weakCompressedList = new ArrayList<CNetworkNotation>();
		noneCompressStoryTrace(commonList);

		if (commonList.size() > 1) {

			Map<Integer, Integer> traceIDToNextTraceID = new HashMap<Integer, Integer>();
			Map<Long, Map<Integer, StoryChangeStateWithTrace>> agentIDSiteIDToTraceID = new HashMap<Long, Map<Integer, StoryChangeStateWithTrace>>();
			// commonList = doPermutation(commonList);
			// deleting opposite rules which are neighbours in trace
			weakCompressedList = updateMainList(commonList);
			int mainSize = commonList.size();
			int weakSize = weakCompressedList.size();

			while (true) {

				while (mainSize != weakSize) {
					mainSize = weakCompressedList.size();
					noneCompressStoryTrace(weakCompressedList);
					weakCompressedList = updateMainList(weakCompressedList);
					weakSize = weakCompressedList.size();
				}

				weakCompressedList = tryToRemoveBlock(weakCompressedList,
						traceIDToNextTraceID, agentIDSiteIDToTraceID);
				weakSize = weakCompressedList.size();
				// if(weakSize)
				// while (doPermutation(weakCompressedList)) {
				if (doPermutation(weakCompressedList)) {
					mainSize = weakCompressedList.size();
					noneCompressStoryTrace(weakCompressedList);
					weakCompressedList = updateMainList(weakCompressedList);
					weakSize = weakCompressedList.size();
					if (weakSize == mainSize) {
						weakCompressedList = tryToRemoveBlock(
								weakCompressedList, traceIDToNextTraceID,
								agentIDSiteIDToTraceID);
						weakSize = weakCompressedList.size();
					}
				}
				// if (mainSize == weakSize)
				// break;
				// }

				// // deleting equal blocks
				// weakCompressedList = removeEqualLevels(weakCompressedList);
				// weakSize = weakCompressedList.size();
				//				
				// // deleting opposite rules which are not neighbours in trace
				// List<Integer> traceIDList = new ArrayList<Integer>();
				// List<Integer> traceIDListOpposite = new ArrayList<Integer>();
				// int rootStep = weakCompressedList.get(0).getStep();
				// List<Integer> weakRelationList = new ArrayList<Integer>();
				//				
				// isOppositeBranch(rootStep, rootStep, weakCompressedList,
				// traceIDList, traceIDListOpposite, weakRelationList);
				// Collections.sort(traceIDList);
				//				
				// for (int deletingTraceID : traceIDList) {
				// int deletingIndex = traceIDToIndex.get(deletingTraceID);
				// CNetworkNotation nn = weakCompressedList.get(deletingIndex);
				//				
				// if (!traceIDListOpposite.contains(deletingTraceID))
				// pushIntro(weakCompressedList, nn, deletingIndex,
				// traceIDList);
				// removeTraceIDsFromMaps(nn);
				// weakCompressedList.remove(deletingIndex);
				// }
				//				
				// if (weakSize != weakCompressedList.size()) {
				// noneCompressStoryTrace(weakCompressedList);
				// weakCompressedList = updateMainList(weakCompressedList);
				// weakSize = weakCompressedList.size();
				// }
				if (weakSize == mainSize)
					break;

			}
			return weakCompressedList;

		}
		return commonList;
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

					int agentNameID = asFR.getAgentName();// .getNameId();
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

				List<CNetworkNotation> listToCheck = new ArrayList<CNetworkNotation>();

				for (CNetworkNotation nn : commonList) {
					addToListToCheck(listToCheck, nn, agentIDToDelete, agentID);
				}

				// if (listToCheck.size() != commonList.size()) {
				//
				// }

				currentList = replaceAgentsInTrace(commonList, agentIDToDelete,
						agentID, agentToNNs.get(agentIDToDelete),
						nnListForChange);
				if (currentList.size() > 0)
					return currentList;
			}
		}
		return currentList;
	}

	private void addToListToCheck(List<CNetworkNotation> listNN,
			CNetworkNotation nnToCheck, Long agentIDToDelete, Long checkAgentID) {
		for (CNetworkNotation nn : listNN) {
			if (!nn.isEqualsNetworkNotation(nnToCheck, agentIDToDelete,
					checkAgentID))
				listNN.add(nn);
		}
	}

	private List<CNetworkNotation> replaceAgentsInTrace(
			List<CNetworkNotation> commonList, Long agentIDToDelete,
			Long agentID, List<CNetworkNotation> nnListForDelete,
			List<CNetworkNotation> nnListForChange) {
		List<CNetworkNotation> listToReturn = new ArrayList<CNetworkNotation>();

		// if (hasLink(nnListForChange, agentIDToDelete))
		// return listToReturn;

		if (hasWrongIntersection(agentIDToDelete, agentID, nnListForDelete,
				nnListForChange))
			return listToReturn;

		for (CNetworkNotation nn : nnListForDelete) {
			Map<Long, AgentSites> changedAgentsFromSolution = nn
					.getChangedAgentsFromSolution();
			AgentSites aS = changedAgentsFromSolution.get(agentIDToDelete);
			if (aS != null) {
				changedAgentsFromSolution.put(agentID, aS);
				changedAgentsFromSolution.remove(agentIDToDelete);
			}

			Map<Long, AgentSitesFromRules> usedAgentsFromRules = nn
					.getUsedAgentsFromRules();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(agentIDToDelete);
			usedAgentsFromRules.put(agentID, aSFR);
			usedAgentsFromRules.remove(agentIDToDelete);

			Map<Long, AgentSites> changesOfAllUsedSites = nn
					.getChangesOfAllUsedSites();
			AgentSites aSCh = changesOfAllUsedSites.get(agentIDToDelete);
			changesOfAllUsedSites.put(agentID, aSCh);
			changesOfAllUsedSites.remove(agentIDToDelete);

			nn.changeIntroCC(agentIDToDelete, agentID);
		}

		for (CNetworkNotation nn : commonList) {

			listToReturn.add(nn);
		}

		return listToReturn;
	}

	private boolean hasWrongIntersection(Long agentIDToDelete, Long agentID,
			List<CNetworkNotation> nnListForDelete,
			List<CNetworkNotation> nnListForChange) {

		List<CNetworkNotation> mainList = new ArrayList<CNetworkNotation>();
		List<Long> mainListAgentID = new ArrayList<Long>();

		int counterForDelete = nnListForDelete.size() - 1;
		int counterForChange = nnListForChange.size() - 1;

		CNetworkNotation nnDelete = null;
		CNetworkNotation nnChange = null;

		while (true) {
			if (counterForDelete >= 0)
				nnDelete = nnListForDelete.get(counterForDelete);
			if (counterForChange >= 0)
				nnChange = nnListForChange.get(counterForChange);

			if (nnDelete.getStep() < nnChange.getStep()) {
				addToChangingList(nnDelete, agentIDToDelete, mainList,
						mainListAgentID);
				counterForDelete--;
			} else {
				addToChangingList(nnChange, agentID, mainList, mainListAgentID);
				counterForChange--;
			}

			if (counterForDelete < 0 || counterForChange < 0)
				break;
		}

		while (true) {
			if (counterForDelete >= 0) {
				nnDelete = nnListForDelete.get(counterForDelete);
				addToChangingList(nnDelete, agentIDToDelete, mainList,
						mainListAgentID);
				counterForDelete--;
			}
			if (counterForChange >= 0) {
				nnChange = nnListForChange.get(counterForChange);
				addToChangingList(nnChange, agentID, mainList, mainListAgentID);
				counterForChange--;
			}

			if (counterForDelete < 0 && counterForChange < 0)
				break;
		}

		for (int i = 1; i < mainList.size(); i++) {
			AgentSites aS = mainList.get(i - 1).getChangesOfAllUsedSites().get(
					mainListAgentID.get(i - 1));
			AgentSites aSNext = mainList.get(i).getChangesOfAllUsedSites().get(
					mainListAgentID.get(i));
			if (!isSequentialTwoSiteMaps(aS, aSNext))
				return true;
		}

		return false;
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

	private void addToChangingList(CNetworkNotation nn, Long agentID,
			List<CNetworkNotation> mainList, List<Long> mainListAgentID) {
		// if (nn.getChangedAgentsFromSolution().containsKey(agentID)) {
		mainList.add(nn);
		mainListAgentID.add(agentID);
		// }
	}

	private boolean hasLink(List<CNetworkNotation> nnList, long agentID) {

		for (CNetworkNotation nn : nnList) {
			if (nn.getUsedAgentsFromRules().containsKey(agentID))
				return true;
		}

		return false;
	}

	private void clearMapsLists(CNetworkNotation nn) {
		Integer traceID = nn.getStep();
		clearMap(this.ruleIDToTraceID, traceID);
		clearMap(this.traceIDToTraceID, traceID);
		clearMap(this.traceIDToTraceIDWeak, traceID);
		this.traceIDToLevel.remove(traceID);
	}

	private void clearMap(Map<Integer, List<Integer>> map, Integer traceID) {
		Iterator<Integer> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			int key = iterator.next();
			map.get(key).remove(traceID);
		}
	}

	private void removeTraceIDsFromMaps(CNetworkNotation nn) {
		Integer removingStep = nn.getStep();
		Integer ruleID = nn.getRule().getRuleID();
		removeTraceIDsFromMaps(removingStep, ruleID);
	}

	private void removeTraceIDsFromMaps(Integer removingStep, Integer ruleID) {
		if (ruleID.intValue() != -1)
			this.ruleIDToTraceID.get(ruleID).remove(removingStep);

		this.traceIDToTraceID.remove(removingStep.intValue());
		this.traceIDToTraceIDWeak.remove(removingStep.intValue());
		this.traceIDToLevel.remove(removingStep.intValue());
	}

	private void updateTraceIDToIndex(List<CNetworkNotation> nnList) {
		traceIDToIndex = new HashMap<Integer, Integer>();
		ruleIDToTraceID = new HashMap<Integer, List<Integer>>();
		// traceIDToLevel = new TreeMap<Integer, Integer>();

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
	}

	private void pushIntro(List<CNetworkNotation> weakCompressedList,
			CNetworkNotation currentNN, int indexToBegin,
			List<Integer> listToDelete) {
		if (currentNN == null)
			return;

		int indexIntroFromCurrent = 0;
		for (List<Long> agentListMain : currentNN.getIntroCC()) {
			for (Long agentIDMain : agentListMain) {
				if (currentNN.getUsedAgentsFromRules().containsKey(agentIDMain)) {
					changeIntro(weakCompressedList, currentNN, indexToBegin,
							listToDelete, agentIDMain, agentListMain,
							indexIntroFromCurrent);
				}
			}
			indexIntroFromCurrent++;
		}
	}

	private void changeIntro(List<CNetworkNotation> weakCompressedList,
			CNetworkNotation currentNN, int indexToBegin,
			List<Integer> listToDelete, Long agentKey,
			List<Long> agentListMain, int indexIntroFromCurrent) {
		for (int i = indexToBegin - 1; i >= 0; i--) {
			CNetworkNotation checkingNN = weakCompressedList.get(i);

			if (!listToDelete.contains(checkingNN.getStep())) {
				if (checkingNN.getUsedAgentsFromRules().get(agentKey) != null) {
					int indexToDel = 0;
					for (List<Long> agentList : checkingNN.getIntroCC()) {
						for (Long agent : agentList) {
							if (agent.longValue() == agentKey.longValue()) {

								checkingNN.changeIntroCCAndAgentNotation(
										indexToDel, agentListMain,
										currentNN.agentsNotation
												.get(indexIntroFromCurrent));
								return;
							}
						}
						indexToDel++;
					}
				}
			}
		}
	}

	private void noneCompressStoryTrace(List<CNetworkNotation> commonList) {

		resetParameters(commonList);
		isCausing(commonList.get(0), commonList, 1, 0);
		pushTree();
		updateTraceIDToIndex(commonList);
	}

	public final void getTreeFromList(List<CNetworkNotation> commonList) {
		List<CNetworkNotation> nnList = commonList;
		if (compressionMode == SimulationArguments.StorifyMode.NONE) {
			noneCompressStoryTrace(commonList);
		} else if (compressionMode == SimulationArguments.StorifyMode.WEAK) {
			nnList = weakCompressStoryTrace(commonList);
		} else if (compressionMode == SimulationArguments.StorifyMode.STRONG) {
			nnList = strongCompressStoryTrace(commonList);
		} else {
			throw new IllegalArgumentException("Unknown StorifyMode: "
					+ compressionMode);
		}
		fillMaps(nnList);
	}

	private void isCausing(CNetworkNotation newNN,
			List<CNetworkNotation> commonList, int begin, int level) {
		Iterator<Long> agentIterator = newNN.getUsedAgentsFromRules().keySet()
				.iterator();

		if (begin >= commonList.size()) {
			addToMapRuleIDToTraceID(newNN, level);
			newNN.setLeaf(true);
			return;
		}

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
				newNN.setLeaf(true);
			}
		}
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
								// isCausing(comparableNN, commonList, i + 1,
								// level);
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
								// isCausing(comparableNN, commonList, i + 1,
								// level);
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
		return nnCS.getNetworkNotation(index).getRule().getName();
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

	private void fillMaps(List<CNetworkNotation> nnList) {

		levelToTraceID = new TreeMap<Integer, List<Integer>>();
		traceIDToIntroString = new HashMap<Integer, List<String>>();
		traceIDToData = new HashMap<Integer, String>();
		traceIDToText = new HashMap<Integer, String>();

		List<Long> addedAgents = new ArrayList<Long>();
		fillAllAddedAgentIDs(addedAgents, nnList);

		List<Long> introAgents = new ArrayList<Long>();
		for (int i = nnList.size() - 1; i >= 0; i--) {
			int counter = 0;
			int index = 0;
			CNetworkNotation nn = nnList.get(i);
			List<String> introStr = new ArrayList<String>();

			for (List<Long> agentIDsList : nn.getIntroCC()) {
				List<Long> currentIntroAgents = new ArrayList<Long>();
				for (Long agentID : agentIDsList) {
					if (nn.getUsedAgentsFromRules().containsKey(agentID))
						if (!addedAgents.contains(agentID))
							if (!introAgents.contains(agentID)) {
								currentIntroAgents.add(agentID);
								counter++;
							}
				}
				if (counter == agentIDsList.size()) {
					introAgents.addAll(currentIntroAgents);
					introStr.add(nn.getAgentsNotation().get(index));
				}
				index++;
				counter = 0;
			}

			traceIDToIntroString.put(nn.getStep(), introStr);
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

			CNetworkNotation nn = nnList.get(traceIDToIndex.get(traceID)
					.intValue());
			this.traceIDToRuleID.put(traceID, nn.getRule().getRuleID());
			IRule rule = nn.getRule();
			traceIDToData.put(traceID, rule.getData(isOcamlStyleObsName));
			traceIDToText.put(traceID, rule.getName());
		}
	}

	private void addToMapRuleIDToTraceID(CNetworkNotation nn, int level) {
		int ruleID = nn.getRule().getRuleID();
		List<Integer> list = ruleIDToTraceID.get(ruleID);
		if (list == null) {
			list = new ArrayList<Integer>();
			ruleIDToTraceID.put(ruleID, list);
		}
		int indexInTrace = nn.getStep();
		if (!list.contains(indexInTrace)) {
			list.add(indexInTrace);
		}

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
		Integer key = nnToAdd.getRule().getRuleID();
		List<Integer> intTraceList = ruleIDToTraceID.get(key);

		if (intTraceList == null) {
			intTraceList = new ArrayList<Integer>();
			ruleIDToTraceID.put(key, intTraceList);
		}

		if (!intTraceList.contains(nnToAdd.getStep())) {
			intTraceList.add(nnToAdd.getStep());
		}

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

	private Integer getTraceIDFrom(CStoryTrees treeIn, Integer traceID,
			List<Integer> childTraceIDListIn) {

		int ruleID = this.traceIDToRuleID.get(traceID);
		for (Integer traceIDIn : childTraceIDListIn) {
			int ruleIDIn = treeIn.traceIDToRuleID.get(traceIDIn);
			if (ruleID == ruleIDIn)
				return traceIDIn;
		}
		return -1;
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
			Integer traceIDIn = getTraceIDFrom(treeIn, traceID,
					childTraceIDListIn);
			if (!isIsomorphic(treeIn, traceID, traceIDIn))
				return false;
		}

		return true;
	}

	private boolean isEqualsIntro(List<Integer> list, List<Integer> listIn,
			CStoryTrees treeIn) {
		int intro = getIntroList(list, this);
		int introIn = getIntroList(listIn, treeIn);
		if (introIn != intro)
			return false;
		return true;
	}

	private int getIntroList(List<Integer> traceList, CStoryTrees tree) {
		int introCounter = 0;
		for (Integer number : traceList) {
			if (tree.getTraceIDToIntroString().get(number) != null)
				introCounter += tree.getTraceIDToIntroString().get(number)
						.size();
		}
		return introCounter;
	}

	public TreeMap<Integer, Integer> getTraceIDToRuleID() {
		return traceIDToRuleID;
	}
}