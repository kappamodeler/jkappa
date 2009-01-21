package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.CNetworkNotation.AgentSitesFromRules;
import com.plectix.simulator.components.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.CNetworkNotation.AgentSitesFromRules.SitesFromRules;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.IState;
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
					pushIntro(updatedList, nn, 0, new ArrayList<Integer>());
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

			while (true) {

				while (mainSize != weakSize) {
					mainSize = weakCompressedList.size();
					noneCompressStoryTrace(weakCompressedList);
					weakCompressedList = updateMainList(weakCompressedList);
					weakSize = weakCompressedList.size();
				}

				// deleting equal blocks
				weakCompressedList = removeEqualLevels(weakCompressedList);
				weakSize = weakCompressedList.size();

				// deleting opposite rules which are not neighbours in trace
				List<Integer> traceIDList = new ArrayList<Integer>();
				List<Integer> traceIDListOpposite = new ArrayList<Integer>();
				int rootStep = weakCompressedList.get(0).getStep();
				List<Integer> weakRelationList = new ArrayList<Integer>();

				isOppositeBranch(rootStep, rootStep, weakCompressedList,
						traceIDList, traceIDListOpposite, weakRelationList);
				Collections.sort(traceIDList);

				for (int deletingTraceID : traceIDList) {
					int deletingIndex = traceIDToIndex.get(deletingTraceID);
					CNetworkNotation nn = weakCompressedList.get(deletingIndex);

					if (!traceIDListOpposite.contains(deletingTraceID))
						pushIntro(weakCompressedList, nn, deletingIndex,
								traceIDList);
					removeTraceIDsFromMaps(nn);
					weakCompressedList.remove(deletingIndex);
				}

				if (weakSize != weakCompressedList.size()) {
					noneCompressStoryTrace(weakCompressedList);
					weakCompressedList = updateMainList(weakCompressedList);
					weakSize = weakCompressedList.size();
				}
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

					int agentNameID = asFR.getAgent().getNameId();
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

//				if (listToCheck.size() != commonList.size()) {
//
//				}

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
			int indexToCheck = traceIDToIndex.get(nnToCheck.getStep());
			int indexToRemove = traceIDToIndex.get(nnToRemove.getStep());

			// pushIntro(nnToCheck, nnToRemove, indexToCheck, indexToRemove);
			copyTraceIDList(nnToRemove, nnToCheck);

			for (int i = begin; i >= end + 1; i--) {
				CNetworkNotation nn = nnList.get(i);
				pushIntro(newList, nn, end, new ArrayList<Integer>());
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
				if (id < lowerBoundTraceID && id >= upperBoundTraceID)
					return true;
			}
		}
		return false;
	}

	private boolean isOppositeBranch(int traceId, Integer mainTraceId,
			List<CNetworkNotation> list, List<Integer> indexList,
			List<Integer> indexListOpposite, List<Integer> weakList) {
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
							indexListOpposite.add(traceId);
						}
					}
					
				}
			}
			isOppositeBranch(trID, mainTraceId, list, indexList,
					indexListOpposite, weakList);
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
			CNetworkNotation currentNN, int indexToBegin,
			List<Integer> listToDelete) {
		if (currentNN == null || !currentNN.isHasIntro())
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

								checkingNN.setHasIntro(true);

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
			Iterator<Integer> siteIterator = aSFR.sites.keySet().iterator();
			int leafIndex = 0;
			while (siteIterator.hasNext()) {
				Integer siteKey = siteIterator.next();
				SitesFromRules sFR = aSFR.sites.get(siteKey);
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

	private void fillMaps(List<CNetworkNotation> nnList) {
		
		
		levelToTraceID = new TreeMap<Integer, List<Integer>>();
		traceIDToIntroString = new HashMap<Integer, List<String>>();
		traceIDToData = new HashMap<Integer, String>();
		traceIDToText = new HashMap<Integer, String>();
		
		List<Long> introAgents = new ArrayList<Long>();

		for (int i = nnList.size() - 1; i >= 0; i--) {
			int counter = 0;
			int index = 0;
			CNetworkNotation nn = nnList.get(i);
			List<String> introStr = new ArrayList<String>();

			for (List<Long> agentIDsList : nn.getIntroCC()) {
				for (Long agentID : agentIDsList) {
					if (nn.getUsedAgentsFromRules().containsKey(agentID))
						if (!introAgents.contains(agentID)) {
							introAgents.add(agentID);
							counter++;
						}
				}
				if (counter == agentIDsList.size()) {
					introStr.add(nn.getAgentsNotation().get(index));

				}
				index++;
				counter = 0;
			}
			traceIDToIntroString.put(nn.getStep(), introStr);
		}
		//traceIDToLevel.size()==7 && 
		if(nnList.get(nnList.size()-2).getRule().getRuleID()==14)
			System.out.println();
		
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

		Iterator<Integer> levelIterator = this.levelToTraceID.keySet()
				.iterator();

		while (levelIterator.hasNext()) {
			int level = levelIterator.next();
			List<Integer> list = this.levelToTraceID.get(level);
			List<Integer> listIn = treeIn.getLevelToTraceID().get(level);
			if (list.size() != listIn.size())
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