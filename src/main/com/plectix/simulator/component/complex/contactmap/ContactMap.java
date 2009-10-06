package com.plectix.simulator.component.complex.contactmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.plectix.simulator.component.LinkStatus;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.abstracting.AbstractSite;
import com.plectix.simulator.component.complex.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.component.complex.subviews.base.AbstractionRule;
import com.plectix.simulator.component.complex.subviews.storage.SubViewsInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.util.BoundContactMap;

/**
 * Class implements contact map.
 * 
 * @author avokhmin
 * 
 */
public final class ContactMap {
	private ContactMapMode mode = ContactMapMode.MODEL;
	private KappaSystem kappaSystem;
	private ContactMapAbstractSolution abstractSolution;
	private Rule focusRule;
	private boolean isInitialized = false;

	/**
	 * This method sets mode of create contact map.
	 * 
	 * @param newmode
	 *            given mode
	 * @see ContactMapMode
	 */
	public final void setMode(ContactMapMode newmode) {
		mode = newmode;
	}

	/**
	 * This method returns abstract solution.
	 * 
	 * @return abstract solution.
	 */
	public final ContactMapAbstractSolution getAbstractSolution() {
		return abstractSolution;
	}

	/**
	 * This method sets simulation data.
	 * 
	 * @param simulationData
	 *            given simulation data
	 */
	public final void setSimulationData(KappaSystem newkappaSystem) {
		kappaSystem = newkappaSystem;
	}

	/**
	 * This method sets "focus rule".
	 * 
	 * @param newfocusRule
	 *            given rule
	 * @see ContactMapMode
	 */
	public final void setFocusRule(Rule newfocusRule) {
		focusRule = newfocusRule;
	}

	public final void constructAbstractContactMapFromSubViews(
			AllSubViewsOfAllAgentsInterface subViews, List<Rule> rules) {
		switch (mode) {
		case MODEL:
			Iterator<String> iterator = subViews.getAllTypesIdOfAgents();
			while (iterator.hasNext()) {
				List<SubViewsInterface> listOfSubViews = subViews
						.getAllSubViewsByType(iterator.next());
				abstractSolution.addData(listOfSubViews);
			}
			break;

		case AGENT_OR_RULE:
			if (focusRule != null) {
				AbstractionRule abstractRule = new AbstractionRule(focusRule);
				Collection<AbstractAgent> agentsFromFocusedRule = abstractRule
						.getFocusedAgents();
				abstractSolution.constructAbstractCard(rules,
						agentsFromFocusedRule);

				List<String> agentNames = new LinkedList<String>();
				agentNames.addAll(abstractSolution.getAgentNameToAgentsList()
						.keySet());

				abstractSolution.constructAbstractCard(rules, null);
				abstractSolution.clearCard(agentNames);
				break;
			} else {
				Iterator<String> iterator1 = subViews.getAllTypesIdOfAgents();
				while (iterator1.hasNext()) {
					List<SubViewsInterface> listOfSubViews = subViews
							.getAllSubViewsByType(iterator1.next());
					abstractSolution.addData(listOfSubViews);
				}
				abstractSolution.addAllRules(rules);

			}
		}

	}

	/**
	 * This method initializes abstract solution.
	 */
	public final void initAbstractSolution() {
		abstractSolution = new ContactMapAbstractSolution(kappaSystem);
	}

	public final List<AbstractAgent> getSideEffect(AbstractSite mainSite) {
		List<AbstractAgent> outList = new LinkedList<AbstractAgent>();
		String mainAgentName = mainSite.getParentAgent().getName();
		String mainSiteName = mainSite.getName();
		Map<String, Map<String, List<ContactMapAbstractEdge>>> mapAll = abstractSolution
				.getEdgesInContactMap();
		if (!mapAll.containsKey(mainAgentName))
			return outList;
		if (!mapAll.get(mainAgentName).containsKey(mainSiteName))
			return outList;
		for (ContactMapAbstractEdge edge : mapAll.get(mainAgentName).get(
				mainSiteName)) {
			/**
			 * mainAgent(mainSite!linkSite.linkAgent)
			 */
			String linkSiteName = edge.getTargetVertexSiteName();
			String connectedAgentName = edge.getTargetVertexAgentName();
			AbstractAgent linkAgent = new AbstractAgent(connectedAgentName);
			AbstractSite linkSite = new AbstractSite(linkAgent, linkSiteName);
			linkAgent.addSite(linkSite);
			linkSite.getLinkState().setAgentName(mainAgentName);
			linkSite.getLinkState().setLinkSiteName(mainSiteName);
			linkSite.getLinkState().setStatusLink(LinkStatus.BOUND);
			outList.add(linkAgent);
		}
		return outList;
	}

	public final void fillingContactMap(List<Rule> rules,
			AllSubViewsOfAllAgentsInterface subViews, KappaSystem kappaSystem) {
		if (!isInitialized) {
			setSimulationData(kappaSystem);
			initAbstractSolution();
			constructAbstractContactMapFromSubViews(subViews, rules);
		}
		isInitialized = true;
	}

	public final void createXML(XMLStreamWriter writer)
			throws XMLStreamException {
		// Element contactMapElement = doc.createElement("ContactMap");
		writer.writeStartElement("ContactMap");
		writer.writeAttribute("Name", "High resolution");

		Map<String, Map<String, ContactMapChangedSite>> agentsInContactMap = getAbstractSolution()
				.getAgentsInContactMap();
		Map<String, Map<String, List<ContactMapAbstractEdge>>> bondsInContactMap = getAbstractSolution()
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
			XMLStreamWriter writer) throws XMLStreamException {
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

	public final boolean isInitialized() {
		return isInitialized;
	}
}
