package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.components.CNetworkNotation.AgentSites;
import com.plectix.simulator.components.CNetworkNotation.AgentSitesFromRules;
import com.plectix.simulator.components.CNetworkNotation.AgentSitesFromRules.SitesFromRules;

public class CStoryTrees {
	private HashMap<Integer, List<RuleIDs>> contiguityList;
	private int ruleId;

	public final int getRuleID() {
		return this.ruleId;
	}

	public final List<RuleIDs> getList(int i) {
		return contiguityList.get(i);
	}

	public final HashMap<Integer, List<RuleIDs>> getMap() {
		return contiguityList;
	}

	HashMap<Long, HashMap<Integer, StorySites>> sSites;

	public final void getTreeFromList(List<CNetworkNotation> commonList) {
		int index = 0;
		CNetworkNotation newNN = commonList.get(index);
		sSites = new HashMap<Long, HashMap<Integer, StorySites>>();
		addToStorySites(newNN, index, commonList, index + 1);

		getTree(index, 1, newNN, commonList);
	}

	// private void getTree(int index, int begin, CNetworkNotation newNN,
	// List<CNetworkNotation> commonList) {
	// List<Integer> list = new ArrayList<Integer>();
	// int begInd = begin;
	// index++;
	// boolean isTrue = false;
	// if (!contiguityList.keySet().contains(newNN.getRule().getRuleID())) {
	// for (int i = begin; i < commonList.size(); i++) {
	// CNetworkNotation nn = commonList.get(i);
	// addToStorySites(nn, index, commonList, i + 1);
	// //if (!list.contains(nn.getRule().getRuleID()))
	// //if (nn.getRule().getRuleID() != newNN.getRule().getRuleID())
	// list.add(nn.getRule().getRuleID());
	// if (fullCover(index)) {
	// begInd = i + 1;
	// isTrue = true;
	// break;
	// }
	// }
	// addToConList(newNN, list);
	// if (isTrue) {
	// for (int i = begin; i < commonList.size(); i++) {
	// CNetworkNotation nn = commonList.get(i);
	// getTree(index, begInd, nn, commonList);
	// }
	// }
	// }
	// }

	private boolean isCausing(CNetworkNotation newNN,
			List<CNetworkNotation> commonList, int begin) {
		Iterator<Long> agentIterator = newNN.usedAgentsFromRules.keySet()
				.iterator();
		while (agentIterator.hasNext()) {
			Long agentKey = agentIterator.next();
			AgentSitesFromRules aSFR = newNN.usedAgentsFromRules.get(agentKey);
			Iterator<Integer> siteIterator = aSFR.sites.keySet().iterator();
			int leafIndex=0;
			while (siteIterator.hasNext()) {
				Integer siteKey = siteIterator.next();
				SitesFromRules sFR = aSFR.sites.get(siteKey);
				boolean isLink = true;
				if (!isCausing(newNN, commonList, begin, isLink, agentKey, siteKey, sFR)){
					leafIndex++;
				}
				isLink = false;
				if (!isCausing(newNN, commonList, begin, isLink, agentKey, siteKey, sFR)){
					leafIndex++;
				}
			}
			if(aSFR.sites.size()*2 == leafIndex){
				contiguityList.get(newNN);
			}
			
		}

		return false;
	}

	private boolean isCausing(CNetworkNotation newNN,
			List<CNetworkNotation> commonList, int begin, boolean isLink,
			Long agentKey, int siteKey, SitesFromRules sFR) {
		for (int i = begin; i < commonList.size(); i++) {
			CNetworkNotation comparableNN = commonList.get(i);
			AgentSitesFromRules aSFRComparable = comparableNN.usedAgentsFromRules
					.get(agentKey);
			if (aSFRComparable != null) {
				SitesFromRules sFRComparable = aSFRComparable.sites
						.get(siteKey);
				if (sFRComparable != null) {
					if (sFRComparable.isCausing(sFR, isLink)) {
						addToConList(newNN, i);
						return true;
					}
					if (sFRComparable.internalStateMode != CNetworkNotation.MODE_NONE) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void getTree(int index, int begin, CNetworkNotation newNN,
			List<CNetworkNotation> commonList) {
		List<Integer> list = new ArrayList<Integer>();
		int begInd = begin;
		index++;
		boolean isTrue = false;
		if (!contiguityList.keySet().contains(newNN.getRule().getRuleID())) {
			for (int i = begin; i < commonList.size(); i++) {
				CNetworkNotation nn = commonList.get(i);

				addToStorySites(nn, index, commonList, i + 1);
				// if (!list.contains(nn.getRule().getRuleID()))
				// if (nn.getRule().getRuleID() != newNN.getRule().getRuleID())
				list.add(nn.getRule().getRuleID());
				if (fullCover(index)) {
					begInd = i + 1;
					isTrue = true;
					break;
				}
			}
			// addToConList(newNN, list);
			if (isTrue) {
				for (int i = begin; i < commonList.size(); i++) {
					CNetworkNotation nn = commonList.get(i);
					getTree(index, begInd, nn, commonList);
				}
			}
		}
	}

	// private void addToConList(CNetworkNotation nn, List<Integer> list) {
	// Integer key = nn.getRule().getRuleID();
	// List<Integer> cList = contiguityList.get(key);
	// if (cList == null) {
	// cList = new ArrayList<Integer>();
	// contiguityList.put(key, cList);
	// }
	// cList.addAll(list);
	// }

	class RuleIDs {
		int ruleID;
		int indexInTrace;
		boolean isLeaf;

		public RuleIDs(int ruleID, int indexInTrace) {
			this.ruleID = ruleID;
			this.indexInTrace = indexInTrace;
			this.isLeaf = false;
		}

		public final boolean equals(Object obj) {
			if (!(obj instanceof RuleIDs))
				return false;
			RuleIDs ruleID = (RuleIDs) obj;
			if (indexInTrace != ruleID.indexInTrace)
				return false;
			return true;
		}
	}

	private void addToConList(CNetworkNotation nn, int index) {
		Integer key = nn.getRule().getRuleID();
		List<RuleIDs> cList = contiguityList.get(key);
		if (cList == null) {
			cList = new ArrayList<RuleIDs>();
			contiguityList.put(key, cList);
		}
		RuleIDs rID = new RuleIDs(key, index);
		if (!(cList.contains(rID)))
			cList.add(rID);
	}

	private boolean fullCover(int index) {
		Iterator<Long> iterator = sSites.keySet().iterator();

		while (iterator.hasNext()) {
			Long key = iterator.next();
			HashMap<Integer, StorySites> ss = sSites.get(key);

			Iterator<Integer> siteIterator = ss.keySet().iterator();
			while (siteIterator.hasNext()) {
				Integer keySite = siteIterator.next();
				StorySites ssSite = ss.get(keySite);

				if (ssSite.level != index && ssSite.isLeaf == false) {
					return false;
				}
			}
		}

		return true;
	}

	public void merge(HashMap<Integer, List<Integer>> conList, int ruleID) {
//		List<Integer> list = this.contiguityList.get(ruleID);
//		List<Integer> inList = conList.get(ruleID);
//		// List<Integer> newList = new ArrayList<Integer>();
//		// newList.addAll(list);
//
//		if (list != null && inList != null)
//			for (Integer id : inList) {
//				if (!list.contains(id)) {
//					list.add(id);// newList.add(id);
//				} else {
//					merge(conList, id);
//				}
//			}
//		// list = newList;
	}

	private void addToStorySites(CNetworkNotation nn, int index,
			List<CNetworkNotation> commonList, int begin) {
		HashMap<Long, AgentSites> chAFS = nn.changedAgentsFromSolution;
		Iterator<Long> iterator = chAFS.keySet().iterator();

		while (iterator.hasNext()) {
			Long key = iterator.next();
			HashMap<Integer, StorySites> ss = sSites.get(key);

			AgentSites as = chAFS.get(key);

			if (ss == null) {
				ss = new HashMap<Integer, StorySites>();
				sSites.put(key, ss);
			}

			Iterator<Integer> siteIterator = as.sites.keySet().iterator();
			while (siteIterator.hasNext()) {
				Integer keySite = siteIterator.next();
				StorySites ssSite = ss.get(keySite);

				if (ssSite == null) {
					ssSite = new StorySites(index, keySite, false);
					ssSite.checkLeaf(commonList, begin, key);
					ss.put(keySite, ssSite);
				} else {
					ssSite.level = index;
				}
			}
		}
	}

	class StorySites {
		boolean isLeaf;
		int level = 0;
		int siteID;

		public StorySites(int level, int siteID, boolean isLeaf) {
			this.level = level;
			this.siteID = siteID;
			this.isLeaf = isLeaf;
		}

		public void checkLeaf(List<CNetworkNotation> commonList, int begin,
				long key) {
			for (int i = begin; i < commonList.size(); i++) {
				CNetworkNotation nn = commonList.get(i);
				AgentSites as = nn.changedAgentsFromSolution.get(key);
				if (as != null) {
					CStoriesSiteStates sss = (CStoriesSiteStates) as.sites
							.get(this.siteID);
					if (sss != null)
						return;
				}
			}
			this.isLeaf = true;
		}
	}

	public CStoryTrees(int ruleId) {
	//	contiguityList = new HashMap<Integer, List<Integer>>();
		this.ruleId = ruleId;
	}
}