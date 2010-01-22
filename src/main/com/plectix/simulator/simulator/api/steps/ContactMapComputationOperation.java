package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;

public class ContactMapComputationOperation extends AbstractOperation {

	public ContactMapComputationOperation() {
		super(OperationType.CONTACT_MAP);
	}
	
	public ContactMap perform(SimulationData simulationData) {
		simulationData.getSimulationArguments().setSimulationType(SimulationType.CONTACT_MAP);
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		ContactMap contactMap = kappaSystem.getContactMap();
		contactMap.fillContactMap(kappaSystem.getRules(), kappaSystem.getSubViews(), kappaSystem);
		return contactMap;
	}

}
