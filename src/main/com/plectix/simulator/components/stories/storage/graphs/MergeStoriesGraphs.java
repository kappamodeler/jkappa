package com.plectix.simulator.components.stories.storage.graphs;

import java.util.AbstractList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;


public class MergeStoriesGraphs {

	private CStories stories;
	private AbstractList<UniqueGraph> listUniqueGraph = new LinkedList<UniqueGraph>();
	
	public MergeStoriesGraphs(CStories _stories) {
		stories = _stories;
	}
	
	public void merge() throws StoryStorageException {

		for (IWireStorage story : getEventsMapForCurrentStory().values()) {
			if(story.isImportantStory()) {
				
				findEqualGraph(story);
			}
		}
		
	}
	
	public AbstractList<UniqueGraph> getListUniqueGraph() {
		return listUniqueGraph;
	}
	
	private Map<Integer, IWireStorage> getEventsMapForCurrentStory() {
		
		return stories.getEventsMapForCurrentStory();
		
	}
	
	private StoriesGraphs getStoriesGraphs(IWireStorage story) throws StoryStorageException {
		StoriesGraphs graph = story.extractPassport().extractGraph();
		graph.buildGraph();
		return graph;
		
	}
	
	private void findEqualGraph(IWireStorage story) throws StoryStorageException {
		
		StoriesGraphs graphs1 = getStoriesGraphs(story);

		boolean isEqual = false;
		
		for (UniqueGraph uGraph : listUniqueGraph) {
			
			StoriesGraphs graphs2 = uGraph.getGraph();
			
			if(equalGraph(graphs1,graphs2)) {
				uGraph.incrementCount();
				uGraph.addAverageTime(graphs2.getPassport().getStorage().getAverageTime());
				isEqual = true;
				return;
			}
			
		}

		if(!isEqual) {
			addNewGraph(graphs1);
		}
	}

	private void addNewGraph(StoriesGraphs graph) {
		listUniqueGraph.add(new UniqueGraph(graph, getEventsMapForCurrentStory().size(), graph.getPassport().getStorage().getAverageTime()));
	}

	private boolean equalGraph(StoriesGraphs graphs1, StoriesGraphs graphs2) {
		TreeMap<Long,LinkedHashSet<Long>> connection1 = graphs1.getConnections();
		TreeMap<Long,LinkedHashSet<Long>> connection2 = graphs2.getConnections();
		
		
		TreeMap<Integer, LinkedHashSet<Integer>> tree1;
		TreeMap<Integer, LinkedHashSet<Integer>> tree2;
		tree1 = buildRuleGraph(graphs1, connection1);
		tree2 = buildRuleGraph(graphs2, connection2);
		
		if(tree1.size()!= tree2.size()) return false;
		for (Integer key : tree1.keySet()) {
			if (!tree2.containsKey(key)){
				return false;
			}
			if (tree1.get(key).size() != tree2.get(key).size()){
				return false;
			}
			for (Integer node : tree1.get(key)) {
				if (!tree2.get(key).contains(node))
					return false;
			}
		}
		
		return true;
		
	}

	private TreeMap<Integer, LinkedHashSet<Integer>> buildRuleGraph(StoriesGraphs graph,
			TreeMap<Long, LinkedHashSet<Long>> connection) {
		TreeMap<Integer, LinkedHashSet<Integer>> ruleGraph = new TreeMap<Integer, LinkedHashSet<Integer>>();
		LinkedHashSet<Integer> set;
		int introSize = graph.getIntroCCIdtoData().size();
		for(Long key: connection.keySet()){
			Long event = graph.getEventIdByNodeId(key);
			int rule = graph.getEventByStepId(event).getRuleId();
			set = new LinkedHashSet<Integer>();
			for (Long nodeId : connection.get(key)) {
				if (nodeId > introSize){
					Long eventId = graph.getEventIdByNodeId(nodeId);
					int ruleId = graph.getEventByStepId(eventId).getRuleId();
					set.add(ruleId);
				}
			}
			ruleGraph.put(rule, set);
		}
		return ruleGraph;
	}

	public Long[] getLinkedHashSetFromConnectionsToLong(StoriesGraphs graph) {
		Collection<LinkedHashSet<Long>> qw = graph.getConnections().values();
		return qw.toArray(new Long [graph.getConnections().values().size()]);
	}

	
	
	
}
