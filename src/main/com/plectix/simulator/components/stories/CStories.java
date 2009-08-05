package com.plectix.simulator.components.stories;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.plectix.simulator.components.stories.storage.graphs.Connection;
import com.plectix.simulator.components.stories.storage.graphs.MergeStoriesGraphs;
import com.plectix.simulator.components.stories.storage.graphs.StoriesGraphs;
import com.plectix.simulator.components.stories.storage.graphs.UniqueGraph;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;

public final class CStories {


	private final List<Integer> aplliedRulesIds;
	private final Map<Integer,IWireStorage> eventsMapForCurrentStory;
	


	public CStories(SimulationData simData) {
		this.aplliedRulesIds = new ArrayList<Integer>();
		this.eventsMapForCurrentStory = new LinkedHashMap<Integer, IWireStorage>();
		for (int i = 0; i < simData.getSimulationArguments().getIterations(); i++) {
			this.eventsMapForCurrentStory.put(new Integer(i),new AbstractStorage(simData
					.getSimulationArguments().getStorifyMode(), i));
		}
	}

	public int getRuleIdAtStories(int index) {
		if (index >= aplliedRulesIds.size() || index < 0)
			return -1;
		return aplliedRulesIds.get(index);
	}

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

	
	public Map<Integer, IWireStorage> getEventsMapForCurrentStory() {
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
			addConnections2(graph, writer);
			writer.writeEndElement();
		}
		
		
	}

	private void addConnections2(StoriesGraphs graph, XMLStreamWriter writer)
	throws XMLStreamException {
		
		for (Connection connection : graph.getConnections2().getConnections()) {
			
			writer.writeStartElement("Connection");
			writer.writeAttribute("FromNode", Long.valueOf(connection.getTo())
					.toString());
			writer.writeAttribute("ToNode", Long.valueOf(connection.getFrom())
					.toString());
			writer.writeAttribute("Relation", "STRONG");
			writer.writeEndElement();
		}
		
	}

	private void addIntros(StoriesGraphs graph, XMLStreamWriter writer)
			throws XMLStreamException, StoryStorageException {
		for (Entry<Long, String> entry : graph.getIntroCCIdtoData().entrySet()) {
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
