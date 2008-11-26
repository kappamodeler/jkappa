package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.components.CNetworkNotation.AgentSites;

public class CStoryTrees {
	private HashMap<Integer, List<Integer>> contiguityList;
	private int ruleId;

	public final int getRuleID() {
		return this.ruleId;
	}

	public final List<Integer> getList(int i) {
		return contiguityList.get(i);
	}

	public final HashMap<Integer, List<Integer>> getMap() {
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
				//if (!list.contains(nn.getRule().getRuleID()))
					//if (nn.getRule().getRuleID() != newNN.getRule().getRuleID())
						list.add(nn.getRule().getRuleID());
				if (fullCover(index)) {
					begInd = i + 1;
					isTrue = true;
					break;
				}
			}
			addToConList(newNN, list);
			if (isTrue) {
				for (int i = begin; i < commonList.size(); i++) {
					CNetworkNotation nn = commonList.get(i);
					getTree(index, begInd, nn, commonList);
				}
			}
		}
	}

	private void addToConList(CNetworkNotation nn, List<Integer> list) {
		Integer key = nn.getRule().getRuleID();
		List<Integer> cList = contiguityList.get(key);
		if (cList == null) {
			cList = new ArrayList<Integer>();
			contiguityList.put(key, cList);
		}
		cList.addAll(list);
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
		List<Integer> list = this.contiguityList.get(ruleID);
		List<Integer> inList = conList.get(ruleID);
		//List<Integer> newList = new ArrayList<Integer>();
		//newList.addAll(list);

		if (list != null && inList != null)
			for (Integer id : inList) {
				if (!list.contains(id)) {
					list.add(id);//newList.add(id);
				} else {
					merge(conList, id);
				}
			}
		//list = newList;
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
		contiguityList = new HashMap<Integer, List<Integer>>();
		this.ruleId = ruleId;
	}
}