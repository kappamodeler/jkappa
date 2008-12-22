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
			byte compressionMode) {
		this.nnCS = nnCS;
		this.ruleId = ruleId;
		this.averageTime = nnCS.getAverageTime();
		this.compressionMode = compressionMode;
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

			// Map<Integer, List<CNetworkNotation>> levelToNNList = new
			// HashMap<Integer, List<CNetworkNotation>>();
			// fillLevelToNetworkNotationMap(levelToNNList, commonList);

			noneCompressStoryTrace(weakCompressedList);
			// weakCompressTree(weakCompressedList);
		}

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
							&& sFRComparable.getInternalStateMode() == CNetworkNotation.MODE_TEST) {
						if (!weakTraceIDs.contains(comparableNN.getStep()))
							weakTraceIDs.add(comparableNN.getStep());
					}
					if (isLink
							&& sFRComparable.getLinkStateMode() == CNetworkNotation.MODE_TEST) {
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

	public void fillMaps(boolean isOcamlStyleObsName) {
		levelToTraceID = new HashMap<Integer, List<Integer>>();
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
			CNetworkNotation nn = this.nnCS.getNetworkNotation(traceID);
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
		if (this.getRuleIDToTraceID().size() != treeIn.getRuleIDToTraceID()
				.size())
			return false;

		List<Integer> treeNumbers = getTreeNumbers(this);

		List<Integer> treeNumbersIn = getTreeNumbers(treeIn);

		for (Integer i : treeNumbers) {
			if (treeNumbersIn.contains(i))
				treeNumbersIn.remove(i);
			else
				return false;
		}

		if (treeNumbersIn.size() != 0)
			return false;

		isomorphicCount++;
		averageTime += treeIn.getAverageTime();
		return true;
	}

	private final List<Integer> getTreeNumbers(CStoryTrees treeIn) {
		Iterator<Integer> ruleIterator = treeIn.getRuleIDToTraceID().keySet()
				.iterator();
		List<Integer> treeNumbers = new ArrayList<Integer>();

		while (ruleIterator.hasNext()) {
			int key = ruleIterator.next();
			List<Integer> list = treeIn.getRuleIDToTraceID().get(key);
			for (Integer number : list)
				treeNumbers.add(key);
		}
		return treeNumbers;
	}
}