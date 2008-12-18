package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.components.CNetworkNotation.*;
import com.plectix.simulator.components.CNetworkNotation.AgentSitesFromRules.SitesFromRules;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;

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
	private HashMap<Integer, List<Integer>> traceIDToTraceID;
SimulationData simData;
	
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
		this.traceIDToTraceID = new HashMap<Integer, List<Integer>>();

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

			noneCompressStoryTrace(weakCompressedList);
		}

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
				byte isCause = isCausing(newNN, commonList, begin, isLink,
						agentKey, siteKey, sFR, level);
				if (isCause == IS_NOT_CAUSE || isCause == IS_NONE) {
					leafIndex++;
				}
				isLink = false;
				isCause = isCausing(newNN, commonList, begin, isLink, agentKey,
						siteKey, sFR, level);
				if (isCause == IS_NOT_CAUSE || isCause == IS_NONE) {
					leafIndex++;
				}

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
			Long agentKey, int siteKey, SitesFromRules sFR, int level) {
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
					// if (!isLink
					// && sFRComparable.getInternalStateMode() !=
					// CNetworkNotation.MODE_NONE) {
					// return IS_NONE;
					// }
					// if (isLink
					// && sFRComparable.getLinkStateMode() !=
					// CNetworkNotation.MODE_NONE) {
					// return IS_NONE;
					// }
				}
			}
		}
		return IS_NOT_CAUSE;
	}

	private void pushTree() {

		Iterator<Integer> ruleIterator = ruleIDToTraceID.keySet().iterator();

		while (ruleIterator.hasNext()) {
			int key = ruleIterator.next();
			List<Integer> currentTraceIDList = ruleIDToTraceID.get(key);

			for (Integer currentTraceID : currentTraceIDList) {
				List<Integer> curList = new ArrayList<Integer>();
				List<Integer> traceIDList = traceIDToTraceID
						.get(currentTraceID);
				for (int traceID : traceIDList) {
					Integer rightLevel = traceIDToLevel.get(traceID);
					Integer checkingLevel = traceIDToLevel.get(currentTraceID) + 1;
					if ((rightLevel != null) && rightLevel == checkingLevel)
						curList.add(traceID);
				}
				traceIDToTraceID.put(currentTraceID, curList);
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
			int counter=0;
			int index=0;
			if (nn.isHasIntro()){
				List<String> introStr = new ArrayList<String>();
				
				for (IConnectedComponent cc : nn.getIntroCC()){
					for (IAgent agent : cc.getAgents()){
						if(!introAgents.contains(agent.getId())){
							introAgents.add(agent.getId());
							counter++;
						}
					}
					if(counter == cc.getAgents().size())
						introStr.add(nn.getAgentsNotation().get(index));
					index++;
					counter=0;
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

	public HashMap<Integer, List<Integer>> getTraceIDToTraceID() {
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