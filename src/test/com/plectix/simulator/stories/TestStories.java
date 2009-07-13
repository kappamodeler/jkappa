//package com.plectix.simulator.stories;
//
//import java.io.*;
//import java.util.*;
//
//import org.junit.Test;
//import org.junit.runners.Parameterized.Parameters;
//
//import com.plectix.simulator.components.CRule;
//import com.plectix.simulator.components.stories.CStoryTrees;
//
//import com.plectix.simulator.util.Failer;
//
//public class TestStories extends InitStoriesTests {
//
//	private static final String separator = File.separator;
//	private static final String path = "test.data" + separator + "stories" + separator;
//	private static final String results = "results" + separator;
//	private static final String simple = "simple" + separator;
//
//	private Collection<List<CStoryTrees>> trees;
//	private Failer myFailer = new Failer();
//
//	private String fileName = "";
//
//	private static List<CRule> rules;
//
//	private List<TreeMap<Integer, List<Integer>>> myTraceList;
//	private TreeMap<Integer, List<Integer>> trace;
//	private TreeMap<Integer, Integer> traceRule;
//
//	@Parameters
//	public static Collection<Object[]> regExValues() {
//		return getAllTestFileNames(path + simple);
//	}
//
//	public TestStories(String testFileName) {
//		super(path + simple, testFileName, true);
//		fileName = testFileName;
//	}
//
//	@Test
//	public void test() {
//		trees = getStories().getTrees();
//		rules = getRules();
//		myTraceList = getMyTraceList();
//		TreeMap<Integer, List<Integer>> foundedTrace = new TreeMap<Integer, List<Integer>>();
//		for (List<CStoryTrees> list : trees) {
//			for (CStoryTrees storyTrees : list) {
//				trace = storyTrees.getTraceIDToTraceID();
//				traceRule = storyTrees.getTraceIDToRuleID();
//				trace = replaceIdtoRule(trace);
//				if ((foundedTrace = findTrace(trace, myTraceList)) == null) {
//					myFailer.assertTrue("There is an unnecessary graph: \n"
//							+ getMap(trace), false);
//				} else {
//					myTraceList.remove(foundedTrace);
//				}
//			}
//		}
//		if (!myTraceList.isEmpty()) 
//			myFailer.assertTrue("Not found the graph" + tostring(myTraceList), false);
//	}
//
//	private String tostring(List<TreeMap<Integer, List<Integer>>> mapList) {
//		StringBuffer sb = new StringBuffer();
//		for (TreeMap<Integer, List<Integer>> treeMap : mapList) {
//			sb.append("\n");
//			for (Map.Entry<Integer, List<Integer>> treeMapEntry : treeMap.entrySet()) {
//				sb.append("\n" + treeMapEntry.getKey());
//				for (Integer value : treeMapEntry.getValue()) {
//					sb.append(" " + value);
//				}
//			}
//		}
//		return sb.toString();
//	}
//
//	private String getMap(TreeMap<Integer, List<Integer>> map) {
//		StringBuffer sb = new StringBuffer();
//		for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
//			sb.append("*" + entry.getKey() + "* ");
//			if (entry.getValue().isEmpty()) {
//				sb.append("-> empty");
//			} else {
//				for (Integer integer : entry.getValue()) {
//					sb.append("->" + integer);
//				}
//			}
//			sb.append("\n");
//		}
//		return sb.toString();
//	}
//
//	private String getRuleNameById(Integer key) {
//		Integer ruleId = traceRule.get(key);
//		for (CRule rule : rules) {
//			if (rule.getRuleID() == ruleId)
//				return rule.getName();
//		}
//		return "null";
//	}
//
//	private boolean isEqual(TreeMap<Integer, List<Integer>> trace2,
//			TreeMap<Integer, List<Integer>> treeMap) {
//		if (trace2.size() != treeMap.size())
//			return false;
//
//		for (Map.Entry<Integer, List<Integer>> entry : trace2.entrySet()) {
//			if (!treeMap.keySet().contains(entry.getKey())) {
//				return false;
//			} else if (!isEqual(entry.getValue(), treeMap.get(entry.getKey()))) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	private boolean isEqual(List<Integer> list1, List<Integer> list2) {
//		if (list1.size()!=list2.size())
//			return false;
//		for (Integer i : list1) {
//			if (!list2.contains(i))
//				return false;
//		}
//		return true;
//	}
//
//	private TreeMap<Integer, List<Integer>> replaceIdtoRule(
//			TreeMap<Integer, List<Integer>> trace2) {
//		List<Integer> list;
//		TreeMap<Integer, List<Integer>> trace = new TreeMap<Integer, List<Integer>>();
//		for (Map.Entry<Integer, List<Integer>> entry : trace2.entrySet()) {
//			list = new ArrayList<Integer>();
//			for (Integer i : entry.getValue()) {
//				list.add(Integer.valueOf(getRuleNameById(i)));
//			}
//			trace.put(Integer.valueOf(getRuleNameById(entry.getKey())), list);
//		}
//		return trace;
//	}
//
//	private TreeMap<Integer, List<Integer>> findTrace(
//			TreeMap<Integer, List<Integer>> traceToFind,
//			List<TreeMap<Integer, List<Integer>>> traceList) {
//		for (TreeMap<Integer, List<Integer>> trace : traceList) {
//			if (isEqual(traceToFind, trace))
//				return trace;
//		}
//		return null;
//	}
//
//	private List<TreeMap<Integer, List<Integer>>> getMyTraceList() {
//		File file = new File(path + results + fileName);
//		FileReader reader;
//		String line = null;
//		String[] keys = null;
//		List<TreeMap<Integer, List<Integer>>> traceList = new ArrayList<TreeMap<Integer, List<Integer>>>();
//		TreeMap<Integer, List<Integer>> trace = new TreeMap<Integer, List<Integer>>();
//		Integer key;
//		List<Integer> nodes = new ArrayList<Integer>();
//		try {
//			reader = new FileReader(file);
//			BufferedReader bfr = new BufferedReader(reader);
//			line = bfr.readLine();
//			while (line != null) {
//				if (line.length() != 0) {
//					keys = line.split(" ");
//					key = Integer.valueOf(keys[0]);
//					for (int i = 1; i < keys.length; i++) {
//						nodes.add(Integer.valueOf(keys[i]));
//					}
//					trace.put(key, nodes);
//					nodes = new ArrayList<Integer>();
//				} else {
//					traceList.add(trace);
//					trace = new TreeMap<Integer, List<Integer>>();
//				}
//				line = bfr.readLine();
//			}
//			traceList.add(trace);
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return traceList;
//	}
//}
