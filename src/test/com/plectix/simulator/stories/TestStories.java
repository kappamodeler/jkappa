package com.plectix.simulator.stories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.stories.CStoryTrees;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.util.Failer;

public class TestStories extends InitStoriesTests {

	private Collection<List<CStoryTrees>> trees;
	private static String path = "test.data/stories/";
	private static String results = "results/";
	private static String simple = "simple/";

	private Failer myFailer = new Failer();

	private String fileName = "";

	private static List<IRule> rules;

	private List<TreeMap<Integer, List<Integer>>> myTraceList;
	private TreeMap<Integer, List<Integer>> trace;
	private TreeMap<Integer, Integer> traceRule;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(path + simple);
	}

	public TestStories(String testFileName) {
		super(path + simple, testFileName);
		fileName = testFileName;
	}

	@Test
	public void test() {
		trees = getStories().getTrees();
		rules = getRules();
		myTraceList = getMyTraceList();
		TreeMap<Integer, List<Integer>> foundedTrace = new TreeMap<Integer, List<Integer>>();
		for (List<CStoryTrees> list : trees) {
			for (CStoryTrees storyTrees : list) {
				trace = storyTrees.getTraceIDToTraceID();
				traceRule = storyTrees.getTraceIDToRuleID();
				trace = replaceIdtoRule(trace);
				if ((foundedTrace = findTrace(trace, myTraceList)) == null) {
					myFailer.assertTrue("There is an unnecessary graph: \n"
							+ getMap(trace), false);
				} else {
					myTraceList.remove(foundedTrace);
				}
			}
		}
		if (!myTraceList.isEmpty()) 
			myFailer.assertTrue("Not found the graph", false);
	}

	private String getMap(TreeMap<Integer, List<Integer>> map) {
		StringBuffer sb = new StringBuffer();
		for (Integer key : map.keySet()) {
			sb.append("*" + key + "* ");
			if (map.get(key).isEmpty())
				sb.append("-> empty");
			for (Integer integer : map.get(key)) {
				sb.append("->" + integer);
			}
			sb.append("\n");
		}
		return sb.toString();
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
		if (trace2.size() != treeMap.size())
			return false;

		for (Integer key : trace2.keySet()) {
			if (!treeMap.keySet().contains(key))
				return false;
			else if (!isEqual(trace2.get(key), treeMap.get(key)))
				return false;
		}
		return true;
	}

	private boolean isEqual(List<Integer> list1, List<Integer> list2) {
		if (list1.size()!=list2.size())
			return false;
		for (Integer i : list1) {
			if (!list2.contains(i))
				return false;
		}
		return true;
	}

	private TreeMap<Integer, List<Integer>> replaceIdtoRule(
			TreeMap<Integer, List<Integer>> trace2) {
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

	private TreeMap<Integer, List<Integer>> findTrace(
			TreeMap<Integer, List<Integer>> traceToFind,
			List<TreeMap<Integer, List<Integer>>> traceList) {
		for (TreeMap<Integer, List<Integer>> trace : traceList) {
			if (isEqual(traceToFind, trace))
				return trace;
		}
		return null;
	}

	private List<TreeMap<Integer, List<Integer>>> getMyTraceList() {
		File file = new File(path + results + fileName);
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
				if (line.length() != 0) {
					keys = line.split(" ");
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
