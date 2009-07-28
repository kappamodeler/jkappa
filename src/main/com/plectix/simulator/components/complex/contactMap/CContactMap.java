package com.plectix.simulator.components.complex.contactMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.BoundContactMap;

/**
 * Class implements contact map.
 * 
 * @author avokhmin
 * 
 */
public class CContactMap {
	private EContactMapMode mode = EContactMapMode.MODEL;
	private SimulationData simulationData;
	private CContactMapAbstractSolution abstractSolution;
	private CRule focusRule;
	private List<CContactMapAbstractRule> abstractRules;
	private List<CAbstractAgent> agentsFromFocusedRule;
	private boolean isInit = false;

	/**
	 * This method sets mode of create contact map.
	 * 
	 * @param mode
	 *            given mode
	 * @see EContactMapMode
	 */
	public void setMode(EContactMapMode mode) {
		this.mode = mode;
	}

	/**
	 * This method returns abstract solution.
	 * 
	 * @return abstract solution.
	 */
	public CContactMapAbstractSolution getAbstractSolution() {
		return abstractSolution;
	}

	/**
	 * This method sets simulation data.
	 * 
	 * @param simulationData
	 *            given simulation data
	 */
	public void setSimulationData(SimulationData simulationData) {
		this.simulationData = simulationData;
	}

	/**
	 * This method sets "focus rule".
	 * 
	 * @param focusRule
	 *            given rule
	 * @see EContactMapMode
	 */
	public void setFocusRule(CRule focusRule) {
		this.focusRule = focusRule;
	}

	/**
	 * Default constructor of contact map.
	 */
	public CContactMap() {
		agentsFromFocusedRule = new ArrayList<CAbstractAgent>();
	}

	/**
	 * This method creates abstract contact map (need uses with create contact
	 * map by <code>MODEL</code>).
	 */
	public void constructAbstractContactMap() {
		// TODO
		switch (mode) {
		case MODEL:
			boolean isEnd = false;
			while (!isEnd) {
				isEnd = true;
				for (CContactMapAbstractRule rule : abstractRules) {
					List<CAbstractAgent> newData = rule.getNewData();
					if (abstractSolution.addNewData(newData, rule)) {
						isEnd = false;
						// clear();
					}
				}
			}
			break;

		case AGENT_OR_RULE:
			// TODO add edges to contact map for agents from
			// agentNameIdToAgentsList which are not in focus;
			break;
		}

	}

	public void constructAbstractContactMapFromSubViews(
			IAllSubViewsOfAllAgents subViews) {
		// TODO
		switch (mode) {
		case MODEL:

			Iterator<Integer> iterator = subViews.getAllTypesIdOfAgents();
			while (iterator.hasNext()) {
				List<ISubViews> listOfSubViews = subViews
						.getAllSubViewsByTypeId(iterator.next());
				abstractSolution.addData(listOfSubViews);
			}

			// boolean isEnd = false;
			// while (!isEnd) {
			// isEnd = true;
			// for (CContactMapAbstractRule rule : abstractRules) {
			// List<CAbstractAgent> newData = rule.getNewData();
			// if (abstractSolution.addNewData(newData, rule)) {
			// isEnd = false;
			// // clear();
			// }
			// }
			// }
			break;

		case AGENT_OR_RULE:
			// TODO add edges to contact map for agents from
			// agentNameIdToAgentsList which are not in focus;
			break;
		}

	}

	/**
	 * This method initializes abstract solution.
	 */
	public final void initAbstractSolution() {
		this.abstractSolution = new CContactMapAbstractSolution(simulationData);
	}

	/**
	 * This method initializes abstract rules.<br>
	 * For <code>AGENT_OR_RULE</code> mode, creates abstract contact map.
	 * 
	 * @param rules
	 *            given rules
	 */
	public void constructAbstractRules(List<CRule> rules) {
		switch (mode) {
		case MODEL:
			List<CContactMapAbstractRule> listAbstractRules = new ArrayList<CContactMapAbstractRule>();
			for (CRule rule : rules) {
				CContactMapAbstractRule abstractRule = new CContactMapAbstractRule(
						abstractSolution, rule);
				abstractRule.initAbstractRule();
				listAbstractRules.add(abstractRule);
			}
			this.abstractRules = listAbstractRules;
			break;

		case AGENT_OR_RULE:
			// CContactMapAbstractRule abstractRule = fillFocusAgentsFromRule(
			fillFocusAgentsFromRule((CRule) this.focusRule,
					this.agentsFromFocusedRule);
			constructAbstractCard(rules, agentsFromFocusedRule);

			// TODO wait and remove!
			// Iterator<Integer> iterator = abstractSolution
			// .getAgentNameIdToAgent().keySet().iterator();
			// while (iterator.hasNext()) {
			// Integer key = iterator.next();
			// addAgentList.add(abstractSolution.getAgentNameIdToAgent().get(
			// key));
			// }
			List<CAbstractAgent> addAgentList = new ArrayList<CAbstractAgent>();
			addAgentList.addAll(abstractSolution.getAgentNameIdToAgent()
					.values());

			List<Integer> agentNameIdList = new ArrayList<Integer>();
			agentNameIdList.addAll(abstractSolution
					.getAgentNameIdToAgentsList().keySet());

			constructAbstractCard(rules, addAgentList);

			clearCard(agentNameIdList);
			break;
		}
	}

	/**
	 * Util method. Clears unnecessary information in contact map.
	 * 
	 * @param agentNameIdList
	 */
	private void clearCard(List<Integer> agentNameIdList) {
		List<Integer> namesOfAgentsToDelete = new ArrayList<Integer>();

		for (Integer key : abstractSolution.getAgentNameIdToAgentsList()
				.keySet()) {
			if (!agentNameIdList.contains(key))
				namesOfAgentsToDelete.add(key);
		}

		for (Integer i : namesOfAgentsToDelete) {
			abstractSolution.getAgentNameIdToAgentsList().remove(i);
			abstractSolution.getEdgesInContactMap().remove(i);
			abstractSolution.getAgentsInContactMap().remove(i);
		}
	}

	/**
	 * Util method. Construct abstract contact map by given rules for guiven
	 * agents
	 * 
	 * @param rules
	 *            given rules
	 * @param addAgentList
	 *            given agents
	 */
	private void constructAbstractCard(List<CRule> rules,
			List<CAbstractAgent> addAgentList) {
		for (CRule rule : rules) {
			List<CAbstractAgent> agentsFromRule = new ArrayList<CAbstractAgent>();
			fillAgentsFromRule(rule, agentsFromRule);
			for (CAbstractAgent agent : addAgentList)
				if (agent.includedInCollectionByName(agentsFromRule)) {
					abstractSolution.addAgentToAgentsMap(agent);
					abstractSolution.addAgentsBoundedWithFocusedAgent(agent,
							agentsFromRule);
					// break;
				}
		}
	}

	/**
	 * Util method. Fills given agents to given "focus rule"
	 * 
	 * @param rule
	 *            given rule
	 * @param agentsList
	 *            given agents
	 */
	private void fillFocusAgentsFromRule(CRule rule,
			List<CAbstractAgent> agentsList) {
		CContactMapAbstractRule abstractRule = new CContactMapAbstractRule(rule);
		abstractRule.initAbstractRule();
		List<CAbstractAgent> list = abstractRule.getFocusedAgents();
		addAgentsToListFromRule(list, agentsList);
		// return abstractRule;
	}

	/**
	 * Util method. Fills given agents to given rule.
	 * 
	 * @param rule
	 *            given rule
	 * @param agentsList
	 *            given agents
	 */
	private void fillAgentsFromRule(CRule rule, List<CAbstractAgent> agentsList) {
		CContactMapAbstractRule abstractRule = new CContactMapAbstractRule(rule);
		List<CAbstractAgent> agents = new ArrayList<CAbstractAgent>();
		if (rule.getLeftHandSide().get(0).isEmpty()) {
			agents = abstractRule.getLhsAgents();
			addAgentsToListFromRule(agents, agentsList);
		}
		agents = abstractRule.getRhsAgents();
		addAgentsToListFromRule(agents, agentsList);
	}

	/**
	 * Util method. Find sets difference and add to <b>agentsForAdding</b>
	 * 
	 * @param agents
	 *            given agents for checks
	 * @param agentsForAdding
	 *            given agents for adds
	 */
	private void addAgentsToListFromRule(List<CAbstractAgent> agents,
			List<CAbstractAgent> agentsForAdding) {
		for (CAbstractAgent agent : agents) {
			if (!agent.includedInCollection(agentsForAdding)) {
				agentsForAdding.add(agent);
			}
		}
	}

	public List<CAbstractAgent> getSideEffect(CAbstractSite mainSite) {
		// TODO
		List<CAbstractAgent> outList = new LinkedList<CAbstractAgent>();
		int mainAgentId = mainSite.getAgentLink().getNameId();
		int mainSiteId = mainSite.getNameId();
		Map<Integer, Map<Integer, List<CContactMapAbstractEdge>>> mapAll = abstractSolution
				.getEdgesInContactMap();
		if(!mapAll.containsKey(mainAgentId))
			return outList;
		if(!mapAll.get(mainAgentId).containsKey(mainSiteId))
			return outList;
		for (CContactMapAbstractEdge edge : mapAll.get(mainAgentId).get(
				mainSiteId)) {
			/**
			 * mainAgent(mainSite!linkSite.linkAgent)
			 */
			int linkSiteId = edge.getVertexToSiteNameID();
			int linkAgentId = edge.getVertexToAgentNameID();
			CAbstractAgent linkAgent = new CAbstractAgent(linkAgentId);
			CAbstractSite linkSite = new CAbstractSite(linkAgent, linkSiteId);
			linkAgent.addSite(linkSite);
			linkSite.getLinkState().setAgentNameID(mainAgentId);
			linkSite.getLinkState().setLinkSiteNameID(mainSiteId);
			linkSite.getLinkState().setStatusLink(CLinkStatus.BOUND);
			outList.add(linkAgent);
		}
		return outList;
	}

	public void fillingContactMap(List<CRule> rules,
			IAllSubViewsOfAllAgents subViews, SimulationData simulationData) {
		if (!isInit) {
			setSimulationData(simulationData);
			initAbstractSolution();
			constructAbstractRules(rules);
			constructAbstractContactMapFromSubViews(subViews);
		}
		isInit = true;
	}

	public void createXML(XMLStreamWriter writer) throws XMLStreamException {

		// Element contactMapElement = doc.createElement("ContactMap");
		writer.writeStartElement("ContactMap");
		writer.writeAttribute("Name", "High resolution");

		Map<Integer, Map<Integer, CContactMapChangedSite>> agentsInContactMap = getAbstractSolution()
				.getAgentsInContactMap();
		Map<Integer, Map<Integer, List<CContactMapAbstractEdge>>> bondsInContactMap = getAbstractSolution()
				.getEdgesInContactMap();
		List<Integer> agentIDWasRead = new ArrayList<Integer>();

		for (Map.Entry<Integer, Map<Integer, CContactMapChangedSite>> entry : agentsInContactMap
				.entrySet()) {

			// Element agent = doc.createElement("Agent");
			writer.writeStartElement("Agent");
			agentIDWasRead.add(entry.getKey());
			Map<Integer, CContactMapChangedSite> sitesMap = entry.getValue();
			Iterator<Map.Entry<Integer, CContactMapChangedSite>> siteIterator = sitesMap
					.entrySet().iterator();
			Map.Entry<Integer, CContactMapChangedSite> siteEntry = siteIterator
					.next();
			CContactMapChangedSite chSite = siteEntry.getValue();
			writer.writeAttribute("Name", chSite.getSite().getAgentLink()
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
		for (Map<Integer, List<CContactMapAbstractEdge>> edgesMap : bondsInContactMap
				.values()) {

			for (List<CContactMapAbstractEdge> edgesList : edgesMap.values()) {

				for (CContactMapAbstractEdge edge : edgesList) {
					// Element bond = doc.createElement("Bond");
					int vertexToSiteNameID = edge.getVertexToSiteNameID();
					int vertexToAgentNameID = edge.getVertexToAgentNameID();
					CAbstractSite vertexFrom = edge.getVertexFrom();
					BoundContactMap b = new BoundContactMap(vertexFrom
							.getAgentLink().getNameId(), ThreadLocalData
							.getNameDictionary().getId(vertexFrom.getName()),
							vertexToAgentNameID, vertexToSiteNameID);
					if (!b.includedInCollection(boundList))// (!boundList.contains(b))
						boundList.add(b);
					else
						continue;
					// if (!agentIDWasRead.contains(vertexToAgentNameID)) {
					writer.writeStartElement("Bond");
					writer.writeAttribute("FromAgent", vertexFrom
							.getAgentLink().getName());
					writer.writeAttribute("FromSite", vertexFrom.getName());
					writer.writeAttribute("ToAgent", ThreadLocalData
							.getNameDictionary().getName(vertexToAgentNameID));
					writer.writeAttribute("ToSite", ThreadLocalData
							.getNameDictionary().getName(vertexToSiteNameID));

					if (edge.getRules().size() != 0) {
						for (int ruleID : edge.getRules()) {
							// Element rule = doc.createElement("Rule");
							writer.writeStartElement("Rule");
							writer.writeAttribute("Id", Integer
									.toString(ruleID));
							// bond.appendChild(rule);
							writer.writeEndElement();
						}
					}
					// contactMapElement.appendChild(bond);
					writer.writeEndElement();
					// }
				}
			}
		}
		// simplxSession.appendChild(contactMapElement);
		writer.writeEndElement();
	}

	private void addSiteToContactMapAgent(CContactMapChangedSite site,
			XMLStreamWriter writer) throws XMLStreamException {
		boolean isDefaultSite = site.getSite().getNameId() == CSite.NO_INDEX;
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
					.isInternalState()));
			writer.writeAttribute("CanBeBound", Boolean.toString(site
					.isLinkState()));

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
	
	public boolean isInit(){
		return isInit;
	}
}
