package com.plectix.simulator.components.complex.influenceMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.contactMap.CContactMap;
import com.plectix.simulator.components.complex.subviews.base.SubViewsRule;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;

public abstract class AInfluenceMap {
	private final static String TYPE_NEGATIVE_MAP = "NEGATIVE";
	private final static String TYPE_POSITIVE_MAP = "POSITIVE";

	protected Map<Integer, List<InfluenceMapEdge>> activationMap;
	protected Map<Integer, List<InfluenceMapEdge>> activationMapObs;
	protected Map<Integer, List<InfluenceMapEdge>> inhibitionMap;
	protected Map<Integer, List<InfluenceMapEdge>> inhibitionMapObs;

	public AInfluenceMap() {
		activationMap = new HashMap<Integer, List<InfluenceMapEdge>>();
		inhibitionMap = new HashMap<Integer, List<InfluenceMapEdge>>();
	}

	public abstract void initInfluenceMap(List<SubViewsRule> rules,
			CContactMap contactMap,
			Map<Integer, CAbstractAgent> agentNameIdToAgent);

	public List<Integer> getActivationByRule(Integer ruleId) {
		List<Integer> answer = new LinkedList<Integer>();
		List<InfluenceMapEdge> list = activationMap.get(ruleId);
		if (list == null) {
			return null;
		}
		for (InfluenceMapEdge iE : activationMap.get(ruleId)) {
			answer.add(iE.getToRule());
		}
		return answer;
	}

	public Element createXML(Document doc, int rules,
			List<IObservablesConnectedComponent> obsCCList,
			boolean isInhibitionMap, KappaSystem myKappaSystem,
			boolean isOcamlStyleObsName) {
		Element influenceMapXML = doc.createElement("InfluenceMap");

		int rulesAndObsNumber = obsCCList.size() + rules;
		/**
		 * add myKappaSystem.getObservables()
		 * */
		for (int i = obsCCList.size() - 1; i >= 0; i--) {
			IObservablesConnectedComponent obsCC = obsCCList.get(i);
			Element node = doc.createElement("Node");
			node.setAttribute("Id", Integer.toString(rulesAndObsNumber--));
			node.setAttribute("Type", "OBSERVABLE");

			String obsName = obsCC.getName();

			if (obsName == null)
				obsName = obsCC.getLine();

			node.setAttribute("Text", '[' + obsName + ']');
			node.setAttribute("Data", obsCC.getLine());
			node.setAttribute("Name", '[' + obsName + ']');
			influenceMapXML.appendChild(node);
		}

		/**
		 * add rules
		 * */
		addRulesToXML(influenceMapXML, rulesAndObsNumber, doc, rules,
				isOcamlStyleObsName, myKappaSystem);

		/**
		 * add activation map
		 * */

		for (int i = rules - 1; i >= 0; i--) {
			CRule rule = myKappaSystem.getRuleByID(i);
			// printMap(doc, TYPE_POSITIVE_MAP, influenceMapXML, rule,
			// rule.getActivatedRuleForXMLOutput(),
			printMap(doc, TYPE_POSITIVE_MAP, influenceMapXML, rule,
					activationMap.get(Integer.valueOf(i)), rule
							.getActivatedObservableForXMLOutput(), rules);
		}
		if (isInhibitionMap) {
			for (int i = rules - 1; i >= 0; i--) {
				CRule rule = myKappaSystem.getRuleByID(i);
				// printMap(doc, TYPE_NEGATIVE_MAP, influenceMapXML, rule,
				// rule.getInhibitedRule(),
				printMap(doc, TYPE_NEGATIVE_MAP, influenceMapXML, rule,
						inhibitionMap.get(Integer.valueOf(i)), rule
								.getInhibitedObservable(), rules);
			}
		}

		return influenceMapXML;
	}

	private final void printMap(Document doc, String mapType,
			Element influenceMap, CRule rule,
			List<InfluenceMapEdge> rulesToPrint,
			List<IObservablesConnectedComponent> obsToPrint, int allRules) {
		int rulesNumber = allRules + 1;
		// for (int j = obsToPrint.size() - 1; j >= 0; j--) {
		// Element node = doc.createElement("Connection");
		// node.setAttribute("FromNode", Integer
		// .toString(rule.getRuleID() + 1));
		// node.setAttribute("ToNode", Integer.toString(obsToPrint.get(j)
		// .getId()
		// + rulesNumber));
		// node.setAttribute("Relation", mapType);
		// influenceMap.appendChild(node);
		// }

		// for (int j = rulesToPrint.size() - 1; j >= 0; j--) {
		if (rulesToPrint == null)
			return;
		for (int j = rulesToPrint.size() - 1; j >= 0; j--) {
			Element node = doc.createElement("Connection");
			node.setAttribute("FromNode", Integer
					.toString(rule.getRuleID() + 1));
			node.setAttribute("ToNode", Integer.toString(rulesToPrint.get(j)
					.getToRule() + 1));
			// .getRuleID() + 1));
			node.setAttribute("Relation", mapType);
			influenceMap.appendChild(node);
		}

	}

	private final void addRulesToXML(Element influenceMap,
			int rulesAndObsNumber, Document doc, int rules,
			boolean isOcamlStyleObsName, KappaSystem myKappaSystem) {
		for (int i = rules - 1; i >= 0; i--) {
			Element node = null;
			CRule rule = myKappaSystem.getRuleByID(i);
			node = doc.createElement("Node");
			node.setAttribute("Type", "RULE");
			node.setAttribute("Text", rule.getName());
			node.setAttribute("Id", Integer.toString(rulesAndObsNumber--));
			node.setAttribute("Data", SimulationData.getData(rule,
					isOcamlStyleObsName));
			node.setAttribute("Name", rule.getName());
			influenceMap.appendChild(node);
		}
	}

	public void fillingActivatedInhibitedRules(List<CRule> rules,
			KappaSystem kappaSystem, CObservables observables) {
		for (CRule rule : rules) {
			if (activationMap.containsKey(rule.getRuleID()))
				for (InfluenceMapEdge edge : activationMap
						.get(rule.getRuleID())) {
					CRule ruleAdd = kappaSystem.getRuleByID(edge.getToRule());
					rule.addActivatedRule(ruleAdd);
				}
			for(IObservablesConnectedComponent obs : observables.getConnectedComponentList()){
				rule.addinhibitedObs(obs);
				rule.addActivatedObs(obs);
			}
			if (inhibitionMap.containsKey(rule.getRuleID()))
				for (InfluenceMapEdge edge : inhibitionMap
						.get(rule.getRuleID())) {
					CRule ruleAdd = kappaSystem.getRuleByID(edge.getToRule());
					rule.addinhibitedRule(ruleAdd);
				}
		}
	}
}
