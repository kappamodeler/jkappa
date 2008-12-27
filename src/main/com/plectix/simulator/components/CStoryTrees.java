package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.components.CNetworkNotation.*;
import com.plectix.simulator.components.CNetworkNotation.AgentSitesFromRules.SitesFromRules;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.simulator.SimulationData;

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
	private HashMap<Integer, List<Integer>> levelToTraceID;
	private HashMap<Integer, List<String>> traceIDToIntroString;
	private HashMap<Integer, String> traceIDToData;
	private HashMap<Integer, String> traceIDToText;
	private TreeMap<Integer, List<Integer>> traceIDToTraceID;
	private TreeMap<Integer, List<Integer>> traceIDToTraceIDWeak;

	private byte compressionMode;

	public double getAverageTime() {
		return averageTime;
	}

	public void setAverageTime(double averageTime) {
		this.averageTime = averageTime;
	}

	public HashMap<Integer, List<Integer>> getLevelToTraceID() {
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
			byte compressionMode, boolean isOcamlStyleObsName) {
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

	private void weakCompressStoryTrace(List<CNetworkNotation> commonList) {
		List<CNetworkNotation> weakCompressedList = new ArrayList<CNetworkNotation>();
		noneCompressStoryTrace(commonList);

		if (commonList.size() > 1) {
			CNetworkNotation lastNN = null;

			int index = 0;
			for (CNetworkNotation nn : commonList) {
				List<Integer> curList = ruleIDToTraceID.get(nn.getRule()
						.getRuleID());
				if (curList != null) {

					if (weakCompressedList.size() <= 1) {
						weakCompressedList.add(nn);
						lastNN = nn;
					} else if (nn.isOpposite(weakCompressedList)) {
						weakCompressedList.add(nn);
						lastNN = nn;
					} else {
						pushIntro(weakCompressedList, lastNN);
					}
				}
				index++;
			}

			// List<Integer> weakIDsToClear = new ArrayList<Integer>();

			// int size = 1;
			// while (size > 0) {
			// List<CNetworkNotation> nnList =
			// removeBlockFromTrace(weakCompressedList);
			// size = nnList.size();
			// if (size > 0)
			// weakCompressedList = nnList;
			// }
			if (commonList.size() != weakCompressedList.size())
				noneCompressStoryTrace(weakCompressedList);
			
			Map<Integer, Integer> traceIDToIndex = new HashMap<Integer, Integer>();
			int ind = 0;
			for (CNetworkNotation nn : weakCompressedList) {
				traceIDToIndex.put(nn.getStep(), ind);
				ind++;
			}
			List<Integer> traceIDList = new ArrayList<Integer>();
			int rootStep = weakCompressedList.get(0).getStep();
			List<Integer> weakRelationList = new ArrayList<Integer>();

			isOppositeBranch(rootStep, rootStep, weakCompressedList,
					traceIDList, traceIDToIndex, weakRelationList);

			Collections.sort(traceIDList);
			for (int i = traceIDList.size() - 1; i >= 0; i--)
				weakCompressedList.remove(traceIDToIndex.get(traceIDList.get(i)).intValue());

			// System.out.println();

			// for (int i = weakCompressedList.size() - 1; i > 0; i--) {
			// if (!weakIDsToClear.contains(i)) {
			// CNetworkNotation nn1 = weakCompressedList.get(i);
			// for (int j = i - 1; j >= 0; j--) {
			// CNetworkNotation nn2 = weakCompressedList.get(j);
			// if (nn1.getRule().getRuleID() == nn2.getRule()
			// .getRuleID())
			// if (isEqualNetworkNotations(nn1, nn2))
			// weakIDsToClear.add(j);
			// }
			// }
			// }
			//
			// Collections.sort(weakIDsToClear);
			//
			// for (int i = weakIDsToClear.size() - 1; i >= 0; i--) {
			// weakCompressedList.remove(weakIDsToClear.get(i).intValue());
			// }

			// Map<Integer, List<CNetworkNotation>> levelToNNList = new
			// HashMap<Integer, List<CNetworkNotation>>();
			// fillLevelToNetworkNotationMap(levelToNNList, commonList);
			if (commonList.size() != weakCompressedList.size())
				noneCompressStoryTrace(weakCompressedList);
			// weakCompressTree(weakCompressedList);
		}

	}

	private boolean isOppositeBranch(int traceId, Integer mainTraceId,
			List<CNetworkNotation> list, List<Integer> indexList,
			Map<Integer, Integer> traceIDToIndex, List<Integer> weakList) {
		List<Integer> traceIDList = traceIDToTraceID.get(traceId);
		Collections.sort(traceIDList);

		for (int trID : traceIDList) {
			if (traceIDList.size() == 1 && (traceId != mainTraceId)) {
				if (!indexList.contains(trID) && !indexList.contains(traceId)) {
					// if (!weakList.contains(trID) &&
					// !weakList.contains(traceId)) {
					if (!traceIDToIndex.containsKey(traceId)
							|| !traceIDToIndex.containsKey(trID))
						System.out.println();

					int index1 = traceIDToIndex.get(traceId);
					int index2 = traceIDToIndex.get(trID);
					if (!list.get(index1).isOpposite(list.get(index2)))
						if (traceIDToTraceIDWeak.get(traceId).size() == 0) {
							indexList.add(trID);
							indexList.add(traceId);
							/*
							 * } else { weakList.add(trID);
							 * weakList.add(traceId);
							 */
						}
				}
			}
			isOppositeBranch(trID, mainTraceId, list, indexList,
					traceIDToIndex, weakList);
		}

		return true;
	}

	private List<CNetworkNotation> removeBlockFromTrace(
			List<CNetworkNotation> weakCompressedList) {
		List<CNetworkNotation> nnList = new ArrayList<CNetworkNotation>();

		int size = weakCompressedList.size() - 1;
		CNetworkNotation nn = weakCompressedList.get(size);
		int index = -1;
		for (int i = size - 1; i > 0; i--) {
			CNetworkNotation newNN = weakCompressedList.get(i);
			if (isEqualNetworkNotations(nn, newNN)) {
				index = i;
				break;
			}
		}

		if (index > 0) {
			for (int i = 0; i < index; i++) {
				nnList.add(weakCompressedList.get(i));
			}
			return nnList;
		}

		return nnList;
	}

	private void pushTrace(int newIndex, int indexToRemove,
			List<CNetworkNotation> list) {
		List<Integer> numbersToPush = new ArrayList<Integer>();

		CNetworkNotation nnToRemove = list.get(indexToRemove);
		CNetworkNotation nnNew = list.get(newIndex);

	}

	private void getFromTree(List<Integer> numbersToPush, int stepToStop,
			int stepToStart) {
		// List<Integer> list = traceIDToTraceID.getSt
	}

	private boolean hasIndexForTracePermutation(List<CNetworkNotation> list,
			List<Integer> weakIDsToClear) {
		for (int i = 1; i < list.size() - 1; i++) {
			int newIndex = 0;
			while (newIndex >= 0) {
				newIndex = getNextNumberToExclude(i, list, weakIDsToClear,
						i + 1);
				if (newIndex > 0) {
					// do something and return true
				}
			}
		}
		return false;
	}

	private int getNextNumberToExclude(int number, List<CNetworkNotation> list,
			List<Integer> weakIDsToClear, int begin) {
		CNetworkNotation nn = list.get(number);

		for (int i = begin; i < list.size(); i++) {
			CNetworkNotation nnToExclude = list.get(i);
			if (nn.getRule().getRuleID() == nnToExclude.getRule().getRuleID())
				if (isEqualNetworkNotations(nn, nnToExclude))
					return i;
		}
		return -1;
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

	private void findEqualLevels(
			Map<Integer, List<CNetworkNotation>> levelToNNList) {
		Iterator<Integer> currentIterator = levelToNNList.keySet().iterator();
		Iterator<Integer> checkingIterator = levelToNNList.keySet().iterator();

		while (currentIterator.hasNext()) {
			int currentKey = currentIterator.next();
			List<CNetworkNotation> currentList = levelToNNList.get(currentKey);
			while (checkingIterator.hasNext()) {
				int checkingKey = checkingIterator.next();
				if (checkingKey != currentKey) {
					List<CNetworkNotation> checkingList = levelToNNList
							.get(checkingKey);

				}
			}
			checkingIterator = levelToTraceID.keySet().iterator();
		}

	}

	private boolean checkLevel(List<CNetworkNotation> currentNNList,
			List<CNetworkNotation> checkingNNList) {
		if (checkingNNList.size() != currentNNList.size())
			return false;

		return false;
	}

	private void fillLevelToNetworkNotationMap(
			Map<Integer, List<CNetworkNotation>> levelToNNList,
			List<CNetworkNotation> commonList) {
		Iterator<Integer> levelIterator = levelToTraceID.keySet().iterator();

		while (levelIterator.hasNext()) {
			int level = levelIterator.next();
			List<Integer> traceIDList = levelToTraceID.get(level);
			List<CNetworkNotation> nnList = new ArrayList<CNetworkNotation>();
			for (int traceID : traceIDList) {
				CNetworkNotation nn = getNetworkNotationByTraceID(commonList,
						traceID);
				if (nn != null)
					nnList.add(nn);
			}
			levelToNNList.put(level, nnList);
		}

	}

	private CNetworkNotation getNetworkNotationByTraceID(
			List<CNetworkNotation> nnList, int traceID) {
		for (CNetworkNotation nn : nnList) {
			if (nn.getStep() == traceID)
				return nn;
		}
		return null;
	}

	private void weakCompressTree(List<CNetworkNotation> weakCompressedList) {
		createWeakLevels();
	}

	private void createWeakLevels() {
		HashMap<Integer, List<Integer>> levelToRules = new HashMap<Integer, List<Integer>>();
		for (Integer key : levelToTraceID.keySet()) {
			List<Integer> ruleList = new ArrayList<Integer>();
			for (Integer trace : levelToTraceID.get(key)) {
				ruleList.add(getRuleForTraceId(trace));
			}
			levelToRules.put(key, ruleList);
		}

		clearLevelsMap(levelToRules);
	}

	private void clearLevelsMap(HashMap<Integer, List<Integer>> levelToRules) {

		for (int level = 0; level < levelToRules.size(); level++) {
			List<Integer> rulesUp = levelToRules.get(level);

			for (int levelDown = level + 1; levelDown < levelToRules.size(); levelDown++) {
				List<Integer> rulesDown = levelToRules.get(levelDown);
				if (rulesDown != null && rulesUp != null
						&& isEqualsList(rulesUp, rulesDown)) {
					System.out.println("EqualsList");
				}
			}

		}

	}

	private static boolean isEqualsList(List<Integer> rulesUp,
			List<Integer> rulesDown) {
		if (rulesUp.size() != rulesDown.size())
			return false;
		for (Integer up : rulesUp)
			if (!rulesDown.contains(up))
				return false;
		return true;
	}

	private int getRuleForTraceId(int traceId) {
		for (Integer rule : ruleIDToTraceID.keySet()) {
			if (ruleIDToTraceID.get(rule).contains(traceId)) {
				return rule;
			}
		}
		return -1;
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

	private void noneCompressStoryTrace(List<CNetworkNotation> commonList) {
		resetParameters(commonList);
		isCausing(commonList.get(0), commonList, 1, 0);
		if (ruleIDToTraceID.size() == 1)
			commonList.get(0).setHasIntro(true);
	}

	public final void getTreeFromList(List<CNetworkNotation> commonList) {
		switch (compressionMode) {
		case SimulationData.STORIFY_MODE_NONE:
			noneCompressStoryTrace(commonList);
			break;
		case SimulationData.STORIFY_MODE_WEAK:
			weakCompressStoryTrace(commonList);
			break;
		default:
			break;
		}
		pushTree();
		fillMaps();
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
						level++;
						List<Integer> helpList = traceIDToTraceID.get(newNN
								.getStep());
						if (helpList == null
								|| !helpList.contains(comparableNN.getStep()))
							addToConList(newNN, comparableNN, i, level);
						isCausing(comparableNN, commonList, i + 1, level);
						return IS_CAUSE;
					}
					if (!isLink
							&& sFRComparable.getInternalStateMode() == CNetworkNotation.MODE_TEST
							&& sFR.getInternalStateMode() == CNetworkNotation.MODE_TEST_OR_MODIFY) {
						if (!weakTraceIDs.contains(comparableNN.getStep()))
							weakTraceIDs.add(comparableNN.getStep());
					}
					if (isLink
							&& sFRComparable.getLinkStateMode() == CNetworkNotation.MODE_TEST
							&& sFR.getLinkStateMode() == CNetworkNotation.MODE_TEST_OR_MODIFY) {
						if (!weakTraceIDs.contains(comparableNN.getStep()))
							weakTraceIDs.add(comparableNN.getStep());
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

	private void fillMaps() {
		levelToTraceID = new HashMap<Integer, List<Integer>>();
		traceIDToIntroString = new HashMap<Integer, List<String>>();
		traceIDToData = new HashMap<Integer, String>();
		traceIDToText = new HashMap<Integer, String>();

		List<Long> agentsIntro = new ArrayList<Long>();
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
			CNetworkNotation nn = this.nnCS.getNetworkNotation(traceID);
			this.traceIDToRuleID.put(traceID, nn.getRule().getRuleID());
			// if (nn.getAgentsNotation().size() > 0)
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