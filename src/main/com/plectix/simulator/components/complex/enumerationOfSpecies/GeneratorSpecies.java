package com.plectix.simulator.components.complex.enumerationOfSpecies;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;

public class GeneratorSpecies {
	private Map<Integer, List<CAbstractAgent>> localViews = new LinkedHashMap<Integer, List<CAbstractAgent>>();
	private Map<String, Species> species = new LinkedHashMap<String, Species>();
	private boolean unbounded = false;

	public Map<String, Species> getSpecies() {
		return species;
	}

	public GeneratorSpecies(Map<Integer, List<CAbstractAgent>> localViews) {

		this.localViews = localViews;
	}

	public void enumerate() {
		Map<Integer, List<CAbstractAgent>> availableListOfView = new LinkedHashMap<Integer, List<CAbstractAgent>>();
		for (Integer i : localViews.keySet()) {
			List<CAbstractAgent> newlist = new LinkedList<CAbstractAgent>();
			newlist = localViews.get(i);
			availableListOfView.put(i, newlist);
			for (CAbstractAgent view : newlist) {
				buildSpeciesFromRoot(availableListOfView, view);
			}
		}
	}

	private void buildSpeciesFromRoot(
			Map<Integer, List<CAbstractAgent>> availableListOfViews,
			CAbstractAgent root) {
		Species first = new Species(availableListOfViews, root);
		Stack<Species> children = new Stack<Species>();
		children.add(first);

		// TODO stack -> map<String,Species>
		while (!children.isEmpty()) {
			Species initiative = children.pop();
			if (initiative.isComplete()) {
				if (species.get(initiative.getHashCode()) == null) {
					species.put(initiative.getHashCode(), initiative);
				}
			} else {
				List<Species> list = initiative.propagate();
				if (list != null)
					children.addAll(list);
			}

		}
	}

	public void writeToXML(XMLStreamWriter xtw) throws XMLStreamException {
		xtw.writeStartElement("Reachables");
		xtw.writeAttribute("Name", "Species");
		if (unbounded)
			xtw.writeAttribute("Cardinal", "Unbounded");
		else {
			Integer cardinal = Integer.valueOf(species.size());
			xtw.writeAttribute("Cardinal", cardinal.toString());
			if (cardinal != 0) {
				xtw.writeStartElement("Set");
				xtw.writeAttribute("Name", "All Species");
				for (Species spesie : species.values()) {
					xtw.writeStartElement("Entry");
					xtw.writeAttribute("Type", "Close");
					xtw.writeAttribute("Weight", "1");
					xtw.writeAttribute("Data", spesie.getHashCode());
					xtw.writeEndElement();
				}
				xtw.writeEndElement();
			}
		}
		xtw.writeEndElement();
	}

	public void setUnbounded() {
		unbounded = true;
	}

}
