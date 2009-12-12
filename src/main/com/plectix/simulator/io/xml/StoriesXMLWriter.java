package com.plectix.simulator.io.xml;

import java.util.AbstractList;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import com.plectix.simulator.io.SimulationDataOutputUtil;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.stories.Stories;
import com.plectix.simulator.staticanalysis.stories.compressions.CompressionPassport;
import com.plectix.simulator.staticanalysis.stories.graphs.Connection;
import com.plectix.simulator.staticanalysis.stories.graphs.MergeStoriesGraphs;
import com.plectix.simulator.staticanalysis.stories.graphs.StoriesGraphs;
import com.plectix.simulator.staticanalysis.stories.graphs.UniqueGraph;
import com.plectix.simulator.staticanalysis.stories.storage.EventIteratorInterface;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;

public class StoriesXMLWriter {
	private Stories stories;
	
	public StoriesXMLWriter(Stories stories) {
		this.stories = stories;
	}
	
	public final void write(OurXMLWriter writer, KappaSystem kappaSystem,
			int numberOfIterations) throws XMLStreamException,
			StoryStorageException {

		MergeStoriesGraphs merging = new MergeStoriesGraphs(stories);
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

	private final void addConnections2(StoriesGraphs graph, OurXMLWriter writer)
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

	private final void addIntros(StoriesGraphs graph, OurXMLWriter writer)
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
			OurXMLWriter writer, KappaSystem kappaSystem)
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
		writer.writeAttribute("Data", SimulationDataOutputUtil.getData(rule, true));
		writer.writeAttribute("Depth", graph.getEventDepth(eventId) + "");
		writer.writeEndElement();

	}

	
}
