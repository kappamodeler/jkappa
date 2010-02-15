package com.plectix.simulator.xmlmap.rulecompression;

import java.util.LinkedHashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler {
	private Set<RuleTag> rules;
	private Set<Association> associations;

	private boolean isQuantitative = false;
	
	private boolean isMap;

	@Override
	public void startDocument() throws SAXException {
		rules = new LinkedHashSet<RuleTag>();
		associations = new LinkedHashSet<Association>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if ("RuleSet".equals(qName)) {
			if (attributes.getValue("Name").equals("Quantitative compression")) {
				isQuantitative = true;
				return;
			}
		}

		if (isQuantitative) {
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
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ("RuleSet".equals(qName)) {
			isQuantitative = false;
		}
		if ("Map".equals(qName)) {
			isMap = false;
		}
	}

	@Override
	public void endDocument() throws SAXException {
	}

	public Set<RuleTag> getRules() {
		return rules;
	}

	public Set<Association> getAssociations() {
		return associations;
	}

}
