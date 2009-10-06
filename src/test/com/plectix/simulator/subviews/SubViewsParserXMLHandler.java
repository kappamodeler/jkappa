package com.plectix.simulator.subviews;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.plectix.simulator.subviews.util.Entry;
import com.plectix.simulator.subviews.util.Set;
import com.plectix.simulator.subviews.util.Tag;

public class SubViewsParserXMLHandler extends DefaultHandler {

	private ArrayList<Set> sets;
	private ArrayList<Tag> tags;
	private ArrayList<Entry> entres;

	private boolean isReachablesElement = false;
	private boolean isSubViewsAttributeName = false;
	private boolean isSetElement = false;
	private boolean isTagElement = false;
	private boolean isEntryElement = false;

	public ArrayList<Set> getSets() {
		return sets;
	}

	public ArrayList<Tag> getTags() {
		return tags;
	}

	public ArrayList<Entry> getEntres() {
		return entres;
	}

	@Override
	public void startDocument() throws SAXException {
		sets = new ArrayList<Set>();
		tags = new ArrayList<Tag>();
		entres = new ArrayList<Entry>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		isReachablesElement = "Reachables".equals(qName);

		if (isReachablesElement || !isSubViewsAttributeName) {

			isSubViewsAttributeName = "Subviews".equals(attributes
					.getValue("Name"));
		}

		if (isSubViewsAttributeName) {

			isSetElement = "Set".equals(qName);

			isTagElement = "Tag".equals(qName);

			isEntryElement = "Entry".equals(qName);

			String valueAttribute;

			if (isSetElement) {

				valueAttribute = attributes.getValue("Agent");

				sets.add(new Set(valueAttribute));
			}

			if (isTagElement) {

				valueAttribute = attributes.getValue("Data");

				tags.add(new Tag(valueAttribute));
			}

			if (isEntryElement) {

				valueAttribute = attributes.getValue("Data");

				entres.add(new Entry(valueAttribute));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if ("Reachables".equals(qName)) {
			isReachablesElement = false;
			isSubViewsAttributeName = false;
		}

		if ("Set".equals(qName)) {
			isSetElement = false;
		}

		isTagElement = false;

		isEntryElement = false;

	}

}
