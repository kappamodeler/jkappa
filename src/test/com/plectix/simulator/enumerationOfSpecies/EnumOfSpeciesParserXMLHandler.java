package com.plectix.simulator.enumerationOfSpecies;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.plectix.simulator.enumerationOfSpecies.UtilsForParserXML.Entry;
import com.plectix.simulator.enumerationOfSpecies.UtilsForParserXML.Reachables;
import com.plectix.simulator.enumerationOfSpecies.UtilsForParserXML.Set;



public class EnumOfSpeciesParserXMLHandler extends DefaultHandler{
	
	private ArrayList<Reachables> reachables;
	private ArrayList<Set> sets;
	private ArrayList<Entry> entres;

	private boolean isReachablesElement = false;
	private boolean isSpeciesAttributeName = false;
	private boolean isSetElement = false;
	private boolean isEntryElement = false;


	
	
	public ArrayList<Reachables> getReachables() {
		return reachables;
	}

	public ArrayList<Set> getSets() {
		return sets;
	}

	public ArrayList<Entry> getEntres() {
		return entres;
	}

	@Override
	public void startDocument() throws SAXException {
		reachables = new ArrayList<Reachables>();
		sets = new ArrayList<Set>();
		entres = new ArrayList<Entry>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	
		
		isReachablesElement = "Reachables".equals(qName);
		
		if(isReachablesElement || !isSpeciesAttributeName) {
			
			isSpeciesAttributeName = "Species".equals(attributes.getValue("Name"));
		
		}
		
		if(isReachablesElement && isSpeciesAttributeName) {
			
			String nameValue = attributes.getValue("Name");
			String cardinalValue = attributes.getValue("Cardinal");
			reachables.add(new Reachables(nameValue, cardinalValue));
	
		}

		if (isSpeciesAttributeName) {
		
			isSetElement = "Set".equals(qName);
		
			isEntryElement = "Entry".equals(qName);
			
			if (isSetElement) {
				
				String nameValue = attributes.getValue("Name");
	
				sets.add(new Set(nameValue));
					
			}
			
			if (isEntryElement) {

				String typeValue = attributes.getValue("Type");
				String weightValue = attributes.getValue("Weight");
				String dataValue = attributes.getValue("Data");
				
				entres.add(new Entry(typeValue, weightValue, dataValue));
					
			}
			
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if ("Reachables".equals(qName)) {
			isReachablesElement = false;
			isSpeciesAttributeName = false;
		}
		
		if ("Set".equals(qName)) {
			isSetElement = false;
		}
		
		isEntryElement = false;

	}

}
