package com.plectix.simulator.staticanalysis.rulecompression;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.XMLSimulatorWriter;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;

public class RuleCompressionXMLWriter {
	private final Map<Integer, Rule> initialRulesMap = new LinkedHashMap<Integer, Rule>();
	private final MainSubViews qualitativeSubViews;
	private final Map<Integer, Integer> associationQualitativeMap 
			= new TreeMap<Integer, Integer>();
	private final Set<Rule> qualitativeRules = new LinkedHashSet<Rule>();
	private final Map<Integer, List<Integer>> associationQualitativeBackMap
				= new LinkedHashMap<Integer, List<Integer>>();
	private final CompressionResults results;
	
	public RuleCompressionXMLWriter(KappaSystem ks, CompressionResults results,
			MainSubViews qualitativeSubViews) {
		this.qualitativeSubViews = qualitativeSubViews;
		this.results = results;
		for (Rule rule : ks.getRules()) {
			this.initialRulesMap.put(rule.getRuleId(), rule);
		}
		for (Map.Entry<Rule, Rule> associationEntry : results.getAssociations()) {
			int idRealRule = associationEntry.getKey().getRuleId();
			int idCompressedRule = associationEntry.getValue().getRuleId();
			associationQualitativeMap.put(idRealRule, idCompressedRule);
			qualitativeRules.add(associationEntry.getValue());
			
			List<Integer> list = associationQualitativeBackMap.get(idCompressedRule);
			if (list == null) {
				list = new LinkedList<Integer>();
				associationQualitativeBackMap.put(idCompressedRule, list);
			}
			list.add(idRealRule);
		}
	}

	public void writeToXML(XMLSimulatorWriter xtw, boolean isOcamlStyleObsName)
			throws XMLStreamException {
		writeToXMLInitialRules(xtw,isOcamlStyleObsName);
		xtw.writeStartElement("RuleSet");
		xtw.writeAttribute("Name", this.results.getCompressionType() + " compression");
		writeToXMLQualitativeRules(xtw, isOcamlStyleObsName);
		writeToXMLAssociationQualitativeMap(xtw);

		xtw.writeEndElement();
	}
	
	private void writeToXMLInitialRules(XMLSimulatorWriter xtw, boolean isOcamlStyleObsName) throws XMLStreamException{
		xtw.writeStartElement("RuleSet");
		xtw.writeAttribute("Name", "Original");
		for(Rule rule : initialRulesMap.values()){
			//<Rule Id="1" Name="Rule1" Data="R(l,r),E(r)->R(l!0,r),E(r!0)" ForwardRate="1"/>
			xtw.writeStartElement("Rule");
			xtw.writeAttribute("Id", Integer.valueOf(rule.getRuleId()).toString());
			xtw.writeAttribute("ForwardRate", Double.valueOf(rule.getRate()).toString());
			String name = rule.getName();
			String data = SimulationData.getData(rule,isOcamlStyleObsName);
			if(name == null)
				name = data;
			xtw.writeAttribute("Name", name);
			xtw.writeAttribute("Data", data);
			
			xtw.writeEndElement();
		}
		xtw.writeEndElement();
	}
	
	private void writeToXMLAssociationQualitativeMap(XMLSimulatorWriter xtw) throws XMLStreamException{
		xtw.writeStartElement("Map");
		xtw.writeAttribute("FromSet", "Original");
		for(Map.Entry<Integer, Integer> entry : associationQualitativeMap.entrySet()){
			xtw.writeStartElement("Association");
			xtw.writeAttribute("FromRule", entry.getKey().toString());
			xtw.writeAttribute("ToRule", entry.getValue().toString());
			xtw.writeEndElement();
		}
		xtw.writeEndElement();
	}
	
	
	private void writeToXMLQualitativeRules(XMLSimulatorWriter xtw, boolean isOcamlStyleObsName)
	throws XMLStreamException {
		for (Rule rule : qualitativeRules) {
			xtw.writeStartElement("Rule");
			Integer id = rule.getRuleId();
			xtw.writeAttribute("Id", id.toString());
			if(qualitativeSubViews.getDeadRules().contains(id))
				xtw.writeAttribute("Data", "Cannot be applied");
			else{
				xtw.writeAttribute("ForwardRate", Double.valueOf(rule.getRate()).toString());
				xtw.writeAttribute("Data", SimulationData.getData(rule,isOcamlStyleObsName));
			}
			

			StringBuffer sb = new StringBuffer();
			List<Integer> list = associationQualitativeBackMap.get(id);
			int size = list.size();
			int counter = 1;
			for (Integer key : list) {
				Rule initRule = initialRulesMap.get(key);
				String name = initRule.getName();
				if (name != null)
					sb.append(name);
				else
					sb.append(SimulationData.getData(rule,isOcamlStyleObsName));
				if(counter != size)
					sb.append(",");
				counter++;
			}
			xtw.writeAttribute("Name", sb.toString());
			xtw.writeEndElement();
		}
	}
}
