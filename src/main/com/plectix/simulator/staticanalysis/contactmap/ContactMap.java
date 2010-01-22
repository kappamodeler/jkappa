package com.plectix.simulator.staticanalysis.contactmap;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.staticanalysis.subviews.base.AbstractionRule;
import com.plectix.simulator.staticanalysis.subviews.storage.SubViewsInterface;

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
		switch (getMode()) {
		case MODEL:
			// semantic contact map
			if (subViews != null && !subViews.isEmpty()) {
				Iterator<String> iterator = subViews.getAllTypesIdOfAgents();
				while (iterator.hasNext()) {
					List<SubViewsInterface> listOfSubViews = subViews
							.getAllSubViewsByType(iterator.next());
					abstractSolution.addData(listOfSubViews);
				}
				break;
			} else {
			//syntactic contact map
				abstractSolution.addAllRules(rules);
			}

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

	public final void fillContactMap(List<Rule> rules,
			AllSubViewsOfAllAgentsInterface subViews, KappaSystem kappaSystem) {
		if (!isInitialized) {
			setSimulationData(kappaSystem);
			initAbstractSolution();
			constructAbstractContactMapFromSubViews(subViews, rules);
		}
		isInitialized = true;
	}

	public final boolean isInitialized() {
		return isInitialized;
	}

	public ContactMapMode getMode() {
		return mode;
	}
}
