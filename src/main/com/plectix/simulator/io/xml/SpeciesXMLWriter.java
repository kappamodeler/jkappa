package com.plectix.simulator.io.xml;

import javax.xml.stream.XMLStreamException;

import com.plectix.simulator.staticanalysis.speciesenumeration.Species;
import com.plectix.simulator.staticanalysis.speciesenumeration.SpeciesEnumeration;

public class SpeciesXMLWriter {
	private final SpeciesEnumeration species;
	
	public SpeciesXMLWriter(SpeciesEnumeration species) {
		this.species = species;
	}
	
	public final void write(OurXMLWriter writer) throws XMLStreamException {
		writer.writeStartElement("Reachables");
		writer.writeAttribute("Name", "Species");
		if (species.isUnbounded())
			writer.writeAttribute("Cardinal", "Unbounded");
		else {
			Integer cardinal = Integer.valueOf(species.getSpecies().size());
			writer.writeAttribute("Cardinal", cardinal.toString());
			if (cardinal != 0) {
				writer.writeStartElement("Set");
				writer.writeAttribute("Name", "All Species");
				for (Species spesie : species.getSpecies().values()) {
					writer.writeStartElement("Entry");
					writer.writeAttribute("Type", "Close");
					writer.writeAttribute("Weight", "1");
					writer.writeAttribute("Data", spesie.getHashCode());
					writer.writeEndElement();
				}
				writer.writeEndElement();
			}
		}
		writer.writeEndElement();
	}

}
