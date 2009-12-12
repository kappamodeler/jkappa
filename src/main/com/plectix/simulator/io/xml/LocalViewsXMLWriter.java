package com.plectix.simulator.io.xml;

import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.util.NameDictionary;

/*package*/ class LocalViewsXMLWriter {
	private final LocalViewsMain localViews;
	
	public LocalViewsXMLWriter(LocalViewsMain localViews) {
		this.localViews = localViews;
	}
	
	public final void write(OurXMLWriter streamWriter) throws XMLStreamException {
		streamWriter.writeStartElement("Reachables");
		streamWriter.writeAttribute("Name", "Views");
		for (Map.Entry<String, List<AbstractAgent>> entry : localViews.getLocalViews().entrySet()) {
			String agentName = entry.getKey();
			if (NameDictionary.isDefaultAgentName(agentName))
				continue;
			List<AbstractAgent> list = entry.getValue();
			streamWriter.writeStartElement("Set");
			streamWriter.writeAttribute("Agent", agentName);
			for (AbstractAgent agent : list) {
				streamWriter.writeStartElement("Entry");
				streamWriter.writeAttribute("Data", agent.toStringForXML());
				streamWriter.writeEndElement();
			}
			streamWriter.writeEndElement();
		}
		streamWriter.writeEndElement();
	}
}
