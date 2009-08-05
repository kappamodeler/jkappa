package com.plectix.simulator.components.complex.subviews;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.influenceMap.AInfluenceMap;
import com.plectix.simulator.components.complex.influenceMap.withoutFuture.CInfluenceMapWithoutFuture;
import com.plectix.simulator.components.complex.subviews.base.AbstractClassSubViewBuilder;
import com.plectix.simulator.components.complex.subviews.base.AbstractionRule;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.components.complex.subviews.storage.SubViewsExeption;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CMainSubViews extends AbstractClassSubViewBuilder implements
		IAllSubViewsOfAllAgents {
	private Map<Integer, CAbstractAgent> agentNameIdToAgent;
	private List<AbstractionRule> abstractRules;
	private LinkedHashSet<Integer> deadRules;

	public CMainSubViews() {
		super();
		agentNameIdToAgent = new LinkedHashMap<Integer, CAbstractAgent>();
		abstractRules = new LinkedList<AbstractionRule>();
	}

	public void build(ISolution solution, List<CRule> rules) {
		Collection<CAgent> agents = prepareSolutionAgents(solution);
		fillModelMapOfAgents(agents, rules);
		fillAgentMap(agents);
		constructAbstractRules(rules);
		constructClasses(abstractRules, agentNameIdToAgent);

		AInfluenceMap wI = new CInfluenceMapWithoutFuture();
		// AInfluenceMap wI = new CInfluenceMapWithFuture();
		wI.initInfluenceMap(abstractRules, null, null, agentNameIdToAgent);

		fillingClasses(agents);
		initBoundRulesAndSubViews();
		try {
			// we use this original heuristic TODO
			constructAbstractContactMap(wI);
			constructAbstractContactMap(wI);
			constructAbstractContactMap(wI);
		} catch (SubViewsExeption e) {
			e.printStackTrace();
		}
	}

	private void initBoundRulesAndSubViews() {
		for (AbstractionRule rule : abstractRules)
			rule.initActionsToSubViews(subViewsMap);
	}

	private void fillingClasses(Collection<CAgent> agents) {
		for (List<ISubViews> subViewsList : subViewsMap.values()) {
			for (ISubViews subViews : subViewsList)
				subViews.fillingInitialState(agentNameIdToAgent, agents);
		}

	}



	public List<ISubViews> getAllSubViewsByTypeId(int type) {
		return subViewsMap.get(type);
	}

	public Iterator<Integer> getAllTypesIdOfAgents() {
		return agentNameIdToAgent.keySet().iterator();
	}

	public Map<Integer, CAbstractAgent> getAgentNameIdToAgent() {
		return agentNameIdToAgent;
	}



	// ==========================================================================
	// =====================

	private static Collection<CAgent> prepareSolutionAgents(ISolution solution) {
		Collection<CAgent> agents = new ArrayList<CAgent>();
		if (solution.getStraightStorage() != null) {
			agents.addAll(solution.getStraightStorage().getAgents());
		}
		if (solution.getSuperStorage() != null) {
			for (SuperSubstance substance : solution.getSuperStorage()
					.getComponents()) {
				agents.addAll(substance.getComponent().getAgents());
			}
		}
		return agents;

	}

	private void fillModelMapOfAgents(Collection<CAgent> agents,
			List<CRule> rules) {
		fillModelMapByAgentList(agents);

		for (CRule rule : rules) {
			for (IConnectedComponent cc : rule.getLeftHandSide())
				fillModelMapByAgentList(cc.getAgents());
			if (rule.getRightHandSide() != null)
				for (IConnectedComponent cc : rule.getRightHandSide())
					fillModelMapByAgentList(cc.getAgents());
		}
	}

	private void fillModelMapByAgentList(Collection<CAgent> listIn) {
		for (CAgent a : listIn) {
			CAbstractAgent modelAgent = agentNameIdToAgent.get(a.getNameId());
			if (modelAgent == null) {
				modelAgent = new CAbstractAgent(a.getNameId());
				agentNameIdToAgent.put(a.getNameId(), modelAgent);
			}

			for (CSite s : a.getSites()) {
				CAbstractSite as = new CAbstractSite(s);
				as.setParentAgent(modelAgent);
				modelAgent.addModelSite(as);
			}
		}
	}

	private void fillAgentMap(Collection<CAgent> agents) {

		for (CAgent agent : agents) {
			CAbstractAgent abstractAgent = new CAbstractAgent(agent,this.agentNameIdToAgent.get(agent.getNameId()));
		}
	}

	/**
	 * This method initializes abstract rules.<br>
	 * For <code>AGENT_OR_RULE</code> mode, creates abstract contact map.
	 * 
	 * @param rules
	 *            given rules
	 */
	private void constructAbstractRules(List<CRule> rules) {
		for (CRule rule : rules) {
			AbstractionRule abstractRule = new AbstractionRule(rule);
			abstractRules.add(abstractRule);
		}
	}

	private void constructClasses(List<AbstractionRule> abstractRules,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		List<AbstractionRule> list = new LinkedList<AbstractionRule>();
		for (AbstractionRule mr : abstractRules)
			list.add(mr);
		constructClassesSubViews(list, agentNameIdToAgent);

	}

	private void constructAbstractContactMap(AInfluenceMap wInfluence)
			throws SubViewsExeption {
		// RuleId
		Queue<Integer> activeRule = new LinkedList<Integer>();
		// RuleId -> isIncluded
		Map<Integer, Boolean> includedInQueue = new LinkedHashMap<Integer, Boolean>();
		// ruleId -> number in array
		Map<Integer, Integer> filter = new LinkedHashMap<Integer, Integer>();

		for (int i = 0; i < abstractRules.size(); i++) {
			AbstractionRule rule = abstractRules.get(i);
			activeRule.add(rule.getRuleId());
			includedInQueue.put(rule.getRuleId(), true);
			filter.put(rule.getRuleId(), i);
		}

		Integer ruleId;
		AbstractionRule rule;
		WrapperTwoSet activatedRule;
		LinkedHashSet<Integer> intersection = new LinkedHashSet<Integer>();
		// int ij=0;
		while (!activeRule.isEmpty()) {
			// System.out.println(ij++);
			ruleId = activeRule.poll();
			includedInQueue.put(ruleId, false);
			rule = abstractRules.get(filter.get(ruleId));
			activatedRule = rule.apply(agentNameIdToAgent, subViewsMap);

			intersection = intersect(activatedRule, wInfluence
					.getActivationByRule(ruleId));
			if (intersection != null)
				for (int j : intersection) {
					if (includedInQueue.get(j))
						continue;
					includedInQueue.put(j, true);
					activeRule.add(j);
				}
		}
	}

	private LinkedHashSet<Integer> intersect(WrapperTwoSet activatedRule,
			List<Integer> activationByRule) {
		if (activatedRule == null || activationByRule == null) {
			return null;
		}
		LinkedHashSet<Integer> answer = activatedRule.getSecond();
		for (Integer i : activatedRule.getFirst()) {
			if (activationByRule.contains(i)) {
				answer.add(i);
			}
		}

		return answer;
	}

	public Element createXML(Document doc) {
		Element reachables = doc.createElement("Reachables");
		reachables.setAttribute("Name", "Subviews");
		for (Integer agentId : agentNameIdToAgent.keySet()) {
			for (ISubViews subViews : subViewsMap.get(agentId)) {
				Element set = doc.createElement("Set");
				String agentName = ThreadLocalData.getNameDictionary().getName(
						agentId);
				set.setAttribute("Agent", agentName);
				Element tag = doc.createElement("Tag");
				String data = "Agent: " + agentName + " ; Sites: ";
				String sites = new String("");
				for (Integer siteId : subViews.getSubViewClass().getSitesId()) {
					if (sites.length() != 0)
						sites += ",";
					sites += ThreadLocalData.getNameDictionary()
							.getName(siteId);
				}
				data += sites + " ";
				tag.setAttribute("Data", data);
				set.appendChild(tag);

				for (CAbstractAgent agent : subViews.getAllSubViews()) {
					Element entry = doc.createElement("Entry");
					entry.setAttribute("Data", agent.toStringForXML());
					set.appendChild(entry);
				}
				reachables.appendChild(set);
			}
		}
		return reachables;
	}

	public Map<Integer, CAbstractAgent> getFullMapOfAgents() {
		return agentNameIdToAgent;
	}

	public void initDeadRules() {
		deadRules = new LinkedHashSet<Integer>();
		for (AbstractionRule rule : abstractRules)
			if (!rule.isApply())
				deadRules.add(rule.getRuleId());
	}

	public LinkedHashSet<Integer> getDeadRules() {
		return deadRules;
	}

	public List<AbstractionRule> getRules() {
		return abstractRules;
	}

	@Override
	public void createXML(XMLStreamWriter writer) throws XMLStreamException {
		// TODO Auto-generated method stub
		writer.writeStartElement("Reachables");
		writer.writeAttribute("Name", "Subviews");
		for (Integer agentId : agentNameIdToAgent.keySet()) {
			if(agentId == -1)
				continue;
			for (ISubViews subViews : subViewsMap.get(agentId)) {
				// Element set = doc.createElement("Set");
				writer.writeStartElement("Set");
				String agentName = ThreadLocalData.getNameDictionary().getName(
						agentId);
				writer.writeAttribute("Agent", agentName);
				// Element tag = doc.createElement("Tag");
				writer.writeStartElement("Tag");
				String data = "Agent: " + agentName + " ; Sites: ";
				String sites = new String("");
				for (Integer siteId : subViews.getSubViewClass().getSitesId()) {
					if (sites.length() != 0)
						sites += ",";
					sites += ThreadLocalData.getNameDictionary()
							.getName(siteId);
				}
				data += sites + " ";
				writer.writeAttribute("Data", data);
				// set.appendChild(tag);
				writer.writeEndElement();

				for (CAbstractAgent agent : subViews.getAllSubViews()) {
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
