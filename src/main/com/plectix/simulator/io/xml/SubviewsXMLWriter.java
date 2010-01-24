package com.plectix.simulator.io.xml;

import javax.xml.stream.XMLStreamException;

import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.staticanalysis.subviews.storage.SubViewsInterface;
import com.plectix.simulator.util.NameDictionary;

class SubviewsXMLWriter {
private final AllSubViewsOfAllAgentsInterface subViewsInstance;
	
	public SubviewsXMLWriter(AllSubViewsOfAllAgentsInterface subViews) {
		this.subViewsInstance = subViews;
	}
	
	
	public final void write(OurXMLWriter writer) throws XMLStreamException {
		// TODO Auto-generated method stub
		writer.writeStartElement("Reachables");
		writer.writeAttribute("Name", "Subviews");
		for (String agentType : subViewsInstance.getAgentNameToAgent().keySet()) {
			if (NameDictionary.isDefaultAgentName(agentType))
				continue;
			for (SubViewsInterface subViews : this.subViewsInstance.getSubViews().get(agentType)) {
				// Element set = doc.createElement("Set");
				writer.writeStartElement("Set");
				writer.writeAttribute("Agent", agentType);
				// Element tag = doc.createElement("Tag");
				writer.writeStartElement("Tag");
				String data = "Agent: " + agentType + " ; Sites: ";
				String sites = new String("");
				for (String siteName : subViews.getSubViewClass().getSitesNames()) {
					if (sites.length() != 0)
						sites += ",";
					sites += siteName;
				}
				data += sites + " ";
				writer.writeAttribute("Data", data);
				// set.appendChild(tag);
				writer.writeEndElement();

				for (AbstractAgent agent : subViews.getAllSubViews()) {
					writer.writeStartElement("Entry");
					writer.writeAttribute("Data", agent.toStringForXML());
					writer.writeEndElement();
				}
				writer.writeEndElement();
			}
		}
		writer.writeEndElement();
	}
}
