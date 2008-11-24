package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CStoryTrees {
	private HashMap<Integer, List<Integer>> contiguityList;
	private int ruleId;

	public final int getRuleID() {
		return this.ruleId;
	}

	public final List<Integer> getList(int i) {
		return contiguityList.get(i);
	}
	
	public final HashMap<Integer, List<Integer>> getMap(){
		return contiguityList;
	}

	public CStoryTrees(List<CNetworkNotation> commonList, int ruleId) {
		contiguityList = new HashMap<Integer, List<Integer>>();
		this.ruleId = ruleId;

		for (int i = 0; i < commonList.size(); i++) {
			CNetworkNotation nnCurrent = commonList.get(i);
			int key = nnCurrent.getRule().getRuleID();
			List<Integer> list = contiguityList.get(key);

			if (list == null) {
				list = new ArrayList<Integer>();
				contiguityList.put(key, list);
			}
			if (i + 1 < commonList.size())
				// if ((i > 0) && (i + 1 < commonList.size()))
				list.add(commonList.get(i + 1).getRule().getRuleID());
		}
	}

	public void addListToTree(List<CNetworkNotation> commonList) {
		for (int i = 0; i < commonList.size(); i++) {
			CNetworkNotation nnCurrent = commonList.get(i);
			int key = nnCurrent.getRule().getRuleID();
			List<Integer> list = contiguityList.get(key);

			if (list == null) {
				list = new ArrayList<Integer>();
				contiguityList.put(key, list);
			}
			if (i + 1 < commonList.size())
				// if ((i > 0) && (i + 1 < commonList.size()))
				addToList(list, commonList.get(i + 1).getRule().getRuleID());
		}
	}

	private void addToList(List<Integer> list, int index) {
		if (!list.contains(index))
			list.add(index);
	}

}