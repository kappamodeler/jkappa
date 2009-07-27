package com.plectix.simulator.localViews;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.plectix.simulator.subViews.UtilsForParserXML.Entry;
import com.plectix.simulator.subViews.UtilsForParserXML.Set;
import com.plectix.simulator.subViews.UtilsForParserXML.Tag;

public class LocalViewsParserXMLHandler extends DefaultHandler {
	
	private ArrayList<Set> sets;
	private ArrayList<Entry> entres;

	private boolean isReachablesElement = false;
	private boolean isLocalViewsAttributeName = false;
	private boolean isSetElement = false;
	private boolean isEntryElement = false;


	public ArrayList<Set> getSets() {
		return sets;
	}

	public ArrayList<Entry> getEntres() {
		return entres;
	}

	@Override
	public void startDocument() throws SAXException {
		sets = new ArrayList<Set>();
		entres = new ArrayList<Entry>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		isReachablesElement = "Reachables".equals(qName);
		
		if(isReachablesElement || !isLocalViewsAttributeName) {
			
			isLocalViewsAttributeName = "Views".equals(attributes.getValue("Name"));
		}

		if (isLocalViewsAttributeName) {
		
			isSetElement = "Set".equals(qName);
			
			isEntryElement = "Entry".equals(qName);
			
			String valueAttribute;
			
			if (isSetElement) {
				
				valueAttribute = attributes.getValue("Agent");
				
				sets.add(new Set(valueAttribute));
			}
			
			if (isEntryElement) {
				
				valueAttribute = attributes.getValue("Data");
				
				entres.add(new Entry(valueAttribute));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if ("Reachables".equals(qName)) {
			isReachablesElement = false;
			isLocalViewsAttributeName = false;
		}
		
		if ("Set".equals(qName)) {
			isSetElement = false;
		}
		
		isEntryElement = false;

	}
	

}
