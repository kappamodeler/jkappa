package com.plectix.simulator.component.complex.influencemap;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.plectix.simulator.component.Observables;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.contactmap.ContactMap;
import com.plectix.simulator.component.complex.subviews.base.AbstractionRule;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;

public abstract class InfluenceMap {
	private final static String TYPE_NEGATIVE_MAP = "NEGATIVE";
	private final static String TYPE_POSITIVE_MAP = "POSITIVE";

	private final Map<Integer, List<InfluenceMapEdge>> activationMap;
	private final Map<Integer, List<InfluenceMapEdge>> activationMapObservables;
	private final Map<Integer, List<InfluenceMapEdge>> inhibitionMap;
	private final Map<Integer, List<InfluenceMapEdge>> inhibitionMapObservables;
	private Map<Integer, List<AbstractionRule>> observbableRules;

	public InfluenceMap() {
		activationMap = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
		inhibitionMap = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
		activationMapObservables = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
		inhibitionMapObservables = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
	}
	
	public abstract void initInfluenceMap(List<AbstractionRule> rules,
			Observables observables, ContactMap contactMap,
			Map<String, AbstractAgent> agentNameToAgent);

	public final List<Integer> getActivationByRule(Integer ruleId) {
		List<Integer> answer = new LinkedList<Integer>();
		List<InfluenceMapEdge> list = activationMap.get(ruleId);
		if (list == null) {
			return null;
		}
		for (InfluenceMapEdge iE : activationMap.get(ruleId)) {
			answer.add(iE.getTargetRule());
		}
		return answer;
	}

	// TODO move out
	public final void createXML(XMLStreamWriter writer, int rules,
			List<ObservableConnectedComponentInterface> observableComponents,
			boolean isInhibitionMap, KappaSystem kappaSystem,
			boolean isOcamlStyleObsName) throws XMLStreamException {
		writer.writeStartElement("InfluenceMap");

		int rulesAndObsNumber = observableComponents.size() + rules;
		/**
		 * add kappaSystem.getObservables()
		 * */
		for (int i = observableComponents.size() - 1; i >= 0; i--) {
			ObservableConnectedComponentInterface obsCC = observableComponents.get(i);
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
				kappaSystem);

		/**
		 * add activation map
		 * */

		for (int i = rules - 1; i >= 0; i--) {
			Rule rule = kappaSystem.getRuleById(i);
			printMap(writer, TYPE_POSITIVE_MAP, rule, activationMap.get(Integer
					.valueOf(i)), activationMapObservables.get(Integer.valueOf(i)),
					rules);
		}
		if (isInhibitionMap) {
			for (int i = rules - 1; i >= 0; i--) {
				Rule rule = kappaSystem.getRuleById(i);
				printMap(writer, TYPE_NEGATIVE_MAP, rule, inhibitionMap
						.get(Integer.valueOf(i)), inhibitionMapObservables.get(Integer
						.valueOf(i)), rules);
			}
		}
		writer.writeEndElement();
	}

	private static final void printMap(XMLStreamWriter writer, String mapType,
			Rule rule, List<InfluenceMapEdge> rulesToPrint,
			List<InfluenceMapEdge> influenceMapEdges, int allRules)
			throws XMLStreamException {
		int rulesNumber = allRules + 1;
		if (influenceMapEdges != null)
		for (int j = influenceMapEdges.size() - 1; j >= 0; j--) {
			writer.writeStartElement("Connection");
			writer.writeAttribute("FromNode", Integer
					.toString(rule.getRuleId() + 1));
			writer.writeAttribute("ToNode", Integer.toString(influenceMapEdges.get(j).getTargetRule() + rulesNumber));
			writer.writeAttribute("Relation", mapType);
			writer.writeEndElement();
		}
		
		if (rulesToPrint != null)
		for (int j = rulesToPrint.size() - 1; j >= 0; j--) {
			writer.writeStartElement("Connection");
			writer.writeAttribute("FromNode", Integer
					.toString(rule.getRuleId() + 1));
			writer.writeAttribute("ToNode", Integer.toString(rulesToPrint
					.get(j).getTargetRule() + 1));
			// .getRuleID() + 1));
			writer.writeAttribute("Relation", mapType);
			writer.writeEndElement();
		}
	}

	private static final void addRulesToXML(int rulesAndObsNumber,
			XMLStreamWriter writer, int rules, boolean isOcamlStyleObsName,
			KappaSystem kappaSystem) throws XMLStreamException {
		for (int i = rules - 1; i >= 0; i--) {
			Rule rule = kappaSystem.getRuleById(i);
			writer.writeStartElement("Node");
			writer.writeAttribute("Type", "RULE");
			if (rule.getName() != null) {
				writer.writeAttribute("Text", rule.getName());
				writer.writeAttribute("Name", rule.getName());
			} else {
				Integer ruleId = rule.getRuleId() + 1;
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

	public final void fillActivatedInhibitedRules(List<Rule> rules,
			KappaSystem kappaSystem, Observables observables) {
		for (Rule rule : rules) {
			if (activationMap.containsKey(rule.getRuleId()))
				for (InfluenceMapEdge edge : activationMap
						.get(rule.getRuleId())) {
					Rule ruleAdd = kappaSystem.getRuleById(edge.getTargetRule());
					rule.addActivatedRule(ruleAdd);
				}
			if (activationMapObservables.containsKey(rule.getRuleId()))
				for (InfluenceMapEdge edge : activationMapObservables.get(rule
						.getRuleId())) {
					for (AbstractionRule obsRule : observbableRules.get(edge.getTargetRule()))
						rule.addActivatedObs(obsRule.getObservableComponent());
				}
			if (inhibitionMapObservables.containsKey(rule.getRuleId()))
				for (InfluenceMapEdge edge : inhibitionMapObservables.get(rule
						.getRuleId())) {
					for (AbstractionRule obsRule : observbableRules.get(edge.getTargetRule()))
						rule.addinhibitedObs(obsRule.getObservableComponent());
				}
			
			if (inhibitionMap.containsKey(rule.getRuleId()))
				for (InfluenceMapEdge edge : inhibitionMap
						.get(rule.getRuleId())) {
					Rule ruleAdd = kappaSystem.getRuleById(edge.getTargetRule());
					rule.addinhibitedRule(ruleAdd);
				}
		}
	}
	
	public final Map<Integer, List<InfluenceMapEdge>> getActivationMap() {
		return activationMap;
	}

	public final Map<Integer, List<InfluenceMapEdge>> getActivationMapObservables() {
		return activationMapObservables;
	}

	public final Map<Integer, List<InfluenceMapEdge>> getInhibitionMap() {
		return inhibitionMap;
	}

	public final Map<Integer, List<InfluenceMapEdge>> getInhibitionMapObservables() {
		return inhibitionMapObservables;
	}

	public final Map<Integer, List<AbstractionRule>> getObservbableRules() {
		return observbableRules;
	}

	public final void setObservbableRules(
			Map<Integer, List<AbstractionRule>> observbableRules) {
		this.observbableRules = observbableRules;
	}
}
