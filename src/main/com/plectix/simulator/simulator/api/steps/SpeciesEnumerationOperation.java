package com.plectix.simulator.simulator.api.steps;

import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.StaticAnalysisException;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.cycledetection.Detector;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.staticanalysis.speciesenumeration.SpeciesEnumeration;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;

public class SpeciesEnumerationOperation extends AbstractOperation<SpeciesEnumeration> {
	private final KappaSystem kappaSystem;
	
	public SpeciesEnumerationOperation(KappaSystem kappaSystem) {
		super(kappaSystem.getSimulationData(), OperationType.SPECIES_ENUMERATION);
		this.kappaSystem = kappaSystem;
		kappaSystem.getSimulationData().getSimulationArguments().setEnumerationOfSpecies(true);
	}
	
	protected SpeciesEnumeration performDry() throws StaticAnalysisException {
		ContactMap contactMap = kappaSystem.getContactMap();
		AllSubViewsOfAllAgentsInterface subViews = kappaSystem.getSubViews();
		LocalViewsMain localViews = kappaSystem.getLocalViews();
		
		SpeciesEnumeration enumerationOfSpecies = new SpeciesEnumeration(localViews.getLocalViews());
		
		List<AbstractAgent> list = new LinkedList<AbstractAgent>();
		list.addAll(contactMap.getAbstractSolution().getAgentNameToAgent().values());
		Detector detector = new Detector(subViews, list);
		if (detector.extractCycles().isEmpty()) {
			enumerationOfSpecies.enumerate();
		} else {
			enumerationOfSpecies.unbound();
		}
		
		kappaSystem.setEnumerationOfSpecies(enumerationOfSpecies);
		
		return enumerationOfSpecies;
	}

	@Override
	protected boolean noNeedToPerform() {
		return kappaSystem.getEnumerationOfSpecies() != null;
	}

	@Override
	protected SpeciesEnumeration retrievePreparedResult() {
		return kappaSystem.getEnumerationOfSpecies();
	}

}
