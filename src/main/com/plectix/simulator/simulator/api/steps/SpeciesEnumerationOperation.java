package com.plectix.simulator.simulator.api.steps;

import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.cycledetection.Detector;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.staticanalysis.speciesenumeration.SpeciesEnumeration;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;

public class SpeciesEnumerationOperation extends AbstractOperation {

	public SpeciesEnumerationOperation() {
		super(OperationType.SPECIES_ENUMERATION);
	}
	
	public SpeciesEnumeration perform(KappaSystem kappaSystem) {
		ContactMap contactMap = kappaSystem.getContactMap();
		AllSubViewsOfAllAgentsInterface subViews = kappaSystem.getSubViews();
		LocalViewsMain localViews = kappaSystem.getLocalViews();
		
		SpeciesEnumeration enumerationOfSpecies = new SpeciesEnumeration(localViews
				.getLocalViews());
		List<AbstractAgent> list = new LinkedList<AbstractAgent>();
		list.addAll(contactMap.getAbstractSolution().getAgentNameToAgent().values());
		Detector detector = new Detector(subViews, list);
		if (detector.extractCycles().isEmpty()) {
			enumerationOfSpecies.enumerate();
		} else {
			enumerationOfSpecies.unbound();
		}
		
		return enumerationOfSpecies;
	}

}
