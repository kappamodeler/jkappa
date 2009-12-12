package com.plectix.simulator.io.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.contactmap.ContactMapAbstractEdge;
import com.plectix.simulator.staticanalysis.contactmap.ContactMapChangedSite;
import com.plectix.simulator.util.BoundContactMap;

/*package*/ class ContactMapXMLWriter {
	private final ContactMap contactMap;
	
	public ContactMapXMLWriter(ContactMap contactMap) {
		this.contactMap = contactMap; 
	}
	
	public final void write(OurXMLWriter writer) throws XMLStreamException {
		// Element contactMapElement = doc.createElement("ContactMap");
		writer.writeStartElement("ContactMap");
		writer.writeAttribute("Name", "High resolution");

		Map<String, Map<String, ContactMapChangedSite>> agentsInContactMap = contactMap.getAbstractSolution()
				.getAgentsInContactMap();
		Map<String, Map<String, List<ContactMapAbstractEdge>>> bondsInContactMap = contactMap.getAbstractSolution()
				.getEdgesInContactMap();
		List<String> readAgentsNames = new ArrayList<String>();

		for (Map.Entry<String, Map<String, ContactMapChangedSite>> entry : agentsInContactMap
				.entrySet()) {

			// Element agent = doc.createElement("Agent");
			writer.writeStartElement("Agent");
			readAgentsNames.add(entry.getKey());
			Map<String, ContactMapChangedSite> sitesMap = entry.getValue();
			Iterator<Map.Entry<String, ContactMapChangedSite>> siteIterator = sitesMap
					.entrySet().iterator();
			Map.Entry<String, ContactMapChangedSite> siteEntry = siteIterator
					.next();
			ContactMapChangedSite chSite = siteEntry.getValue();
			writer.writeAttribute("Name", chSite.getSite().getParentAgent()
					.getName());

			// TODO
			addSiteToContactMapAgent(chSite, writer);

			while (siteIterator.hasNext()) {
				siteEntry = siteIterator.next();
				addSiteToContactMapAgent(siteEntry.getValue(), writer);
			}
			// contactMapElement.appendChild(agent);
			writer.writeEndElement();
		}

		List<BoundContactMap> boundList = new ArrayList<BoundContactMap>();
		for (Map<String, List<ContactMapAbstractEdge>> edgesMap : bondsInContactMap
				.values()) {

			for (List<ContactMapAbstractEdge> edgesList : edgesMap.values()) {

				for (ContactMapAbstractEdge edge : edgesList) {
					String vertexToSiteName = edge.getTargetVertexSiteName();
					String vertexToAgentName = edge.getTargetVertexAgentName();
					AbstractSite vertexFrom = edge.getSourceVertex();
					BoundContactMap boundContactMap = new BoundContactMap(
							vertexFrom.getParentAgent().getName(), vertexFrom
									.getName(), vertexToAgentName,
							vertexToSiteName);
					if (!boundContactMap.includedInCollection(boundList))// (!boundList.contains(b))
						boundList.add(boundContactMap);
					else
						continue;
					writer.writeStartElement("Bond");
					writer.writeAttribute("FromAgent", vertexFrom
							.getParentAgent().getName());
					writer.writeAttribute("FromSite", vertexFrom.getName());
					writer.writeAttribute("ToAgent", vertexToAgentName);
					writer.writeAttribute("ToSite", vertexToSiteName);

					if (edge.getRules().size() != 0) {
						for (int ruleID : edge.getRules()) {
							writer.writeStartElement("Rule");
							writer.writeAttribute("Id", Integer
									.toString(ruleID));
							writer.writeEndElement();
						}
					}
					writer.writeEndElement();
					// }
				}
			}
		}
		writer.writeEndElement();
	}

	private final void addSiteToContactMapAgent(ContactMapChangedSite site,
			OurXMLWriter writer) throws XMLStreamException {
		boolean isDefaultSite = site.getSite().hasDefaultName();
		if (isDefaultSite) {
			for (Integer ruleID : site.getUsedRuleIDs()) {
				if (isDefaultSite) {
					writer.writeStartElement("Rule");
					writer.writeAttribute("Id", Integer.toString(ruleID));
					writer.writeEndElement();
				}
			}
		} else {
			writer.writeStartElement("Site");
			writer.writeAttribute("Name", site.getSite().getName());
			writer.writeAttribute("CanChangeState", Boolean.toString(site
					.hasInternalState()));
			writer.writeAttribute("CanBeBound", Boolean.toString(site
					.hasLinkState()));

			for (Integer ruleID : site.getUsedRuleIDs()) {
				if (isDefaultSite) {
					writer.writeStartElement("Rule");
					writer.writeAttribute("Id", Integer.toString(ruleID));
					writer.writeEndElement();
				}
			}
			writer.writeEndElement();
		}
	}

}
