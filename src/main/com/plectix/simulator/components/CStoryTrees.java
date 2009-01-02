package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.CNetworkNotation.AgentSitesFromRules;
import com.plectix.simulator.components.CNetworkNotation.AgentSitesFromRules.SitesFromRules;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.simulator.SimulationArguments;

public final class CStoryTrees {
	public static final byte IS_CAUSE = 2;
	public static final byte IS_NONE = 1;
	public static final byte IS_NOT_CAUSE = 0;
	private int isomorphicCount = 1;
	private double averageTime;
	private int ruleId;
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
			SimulationArguments.StorifyMode compressionMode, boolean isOcamlStyleObsName) {
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
				} else if (nn.isNotOpposite(updatedList)) {
					updatedList.add(nn);
					lastNN = nn;
				} else {
					pushIntro(updatedList, nn);
					removeTraceIDsFromMaps(lastNN);
					removeTraceIDsFromMaps(nn);
					clearMapsLists(nn);
					clearMapsLists(lastNN);
				}
			}
			index++;
		}

		updateTraceIDToIndex(updatedList);
		return updatedList;
	}

	private List<CNetworkNotation> weakCompressStoryTrace(
			List<CNetworkNotation> commonList) {
		List<CNetworkNotation> weakCompressedList = new ArrayList<CNetworkNotation>();
		noneCompressStoryTrace(commonList);
		if (commonList.size() > 1) {

			// deleting opposite rules which are neighbours in trace
			weakCompressedList = updateMainList(commonList);
			int mainSize = commonList.size();
			int weakSize = weakCompressedList.size();

			while (mainSize != weakSize) {
				mainSize = weakCompressedList.size();
				noneCompressStoryTrace(weakCompressedList);
				weakCompressedList = updateMainList(weakCompressedList);
				weakSize = weakCompressedList.size();
			}

			// deleting equal blocks
			weakCompressedList = removeEqualLevels(weakCompressedList);
			int lastSize = weakCompressedList.size();

			// deleting opposite rules which are not neighbours in trace
			List<Integer> traceIDList = new ArrayList<Integer>();
			int rootStep = weakCompressedList.get(0).getStep();
			List<Integer> weakRelationList = new ArrayList<Integer>();

			isOppositeBranch(rootStep, rootStep, weakCompressedList,
					traceIDList, weakRelationList);
			Collections.sort(traceIDList);

			for (int deletingTraceID : traceIDList) {
				int deletingIndex = traceIDToIndex.get(deletingTraceID);
				CNetworkNotation nn = weakCompressedList.get(deletingIndex);
				pushIntro(weakCompressedList, nn, deletingIndex, traceIDList);
				removeTraceIDsFromMaps(nn);
				weakCompressedList.remove(deletingIndex);
			}

			if (lastSize != weakCompressedList.size()) {
				noneCompressStoryTrace(weakCompressedList);
				weakCompressedList = updateMainList(weakCompressedList);
			}
			return weakCompressedList;
		}
		return commonList;
	}

	private List<CNetworkNotation> strongCompressStoryTrace(
			List<CNetworkNotation> commonList) {
		List<CNetworkNotation> strongCompressedList = new ArrayList<CNetworkNotation>();
		strongCompressedList = weakCompressStoryTrace(commonList);

		HashMap<Long, List<IRule>> agentToRules = new HashMap<Long, List<IRule>>();
		HashMap<Integer, List<Long>> agentNameIDToAgentID = new HashMap<Integer, List<Long>>();

		for (CNetworkNotation nn : strongCompressedList) {

			Iterator<Long> agentIDIterator = nn.getChangedAgentsFromSolution()
					.keySet().iterator();
			while (agentIDIterator.hasNext()) {
				long agentID = agentIDIterator.next();
				AgentSites as = nn.getChangedAgentsFromSolution().get(agentID);

				List<IRule> rulesList = agentToRules.get(agentID);
				if (rulesList == null) {
					rulesList = new ArrayList<IRule>();
					agentToRules.put(agentID, rulesList);
				}
				rulesList.add(nn.getRule());

				int agentNameID = as.getAgent().getNameId();
				List<Long> agentIDsList = agentNameIDToAgentID.get(agentNameID);
				if (agentIDsList == null) {
					agentIDsList = new ArrayList<Long>();
					agentNameIDToAgentID.put(agentNameID, agentIDsList);
				}
				agentIDsList.add(agentID);
			}
		}

		return strongCompressedList;
	}

	private void clearMapsLists(CNetworkNotation nn) {
		Integer traceID = nn.getStep();
		clearMap(this.ruleIDToTraceID, traceID);
		clearMap(this.traceIDToTraceID, traceID);
		clearMap(this.traceIDToTraceIDWeak, traceID);
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
		this.ruleIDToTraceID.get(nn.getRule().getRuleID()).remove(removingStep);
		this.traceIDToTraceID.remove(removingStep.intValue());
		this.traceIDToTraceIDWeak.remove(removingStep.intValue());
		this.traceIDToLevel.remove(removingStep.intValue());
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
	}

	private List<CNetworkNotation> removeEqualLevels(
			List<CNetworkNotation> nnList) {
		Iterator<Integer> ruleIDIterator = ruleIDToTraceID.keySet().iterator();

		while (ruleIDIterator.hasNext()) {
			int ruleID = ruleIDIterator.next();
			List<Integer> traceIDList = ruleIDToTraceID.get(ruleID);
			if (traceIDList.size() > 1) {
				Collections.sort(traceIDList);
				List<CNetworkNotation> cList = removeEquivalentRules(
						traceIDToIndex, nnList, traceIDList);
				if (cList != null) {
					nnList = new ArrayList<CNetworkNotation>();
					nnList = cList;
					traceIDToIndex = new HashMap<Integer, Integer>();
					int ind = 0;
					for (CNetworkNotation nn : nnList) {
						traceIDToIndex.put(nn.getStep(), ind);
						ind++;
					}
				}
			}

		}

		return nnList;
	}

	private List<CNetworkNotation> removeEquivalentRules(
			Map<Integer, Integer> traceIDToIndex,
			List<CNetworkNotation> nnList, List<Integer> traceIDList) {
		List<Integer> traceIDToRemoveBegin = new ArrayList<Integer>();
		List<Integer> traceIDToRemoveEnd = new ArrayList<Integer>();
		int size = traceIDList.size() - 1;

		for (int i = 0; i <= size - 1; i++) {
			int iToRemove = traceIDList.get(i);
			CNetworkNotation nnToRemove = nnList.get(traceIDToIndex.get(
					iToRemove).intValue());
			for (int j = i + 1; j <= size; j++) {
				int iToCheck = traceIDList.get(j);
				CNetworkNotation nnToCheck = nnList.get(traceIDToIndex.get(
						iToCheck).intValue());
				if (isEqualNetworkNotations(nnToCheck, nnToRemove))
					if (!hasLessTraceID(iToRemove, iToCheck))
						if (!traceIDToRemoveBegin.contains(iToRemove)) {
							traceIDToRemoveBegin.add(iToRemove);
							traceIDToRemoveEnd.add(iToCheck);
							break;
						}
			}
		}

		int sizeToRemove = traceIDToRemoveBegin.size();
		if (sizeToRemove > 0) {
			int end = traceIDToIndex.get(traceIDToRemoveEnd.get(
					sizeToRemove - 1).intValue());
			int begin = traceIDToIndex.get(traceIDToRemoveBegin.get(0)
					.intValue());

			List<CNetworkNotation> newList = new ArrayList<CNetworkNotation>();
			for (int j = 0; j <= end; j++) {
				CNetworkNotation nn = nnList.get(j);
				newList.add(nn);
			}

			CNetworkNotation nnToCheck = nnList.get(end);
			CNetworkNotation nnToRemove = nnList.get(begin);

			pushIntro(nnToCheck, nnToRemove);
			copyTraceIDList(nnToRemove, nnToCheck);

			for (int i = begin; i >= end + 1; i--) {
				CNetworkNotation nn = nnList.get(i);
				pushIntro(newList, nn);
				removeTraceIDsFromMaps(nn);
			}

			for (int i = begin + 1; i < nnList.size(); i++) {
				CNetworkNotation nn = nnList.get(i);
				newList.add(nn);
			}
			return newList;
		}
		return null;
	}

	private void copyTraceIDList(CNetworkNotation pushingNN,
			CNetworkNotation currentNN) {
		List<Integer> list = traceIDToTraceID.get(pushingNN.getStep());
		traceIDToTraceID.put(currentNN.getStep(), list);
		List<Integer> weakList = traceIDToTraceIDWeak.get(pushingNN.getStep());
		if (weakList == null || weakList.size() == 0)
			traceIDToTraceIDWeak.remove(currentNN.getStep());
		else
			traceIDToTraceIDWeak.put(currentNN.getStep(), weakList);

	}

	private void pushIntro(CNetworkNotation nnToCheck,
			CNetworkNotation nnToRemove) {
		if (nnToRemove == null)
			return;
		Iterator<Long> agentIterator = nnToRemove.getUsedAgentsFromRules()
				.keySet().iterator();
		while (agentIterator.hasNext()) {
			Long agentKey = agentIterator.next();
			if (nnToCheck.getUsedAgentsFromRules().get(agentKey) != null) {
				nnToCheck.setHasIntro(true);
				break;
			}
		}

	}

	private boolean hasLessTraceID(int upperBoundTraceID, int lowerBoundTraceID) {
		Iterator<Integer> traceIDIterator = traceIDToTraceID.keySet()
				.iterator();
		int currentTraceId = traceIDIterator.next();

		if (currentTraceId != upperBoundTraceID) {
			while (traceIDIterator.hasNext()
					&& currentTraceId < upperBoundTraceID)
				currentTraceId = traceIDIterator.next();

			while (traceIDIterator.hasNext()
					&& currentTraceId < lowerBoundTraceID) {
				currentTraceId = traceIDIterator.next();
				for (int id : traceIDToTraceID.get(currentTraceId)) {
					if (id < upperBoundTraceID)
						return true;
				}
			}
		} else
			while (traceIDIterator.hasNext()
					&& currentTraceId < lowerBoundTraceID)
				currentTraceId = traceIDIterator.next();

		while (traceIDIterator.hasNext()) {
			currentTraceId = traceIDIterator.next();
			for (int id : traceIDToTraceID.get(currentTraceId)) {
				if (id < lowerBoundTraceID)
					return true;
			}
		}
		return false;
	}

	private boolean isOppositeBranch(int traceId, Integer mainTraceId,
			List<CNetworkNotation> list, List<Integer> indexList,
			List<Integer> weakList) {
		List<Integer> traceIDList = traceIDToTraceID.get(traceId);
		Collections.sort(traceIDList);

		for (int i = traceIDList.size() - 1; i >= 0; i--) {
			int trID = traceIDList.get(i);
			if (traceIDList.size() == 1 && (traceId != mainTraceId)) {
				if (!indexList.contains(trID) && !indexList.contains(traceId)) {
					int index1 = traceIDToIndex.get(traceId);

					int index2 = traceIDToIndex.get(trID);

					if (!list.get(index1).isOpposite(list.get(index2))) {
						List<Integer> listWeak = traceIDToTraceIDWeak
								.get(traceId);
						if (listWeak == null
								|| (listWeak != null && listWeak.size() == 0)) {
							indexList.add(trID);
							indexList.add(traceId);
						}
					}
				}
			}
			isOppositeBranch(trID, mainTraceId, list, indexList, weakList);
		}

		return true;
	}

	private boolean isEqualNetworkNotations(CNetworkNotation nn1,
			CNetworkNotation nn2) {
		if (nn1.getRule().getRuleID() != nn2.getRule().getRuleID())
			return false;
		Map<Long, AgentSitesFromRules> usedAgentsFromRules1 = nn1
				.getUsedAgentsFromRules();
		Map<Long, AgentSitesFromRules> usedAgentsFromRules2 = nn2
				.getUsedAgentsFromRules();
		if (usedAgentsFromRules1.size() != usedAgentsFromRules2.size())
			return false;
		Iterator<Long> iterator = usedAgentsFromRules1.keySet().iterator();
		while (iterator.hasNext()) {
			Long key = iterator.next();
			if (!usedAgentsFromRules2.containsKey(key))
				return false;
		}

		return true;
	}

	private void pushIntro(List<CNetworkNotation> weakCompressedList,
			CNetworkNotation currentNN) {
		if (currentNN == null)
			return;
		Iterator<Long> agentIterator = currentNN.getUsedAgentsFromRules()
				.keySet().iterator();
		while (agentIterator.hasNext()) {
			Long agentKey = agentIterator.next();
			for (int i = weakCompressedList.size() - 1; i >= 0; i--) {
				CNetworkNotation chekingNN = weakCompressedList.get(i);
				if (chekingNN.getUsedAgentsFromRules().get(agentKey) != null) {
					chekingNN.setHasIntro(true);
					break;
				}
			}
		}
	}

	// private void pushIntro(List<CNetworkNotation> weakCompressedList,
	// CNetworkNotation currentNN, int indexToBegin) {
	// if (currentNN == null)
	// return;
	// Iterator<Long> agentIterator = currentNN.getUsedAgentsFromRules()
	// .keySet().iterator();
	// while (agentIterator.hasNext()) {
	// Long agentKey = agentIterator.next();
	// for (int i = indexToBegin - 1; i >= 0; i--) {
	// CNetworkNotation chekingNN = weakCompressedList.get(i);
	// if (chekingNN.getUsedAgentsFromRules().get(agentKey) != null) {
	// chekingNN.setHasIntro(true);
	// int indexToDel = 0;
	// for (IConnectedComponent cc : chekingNN.getIntroCC()){
	// boolean is = false;
	// for (IAgent agent : cc.getAgents()) {
	// if (agent.getId() == agentKey) {
	// chekingNN.getIntroCC().remove(indexToDel);
	// is = true;
	// break;
	// }
	// }
	// if(is)
	// break;
	// indexToDel++;
	// }
	// break;
	// }
	// }
	// }
	// }

	private void pushIntro(List<CNetworkNotation> weakCompressedList,
			CNetworkNotation currentNN, int indexToBegin,
			List<Integer> listToDelete) {
		if (currentNN == null || !currentNN.isHasIntro())
			return;
	
		for (IConnectedComponent ccMain : currentNN.getIntroCC()) {
			for (IAgent agentMain : ccMain.getAgents()) {

				Long agentKey = agentMain.getId();
				if (currentNN.getUsedAgentsFromRules().containsKey(agentKey)) {

					for (int i = indexToBegin - 1; i >= 0; i--) {
						CNetworkNotation checkingNN = weakCompressedList.get(i);

						if (!listToDelete.contains(checkingNN.getStep())) {
							if (checkingNN.getUsedAgentsFromRules().get(
									agentKey) != null) {
								checkingNN.setHasIntro(true);
								int indexToDel = 0;
								for (IConnectedComponent cc : checkingNN
										.getIntroCC()) {
									boolean is = false;
									for (IAgent agent : cc.getAgents()) {
										if (agent.getId() == agentKey) {
											checkingNN.changeIntroCCAndAgentNotation(
													indexToDel, ccMain, currentNN.agentsNotation.get(indexToDel));
											is = true;
											break;
										}
									}
									if (is)
										break;
									indexToDel++;
								}
								break;
							}
						}
					}
				}
			}
		}
	}

	private void noneCompressStoryTrace(List<CNetworkNotation> commonList) {
		resetParameters(commonList);
		isCausing(commonList.get(0), commonList, 1, 0);
		if (ruleIDToTraceID.size() == 1)
			commonList.get(0).setHasIntro(true);
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
			throw new IllegalArgumentException("Unknown StorifyMode: " + compressionMode);
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
			Iterator<Integer> siteIterator = aSFR.sites.keySet().iterator();
			int leafIndex = 0;
			while (siteIterator.hasNext()) {
				Integer siteKey = siteIterator.next();
				SitesFromRules sFR = aSFR.sites.get(siteKey);
				boolean isLink = true;
				List<Integer> weakTraceIDs = new ArrayList<Integer>();
				byte isCause = isCausing(newNN, commonList, begin, isLink,
						agentKey, siteKey, sFR, level, weakTraceIDs);
				if (isCause == IS_NOT_CAUSE || isCause == IS_NONE) {
					leafIndex++;
				}
				if (isCause == IS_CAUSE)
					putToWeakRelationMap(newNN.getStep(), weakTraceIDs);
				isLink = false;

				weakTraceIDs = new ArrayList<Integer>();
				isCause = isCausing(newNN, commonList, begin, isLink, agentKey,
						siteKey, sFR, level, weakTraceIDs);
				if (isCause == IS_NOT_CAUSE || isCause == IS_NONE) {
					leafIndex++;
				}
				if (isCause == IS_CAUSE)
					putToWeakRelationMap(newNN.getStep(), weakTraceIDs);
			}
			if (aSFR.sites.size() * 2 == leafIndex) {
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
				SitesFromRules sFRComparable = aSFRComparable.sites
						.get(siteKey);
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
							&& sFRComparable.getInternalStateMode() == CNetworkNotation.MODE_TEST
							&& sFR.getInternalStateMode() == CNetworkNotation.MODE_TEST_OR_MODIFY) {
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
							&& sFRComparable.getLinkStateMode() == CNetworkNotation.MODE_TEST
							&& sFR.getLinkStateMode() == CNetworkNotation.MODE_TEST_OR_MODIFY) {
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

	private void fillMaps(List<CNetworkNotation> nnList) {
		levelToTraceID = new TreeMap<Integer, List<Integer>>();
		traceIDToIntroString = new HashMap<Integer, List<String>>();
		traceIDToData = new HashMap<Integer, String>();
		traceIDToText = new HashMap<Integer, String>();

		List<Long> introAgents = new ArrayList<Long>();

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
			int counter = 0;
			int index = 0;

			if (nn.isHasIntro()) {
				List<String> introStr = new ArrayList<String>();

				for (IConnectedComponent cc : nn.getIntroCC()) {
					for (IAgent agent : cc.getAgents()) {
						if (!introAgents.contains(agent.getId())) {
							introAgents.add(agent.getId());
							counter++;
						}
					}
					if (counter == cc.getAgents().size())
						introStr.add(nn.getAgentsNotation().get(index));
					index++;
					counter = 0;
				}
				traceIDToIntroString.put(traceID, introStr);
			}
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
		if (this.ruleIDToTraceID.size() != treeIn.getRuleIDToTraceID().size())
			return false;

		if (this.levelToTraceID.size() != treeIn.getLevelToTraceID().size())
			return false;

		Iterator<Integer> levelIterator = this.levelToTraceID.keySet()
				.iterator();

		while (levelIterator.hasNext()) {
			int level = levelIterator.next();
			List<Integer> list = this.levelToTraceID.get(level);
			List<Integer> listIn = treeIn.getLevelToTraceID().get(level);
			if (list.size() != list.size())
				return false;
			List<Integer> rules = new ArrayList<Integer>();
			int intro = getRulesList(list, this, rules);
			List<Integer> rulesIn = new ArrayList<Integer>();
			int introIn = getRulesList(listIn, treeIn, rulesIn);
			if (introIn != intro)
				return false;

			for (Integer ruleID : rules) {
				if (!rulesIn.contains(ruleID))
					return false;
			}
		}
		this.isomorphicCount++;
		return true;
	}

	private int getRulesList(List<Integer> traceList, CStoryTrees tree,
			List<Integer> rules) {
		int introCounter = 0;
		for (Integer number : traceList) {
			rules.add(tree.getTraceIDToRuleID().get(number));
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