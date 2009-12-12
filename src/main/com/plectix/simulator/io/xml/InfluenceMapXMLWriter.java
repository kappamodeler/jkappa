package com.plectix.simulator.io.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.io.SimulationDataOutputUtil;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMap;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMapEdge;

/*package*/ class InfluenceMapXMLWriter {
	// TODO move out
	private final InfluenceMap influenceMap;
	private final static String TYPE_NEGATIVE_MAP = "NEGATIVE";
	private final static String TYPE_POSITIVE_MAP = "POSITIVE";

	public InfluenceMapXMLWriter(InfluenceMap influenceMap) {
		this.influenceMap = influenceMap;
	}
	
	public final void write(OurXMLWriter writer, int rules,
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
				kappaSystem, true);

		/**
		 * add activation map
		 * */

		for (int i = rules - 1; i >= 0; i--) {
			Rule rule = kappaSystem.getRuleById(i);
			printMap(writer, TYPE_POSITIVE_MAP, rule, influenceMap.getActivationMap().get(Integer
					.valueOf(i)), influenceMap.getActivationMapObservables().get(Integer.valueOf(i)),
					rules);
		}
		if (isInhibitionMap) {
			for (int i = rules - 1; i >= 0; i--) {
				Rule rule = kappaSystem.getRuleById(i);
				printMap(writer, TYPE_NEGATIVE_MAP, rule, influenceMap.getInhibitionMap()
						.get(Integer.valueOf(i)), influenceMap.getInhibitionMapObservables().get(Integer
						.valueOf(i)), rules);
			}
		}
		writer.writeEndElement();
	}

	private static final void printMap(OurXMLWriter writer, String mapType,
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

	public static final void addRulesToXML(int rulesAndObsNumber,
			OurXMLWriter writer, int rules, boolean isOcamlStyleObsName,
			KappaSystem kappaSystem, boolean writeText) throws XMLStreamException {
		for (int i = rules - 1; i >= 0; i--) {
			Rule rule = kappaSystem.getRuleById(i);
			if(writeText)
				writer.writeStartElement("Node");
			else 
				writer.writeStartElement("Rule");
			writer.writeAttribute("Type", "RULE");
			if (rule.getName() != null) {
				if(writeText)
					writer.writeAttribute("Text", rule.getName());
				writer.writeAttribute("Name", rule.getName());
			} else {
				Integer ruleId = rule.getRuleId() + 1;
				if(writeText)
					writer.writeAttribute("Text", "%Auto_" + ruleId);
				writer.writeAttribute("Name", "%Auto_" + ruleId);
			}
			writer.writeAttribute("Id", Integer.toString(rulesAndObsNumber--));
			writer.writeAttribute("Data", SimulationDataOutputUtil.getData(rule,
					isOcamlStyleObsName));
			writer.writeEndElement();
		}
	}
}
