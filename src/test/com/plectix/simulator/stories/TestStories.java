package com.plectix.simulator.stories;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import com.plectix.simulator.components.stories.CStoryTrees;
import com.plectix.simulator.interfaces.IRule;

public class TestStories {
	private Collection<List<CStoryTrees>> trees;
	private static String path;
	private static List<IRule> rules;

	private List<TreeMap<Integer, List<Integer>>> myTraceList;
	private TreeMap<Integer, List<Integer>> trace;
	private TreeMap<Integer, Integer> traceRule;

	public TestStories(Collection<List<CStoryTrees>> storyTrees,
			List<IRule> rulesList, String resultPath) {
		trees = storyTrees;
		path = resultPath;
		rules = rulesList;
	}

	public void test() {
		myTraceList = getMyTraceList();
		for (List<CStoryTrees> list : trees) {
			for (CStoryTrees storyTrees : list) {
				trace = storyTrees.getTraceIDToTraceID();
				traceRule = storyTrees.getTraceIDToRuleID();

				if (!findTrace(trace, traceRule, myTraceList))
					fail("not found graph");
			}
		}
	}

	private String getRuleNameById(Integer key) {
		Integer ruleId = traceRule.get(key);
		for (IRule rule : rules) {
			if (rule.getRuleID() == ruleId)
				return rule.getName();
		}
		return "null";
	}

	private boolean isEqual(TreeMap<Integer, List<Integer>> trace2,
			TreeMap<Integer, List<Integer>> treeMap) {
		for (Integer key : trace2.keySet()) {
			if (!treeMap.keySet().contains(key))
				return false;
			else if (!isEqual(trace2.get(key), treeMap.get(key)))
				return false;
			else
				treeMap.remove(key);
		}
		if (treeMap.isEmpty())
			return true;
		return false;
	}

	private boolean isEqual(List<Integer> list1, List<Integer> list2) {
		for (Integer i : list1) {
			if (!list2.contains(i))
				return false;
			else
				list2.remove(i);
		}
		if (list2.isEmpty())
			return true;
		return false;
	}

	private TreeMap<Integer, List<Integer>> replaceIdtoRule(
			TreeMap<Integer, List<Integer>> trace2,
			TreeMap<Integer, Integer> rules2) {
		List<Integer> list;
		TreeMap<Integer, List<Integer>> trace = new TreeMap<Integer, List<Integer>>();
		for (Integer key : trace2.keySet()) {
			list = new ArrayList<Integer>();
			for (Integer i : trace2.get(key)) {
				list.add(Integer.valueOf(getRuleNameById(i)));
			}
			trace.put(Integer.valueOf(getRuleNameById(key)), list);
		}
		return trace;
	}

	private boolean findTrace(TreeMap<Integer, List<Integer>> trace,
			TreeMap<Integer, Integer> rules,
			List<TreeMap<Integer, List<Integer>>> traceList) {
		trace = replaceIdtoRule(trace, rules);
		for (TreeMap<Integer, List<Integer>> treeMap : traceList) {
			if (isEqual(trace, treeMap))
				return true;
		}
		return false;
	}

	private List<TreeMap<Integer, List<Integer>>> getMyTraceList() {
		File file = new File(path);
		FileReader reader;
		String line = null;
		String[] keys = null;
		List<TreeMap<Integer, List<Integer>>> traceList = new ArrayList<TreeMap<Integer, List<Integer>>>();
		TreeMap<Integer, List<Integer>> trace = new TreeMap<Integer, List<Integer>>();
		Integer key;
		List<Integer> nodes = new ArrayList<Integer>();
		try {
			reader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(reader);
			line = bfr.readLine();
			while (line != null) {
				keys = line.split(" ");
				if (keys.length > 0) {
					key = Integer.valueOf(keys[0]);
					for (int i = 1; i < keys.length; i++) {
						nodes.add(Integer.valueOf(keys[i]));
					}
					trace.put(key, nodes);
					nodes = new ArrayList<Integer>();
				} else {
					traceList.add(trace);
					trace = new TreeMap<Integer, List<Integer>>();
				}
				line = bfr.readLine();
			}
			traceList.add(trace);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return traceList;
	}
}
