package com.plectix.simulator.xmlmap.rulecompression;

import java.util.LinkedHashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler {
	private Set<RuleTag> quantitativeRules;
	private Set<RuleTag> qualitativeRules;
	private Set<Association> quantitativeAssociations;
	private Set<Association> qualitativeAssociations;
	
	private enum CompressionType{
		NONE,
		QUANTITATIVE,
		QUALITATIVE
	}

	private CompressionType compressionType = CompressionType.NONE;
	private boolean isMap;

	@Override
	public void startDocument() throws SAXException {
		quantitativeRules = new LinkedHashSet<RuleTag>();
		qualitativeRules = new LinkedHashSet<RuleTag>();
		quantitativeAssociations = new LinkedHashSet<Association>();
		qualitativeAssociations = new LinkedHashSet<Association>();
	}
	
	private boolean checkCompression(String qName, Attributes attributes, String strCompression, CompressionType compressionType){
		if ("RuleSet".equals(qName)) {
			if (attributes.getValue("Name").equals(strCompression)){
				this.compressionType = compressionType;
				return true;
			}
		}
		return false;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if(checkCompression(qName, attributes, "Quantitative compression", CompressionType.QUANTITATIVE))
			return;

		if(checkCompression(qName, attributes, "Qualitative compression", CompressionType.QUALITATIVE))
			return;

		switch (compressionType) {
		case QUANTITATIVE:
			fillingSets(qName, attributes, quantitativeRules, quantitativeAssociations);
			break;
		case QUALITATIVE:
			fillingSets(qName, attributes, qualitativeRules, qualitativeAssociations);
			break;
		}
	}
	
	private void fillingSets(String qName, Attributes attributes, Set<RuleTag> rules, Set<Association> associations){
		if ("Rule".equals(qName)){// && attributes.getValue("Data").equals("Cannot be applied")) {
			rules.add(new RuleTag(Integer.parseInt(attributes
					.getValue("Id")), attributes.getValue("Data"),
					attributes.getValue("Name")));
			return;
		}

		if ("Map".equals(qName)) {
			if (attributes.getValue("FromSet").equals("Original")) {
				isMap = true;
				return;
			}
		}
		if (isMap) {
			if ("Association".equals(qName)) {
				associations.add(new Association(attributes
						.getValue("FromRule"), attributes
						.getValue("ToRule")));
				return;
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ("RuleSet".equals(qName)) {
			//isQuantitative = false;
			compressionType = CompressionType.NONE;
		}
		if ("Map".equals(qName)) {
			isMap = false;
		}
	}

	@Override
	public void endDocument() throws SAXException {
	}

	public Set<RuleTag> getQuantitativeRules() {
		return quantitativeRules;
	}

	public Set<Association> getQuantitativeAssociations() {
		return quantitativeAssociations;
	}

	public Set<RuleTag> getQualitativeRules() {
		return qualitativeRules;
	}
	
	public Set<Association> getQualitativeAssociations() {
		return qualitativeAssociations;
	}

}
