package com.plectix.simulator.components.stories;

import java.util.*;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.stories.compressions.CompressionPassport;
import com.plectix.simulator.components.stories.storage.AbstractStorage;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.graphs.MergeStoriesGraphs;
import com.plectix.simulator.components.stories.storage.graphs.StoriesGraphs;
import com.plectix.simulator.components.stories.storage.graphs.UniqueGraph;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;

public final class CStories {

	private int iterations = 10;

	private final List<Integer> aplliedRulesIds;
	private final Map<Integer,IWireStorage> eventsMapForCurrentStory;

	private SimulationData simulationData;

	public CStories(SimulationData simData) {
		this.simulationData = simData;
		this.iterations = simData.getSimulationArguments().getIterations();
		this.aplliedRulesIds = new ArrayList<Integer>();
		this.eventsMapForCurrentStory = new LinkedHashMap<Integer, IWireStorage>();
		for (int i = 0; i < iterations; i++) {
			this.eventsMapForCurrentStory.put(new Integer(i),new AbstractStorage(simulationData
					.getSimulationArguments().getStorifyMode(), i));
		}
	}

	public int getRuleIdAtStories(int index) {
		if (index >= aplliedRulesIds.size() || index < 0)
			return -1;
		return aplliedRulesIds.get(index);
	}

//	public final Collection<List<CStoryTrees>> getTrees() {
//		return Collections.unmodifiableCollection(trees.values());
//	}

	/**
	 * param index ; root of cleaning
	 */
	public final void cleaningStory(int index) throws StoryStorageException{
		if (eventsMapForCurrentStory.get(index).isImportantStory()){
			eventsMapForCurrentStory.get(index).handling();
		}
		else{
			//delete this story
			eventsMapForCurrentStory.get(index).clearList();
			ThreadLocalData.getTypeById().resetTypesOfAgents(eventsMapForCurrentStory.get(index).getIteration());
		}
	}

	public final void addToStories(List<Integer> ruleIDs) {
		for (Integer ruleId : ruleIDs) {
			this.aplliedRulesIds.add(ruleId);
		}
	}

	public final void addEventToStory(int index,
			CEvent eventContainer) throws StoryStorageException {
		eventsMapForCurrentStory.get(Integer.valueOf(index)).addEventContainer(eventContainer,false);
	}

	public final void addLastEventToStoryStorifyRule(int index,
			CEvent eventContainer,
			double currentTime) throws StoryStorageException  {
		this.eventsMapForCurrentStory.get(Integer.valueOf(index))
				.addLastEventContainer(eventContainer, currentTime);
	}

	public final boolean checkRule(int ruleToBeChecked, int index) {
		if (aplliedRulesIds.contains(ruleToBeChecked)) {
			eventsMapForCurrentStory.get(Integer.valueOf(index)).setEndOfStory();
			return true;
		} else {
			return false;
		}
	}

	public final void merge() {
//		for (Integer key : aplliedRulesIds) {
//			List<CStoryTrees> treeList = trees.get(key);
//			if (treeList == null) {
//				treeList = new ArrayList<CStoryTrees>();
//				trees.put(key, treeList);
//			}
//
//			for (NetworkNotationForCurrentStory nnCS : networkNotationForCurrentStory) {
//				if (!nnCS.getNetworkNotationList().isEmpty())
//					if (nnCS.getNetworkNotationList().get(0).getRule()
//							.getRuleID() == key) {
//
//						CStoryTrees tree = new CStoryTrees(key, nnCS
//								.getAverageTime(), simulationData
//								.getSimulationArguments().getStorifyMode(),
//								simulationData.isOcamlStyleObsName());
//						tree.getTreeFromList(nnCS.getNetworkNotationList());
//
//						if (treeList.isEmpty())
//							treeList.add(tree);
//						else {
//							boolean isAdd = true;
//							for (CStoryTrees sTree : treeList)
//								if (sTree.isIsomorphic(tree)) {
//									isAdd = false;
//									break;
//								}
//							if (isAdd)
//								treeList.add(tree);
//						}
//					}
//			}
//
//		}
	}
	
	public Map<Integer, IWireStorage> getEventsMapForCurrentStory() {
		// TODO Auto-generated method stub
		return eventsMapForCurrentStory;
	}

	public void createXML(XMLStreamWriter writer, KappaSystem myKappaSystem,
			int numberOfIterations) throws XMLStreamException,
			StoryStorageException {
		MergeStoriesGraphs merging = new MergeStoriesGraphs(this);
		merging.merge();
		AbstractList<UniqueGraph> list = merging.getListUniqueGraph();
		for (UniqueGraph uniqueGraph : list) {
			writer.writeStartElement("Story");
			writer.writeAttribute("Observable", myKappaSystem.getRuleByID(
					uniqueGraph.getGraph().getPassport().getStorage()
							.observableEvent().getRuleId()).getName());
			Double percentage = uniqueGraph.getPersent();
			 writer.writeAttribute("Percentage", Double.toString(percentage * 100));
			 writer.writeAttribute("Average", Double.toString(uniqueGraph.getAverageTime()));
			CompressionPassport passport = uniqueGraph.getGraph().getPassport();
			StoriesGraphs graph = uniqueGraph.getGraph();

			for (IEventIterator eventIterator = passport.eventIterator(true); eventIterator
					.hasNext();) {
				long eventId = (long) eventIterator.next();
				if (eventId != -1)
					addNode(eventId, graph, writer, myKappaSystem);
				else
					addIntros(graph, writer);
			}
			addConnections(graph, writer);
			writer.writeEndElement();
		}
		
		
	}

	private void addConnections(StoriesGraphs graph, XMLStreamWriter writer)
			throws XMLStreamException {
		long id1, id2;
		for (Entry<Long, LinkedHashSet<Long>> connSet : graph.getConnections()
				.entrySet()) {
			id1 = connSet.getKey();
			for (Long id : connSet.getValue()) {
				id2 = id;
					writer.writeStartElement("Connection");
					writer.writeAttribute("FromNode", Long.valueOf(id2)
							.toString());
					writer.writeAttribute("ToNode", Long.valueOf(id1)
							.toString());
					writer.writeAttribute("Relation", "STRONG");
					writer.writeEndElement();
			}
		}

	}

	private void addIntros(StoriesGraphs graph, XMLStreamWriter writer)
			throws XMLStreamException, StoryStorageException {
		Set<Long> set = new LinkedHashSet<Long>();
		for(Set<Long> s : graph.getConnections().values())
			set.addAll(s);
		for (Entry<Long, String> entry : graph.getIntroCCIdtoData().entrySet()) {
			if(!set.contains(entry.getKey()))
				continue;
			int depth = graph.getIntroDepth(entry.getKey());
			if (depth != -1){
			writer.writeStartElement("Node");
			writer
					.writeAttribute("Id", Long.valueOf(entry.getKey())
							.toString());
			writer.writeAttribute("Type", "INTRO");
			writer.writeAttribute("Text", "intro:" + entry.getValue());
			writer.writeAttribute("Data", "");
			writer.writeAttribute("Depth", Integer.valueOf(depth)
					.toString());
			writer.writeEndElement();
			}
		}
	}
	
	
	
	private void addNode(long eventId, StoriesGraphs graph,
			XMLStreamWriter writer, KappaSystem kappaSystem)
			throws XMLStreamException, StoryStorageException {
		writer.writeStartElement("Node");
		writer.writeAttribute("Id", Long.valueOf(
				graph.getNodeIdByEventId(eventId)).toString());
		if (eventId != graph.getPassport().getStorage().observableEvent().getStepId())
			writer.writeAttribute("Type", "RULE");
		else
			writer.writeAttribute("Type", "OBSERVABLE");
		CRule rule = kappaSystem.getRuleByID(graph.getEventByStepId(eventId)
				.getRuleId());
		writer.writeAttribute("Text", rule.getName());
		writer.writeAttribute("Data", SimulationData.getData(rule, true));
		writer.writeAttribute("Depth", graph.getEventDepth(eventId)
				.toString());
		writer.writeEndElement();

	}

}
