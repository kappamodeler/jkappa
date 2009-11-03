package com.plectix.simulator.staticanalysis.stories;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.XMLSimulatorWriter;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.stories.compressions.CompressionPassport;
import com.plectix.simulator.staticanalysis.stories.compressions.Compressor;
import com.plectix.simulator.staticanalysis.stories.graphs.Connection;
import com.plectix.simulator.staticanalysis.stories.graphs.MergeStoriesGraphs;
import com.plectix.simulator.staticanalysis.stories.graphs.StoriesGraphs;
import com.plectix.simulator.staticanalysis.stories.graphs.UniqueGraph;
import com.plectix.simulator.staticanalysis.stories.storage.AbstractStorage;
import com.plectix.simulator.staticanalysis.stories.storage.Event;
import com.plectix.simulator.staticanalysis.stories.storage.EventIteratorInterface;
import com.plectix.simulator.staticanalysis.stories.storage.StoriesAgentTypesStorage;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.staticanalysis.stories.storage.WireStorageInterface;

public final class Stories {

	private final List<Integer> observablesRulesIds;
	private final Map<Integer, WireStorageInterface> eventsMapForCurrentStory;
	private StoriesAgentTypesStorage storiesAgentTypesStorage;
	
	// may be non-compression, weak compression, strong compression
	private StoryCompressionMode storifyMode;

	public Stories(SimulationData simData) {
		this.observablesRulesIds = new ArrayList<Integer>();
		this.eventsMapForCurrentStory = new LinkedHashMap<Integer, WireStorageInterface>();
		storiesAgentTypesStorage = new StoriesAgentTypesStorage();
		storifyMode = simData.getSimulationArguments()
		.getStorifyMode();
		for (int i = 0; i < simData.getSimulationArguments().getIterations(); i++) {
			this.eventsMapForCurrentStory.put(new Integer(i),
					new AbstractStorage(i, storiesAgentTypesStorage));
		}
	}

	public final int getRuleIdAtStories(int index) {
		if (index >= observablesRulesIds.size() || index < 0)
			return -1;
		return observablesRulesIds.get(index);
	}

	/**
	 * param index ; root of cleaning
	 */
	public final void cleaningStory(int index) throws StoryStorageException {
		if (eventsMapForCurrentStory.get(index).isImportantStory()) {
			compress(eventsMapForCurrentStory.get(index));
		} else {
			// delete this story
			eventsMapForCurrentStory.get(index).clearList();
			storiesAgentTypesStorage
					.resetTypesOfAgents(eventsMapForCurrentStory.get(index)
							.getIteration());
		}
	}

	private void compress(WireStorageInterface wireStorageInterface) throws StoryStorageException {
		if (wireStorageInterface.observableEvent() != null && wireStorageInterface.initialEvent() != null) {
			Compressor compressor = new Compressor(wireStorageInterface);
			compressor.execute(storifyMode);

		}

		
	}

	public final void addToStories(List<Integer> ruleIDs) {
			observablesRulesIds.addAll(ruleIDs);
	}

	public final void addEventToStory(int index, Event eventContainer)
			throws StoryStorageException {
		eventsMapForCurrentStory.get(Integer.valueOf(index)).addEventContainer(
				eventContainer);
	}

	public final void addLastEventToStoryStorifyRule(int index,
			Event eventContainer, double currentTime)
			throws StoryStorageException {
		this.eventsMapForCurrentStory.get(Integer.valueOf(index))
				.addLastEventContainer(eventContainer, currentTime);
	}

	public final boolean checkRule(int ruleToBeChecked, int index) {
		return observablesRulesIds.contains(ruleToBeChecked);
	}

	public final Map<Integer, WireStorageInterface> getEventsMapForCurrentStory() {
		return eventsMapForCurrentStory;
	}

	public final void createXML(XMLSimulatorWriter writer, KappaSystem kappaSystem,
			int numberOfIterations) throws XMLStreamException,
			StoryStorageException {

		MergeStoriesGraphs merging = new MergeStoriesGraphs(this);
		merging.merge();

		AbstractList<UniqueGraph> list = merging.getListUniqueGraph();

		for (UniqueGraph uniqueGraph : list) {
			writer.writeStartElement("Story");
			writer.writeAttribute("Observable", kappaSystem.getRuleById(
					uniqueGraph.getGraph().getPassport().getStorage()
							.observableEvent().getRuleId()).getName());
			Double percentage = uniqueGraph.getPersent();
			writer.writeAttribute("Percentage", Double
					.toString(percentage * 100));
			writer.writeAttribute("Average", Double.toString(uniqueGraph
					.getAverageTime()));
			CompressionPassport passport = uniqueGraph.getGraph().getPassport();
			StoriesGraphs graph = uniqueGraph.getGraph();

			for (EventIteratorInterface eventIterator = passport
					.eventIterator(true); eventIterator.hasNext();) {
				long eventId = (long) eventIterator.next();
				if (eventId != -1)
					addNode(eventId, graph, writer, kappaSystem);
				else
					addIntros(graph, writer);
			}
			addConnections2(graph, writer);
			writer.writeEndElement();
		}

	}

	private final void addConnections2(StoriesGraphs graph, XMLSimulatorWriter writer)
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

	private final void addIntros(StoriesGraphs graph, XMLSimulatorWriter writer)
			throws XMLStreamException, StoryStorageException {
		for (Entry<Long, String> entry : graph.getIntroComponentIdtoData().entrySet()) {
			int depth = graph.getIntroDepth(entry.getKey());
			if (depth != -1) {
				writer.writeStartElement("Node");
				writer.writeAttribute("Id", Long.valueOf(entry.getKey())
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

	private final void addNode(long eventId, StoriesGraphs graph,
			XMLSimulatorWriter writer, KappaSystem kappaSystem)
			throws XMLStreamException, StoryStorageException {
		writer.writeStartElement("Node");
		writer.writeAttribute("Id", Long.valueOf(
				graph.getNodeIdByEventId(eventId)).toString());
		if (eventId != graph.getPassport().getStorage().observableEvent()
				.getStepId())
			writer.writeAttribute("Type", "RULE");
		else
			writer.writeAttribute("Type", "OBSERVABLE");
		Rule rule = kappaSystem.getRuleById(graph.getEventByStepId(eventId)
				.getRuleId());
		writer.writeAttribute("Text", rule.getName());
		writer.writeAttribute("Data", SimulationData.getData(rule, true));
		writer.writeAttribute("Depth", graph.getEventDepth(eventId) + "");
		writer.writeEndElement();

	}

	public final StoriesAgentTypesStorage getStoriesAgentTypesStorage() {
		return storiesAgentTypesStorage;
	}
}
