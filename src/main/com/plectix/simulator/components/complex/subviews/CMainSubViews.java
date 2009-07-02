package com.plectix.simulator.components.complex.subviews;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.base.AbstractClassSubViewBuilder;
import com.plectix.simulator.components.complex.subviews.base.SubViewsRule;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.components.complex.subviews.storage.SubViewsExeption;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.ThreadLocalData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CMainSubViews extends AbstractClassSubViewBuilder implements
		IAllSubViewsOfAllAgents {
	private Map<Integer, CAbstractAgent> agentNameIdToAgent;
	private List<SubViewsRule> abstractRules;

	public CMainSubViews() {
		super();
		agentNameIdToAgent = new HashMap<Integer, CAbstractAgent>();
		abstractRules = new LinkedList<SubViewsRule>();
	}

	public void build(ISolution solution, List<CRule> rules) {
		Collection<CAgent> agents = prepareSolutionAgents(solution);
		fillModelMapOfAgents(agents, rules);
		fillAgentMap(agents);
		constructAbstractRules(rules);
		constructClasses(abstractRules, agentNameIdToAgent);
		fillingClasses(agents);
		initBoundRulesAndSubViews();
		try {
			constructAbstractContactMap();
		} catch (SubViewsExeption e) {
			e.printStackTrace();
		}
	}

	private void initBoundRulesAndSubViews() {
		for (SubViewsRule rule : abstractRules)
			rule.initActionsToSubViews(subViewsMap);
	}

	private void fillingClasses(Collection<CAgent> agents) {
		for (List<ISubViews> subViewsList : subViewsMap.values()) {
			for (ISubViews subViews : subViewsList)
				subViews.fillingInitialState(agentNameIdToAgent, agents);
		}

	}

	public List<ISubViews> getAllSubViewsByType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ISubViews> getAllSubViewsByTypeId(int type) {
		return subViewsMap.get(type);
	}

	public Iterator<Integer> getAllTypesIdOfAgents() {
		return agentNameIdToAgent.keySet().iterator();
	}

	public List<String> getAllTypesOfAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISubViews getSubViewForRule(String typeOfAgent, CRule rule) {
		// TODO Auto-generated method stub
		return null;
	}

	//==========================================================================
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
				modelAgent = new CAbstractAgent(a);
				agentNameIdToAgent.put(a.getNameId(), modelAgent);
			}

			for (CSite s : a.getSites()) {
				CAbstractSite as = new CAbstractSite(s);
				as.setAgentLink(modelAgent);
				modelAgent.addModelSite(as);
			}
		}
	}

	private void fillAgentMap(Collection<CAgent> agents) {

		for (CAgent agent : agents) {
			CAbstractAgent abstractAgent = new CAbstractAgent(agent);
			abstractAgent.addSites(agent, this.agentNameIdToAgent);
			// addAgentToAgentsMap(abstractAgent);
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
			SubViewsRule abstractRule = new SubViewsRule(rule);
			abstractRules.add(abstractRule);
		}
	}

	private void constructClasses(List<SubViewsRule> abstractRules,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		List<SubViewsRule> list = new LinkedList<SubViewsRule>();
		for (SubViewsRule mr : abstractRules)
			list.add(mr);
		constructClassesSubViews(list, agentNameIdToAgent);

	}

	/**
	 * This method creates abstract contact map (need uses with create contact
	 * map by <code>MODEL</code>).
	 * 
	 * @throws SubViewsExeption
	 */
	private void constructAbstractContactMap() throws SubViewsExeption {
		boolean isEnd = false;
		while (!isEnd) {
			isEnd = true;
			for (SubViewsRule rule : abstractRules) {
				if (rule.apply(agentNameIdToAgent, subViewsMap))
					isEnd = false;
			}
		}
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
				String sites = new String();
				for (Integer siteId : subViews.getSubViewClass().getSitesId()) {
					if (sites.length() != 0)
						sites = ",";
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
}
