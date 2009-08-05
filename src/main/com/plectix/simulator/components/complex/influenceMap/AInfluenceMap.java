package com.plectix.simulator.components.complex.influenceMap;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.contactMap.CContactMap;
import com.plectix.simulator.components.complex.subviews.base.AbstractionRule;
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

	protected Map<Integer, List<AbstractionRule>> obsRules;

	public AInfluenceMap() {
		activationMap = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
		inhibitionMap = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
		activationMapObs = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
		inhibitionMapObs = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
	}

	public abstract void initInfluenceMap(List<AbstractionRule> rules,
			CObservables observables, CContactMap contactMap,
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

	public void createXML(XMLStreamWriter writer, int rules,
			List<IObservablesConnectedComponent> obsCCList,
			boolean isInhibitionMap, KappaSystem myKappaSystem,
			boolean isOcamlStyleObsName) throws XMLStreamException {
		writer.writeStartElement("InfluenceMap");

		int rulesAndObsNumber = obsCCList.size() + rules;
		/**
		 * add myKappaSystem.getObservables()
		 * */
		for (int i = obsCCList.size() - 1; i >= 0; i--) {
			IObservablesConnectedComponent obsCC = obsCCList.get(i);
			writer.writeStartElement("Node");
			writer.writeAttribute("Id", Integer.toString(rulesAndObsNumber--));
			writer.writeAttribute("Type", "OBSERVABLE");

			String obsName = obsCC.getName();

			if (obsName == null)
				obsName = obsCC.getLine();

			writer.writeAttribute("Text", '[' + obsName + ']');
			writer.writeAttribute("Data", obsCC.getLine());
			writer.writeAttribute("Name", '[' + obsName + ']');
			writer.writeEndElement();
		}

		/**
		 * add rules
		 * */
		addRulesToXML(rulesAndObsNumber, writer, rules, isOcamlStyleObsName,
				myKappaSystem);

		/**
		 * add activation map
		 * */

		for (int i = rules - 1; i >= 0; i--) {
			CRule rule = myKappaSystem.getRuleByID(i);
			printMap(writer, TYPE_POSITIVE_MAP, rule, activationMap.get(Integer
					.valueOf(i)), activationMapObs.get(Integer.valueOf(i)),
					rules);
		}
		if (isInhibitionMap) {
			for (int i = rules - 1; i >= 0; i--) {
				CRule rule = myKappaSystem.getRuleByID(i);
				printMap(writer, TYPE_NEGATIVE_MAP, rule, inhibitionMap
						.get(Integer.valueOf(i)), inhibitionMapObs.get(Integer
						.valueOf(i)), rules);
			}
		}
		writer.writeEndElement();
	}

	private final void printMap(XMLStreamWriter writer, String mapType,
			CRule rule, List<InfluenceMapEdge> rulesToPrint,
			List<InfluenceMapEdge> obsToPrint, int allRules)
			throws XMLStreamException {
		int rulesNumber = allRules + 1;
		if (obsToPrint != null)
		for (int j = obsToPrint.size() - 1; j >= 0; j--) {
			writer.writeStartElement("Connection");
			writer.writeAttribute("FromNode", Integer
					.toString(rule.getRuleID() + 1));
			writer.writeAttribute("ToNode", Integer.toString(obsToPrint.get(j).getToRule() + rulesNumber));
			writer.writeAttribute("Relation", mapType);
			writer.writeEndElement();
		}
		
		if (rulesToPrint != null)
		for (int j = rulesToPrint.size() - 1; j >= 0; j--) {
			writer.writeStartElement("Connection");
			writer.writeAttribute("FromNode", Integer
					.toString(rule.getRuleID() + 1));
			writer.writeAttribute("ToNode", Integer.toString(rulesToPrint
					.get(j).getToRule() + 1));
			// .getRuleID() + 1));
			writer.writeAttribute("Relation", mapType);
			writer.writeEndElement();
		}
	}

	private static final void addRulesToXML(int rulesAndObsNumber,
			XMLStreamWriter writer, int rules, boolean isOcamlStyleObsName,
			KappaSystem myKappaSystem) throws XMLStreamException {
		for (int i = rules - 1; i >= 0; i--) {
			CRule rule = myKappaSystem.getRuleByID(i);
			writer.writeStartElement("Node");
			writer.writeAttribute("Type", "RULE");
			if (rule.getName() != null) {
				writer.writeAttribute("Text", rule.getName());
				writer.writeAttribute("Name", rule.getName());
			} else {
				Integer ruleId = rule.getRuleID() + 1;
				writer.writeAttribute("Text", "%Auto_" + ruleId);
				writer.writeAttribute("Name", "%Auto_" + ruleId);
//				writer.writeAttribute("Text", SimulationData.getData(rule,
//						isOcamlStyleObsName));
			}
			writer.writeAttribute("Id", Integer.toString(rulesAndObsNumber--));
			writer.writeAttribute("Data", SimulationData.getData(rule,
					isOcamlStyleObsName));
			writer.writeEndElement();
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
			if (activationMapObs.containsKey(rule.getRuleID()))
				for (InfluenceMapEdge edge : activationMapObs.get(rule
						.getRuleID())) {
					for (AbstractionRule obsRule : obsRules.get(edge.getToRule()))
						rule.addActivatedObs(obsRule.getObsConnectedComponent());
				}
			if (inhibitionMapObs.containsKey(rule.getRuleID()))
				for (InfluenceMapEdge edge : inhibitionMapObs.get(rule
						.getRuleID())) {
					for (AbstractionRule obsRule : obsRules.get(edge.getToRule()))
						rule.addinhibitedObs(obsRule.getObsConnectedComponent());
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
