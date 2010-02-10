package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.contactmap.ContactMapMode;

public class ContactMapComputationOperation extends AbstractOperation<ContactMap> {
	private final SimulationData simulationData;
	
	public ContactMapComputationOperation(SimulationData simulationData) {
		super(simulationData, initiateType(simulationData));	
		this.simulationData = simulationData;
	}

	private static OperationType initiateType(SimulationData simulationData) {
		if (simulationData.getKappaSystem().getContactMap().getMode() == ContactMapMode.SEMANTIC) {
			return OperationType.MODEL_CONTACT_MAP;
		} else {
			return OperationType.NON_MODEL_CONTACT_MAP;
		}
	}
	
	protected ContactMap performDry() {
		simulationData.getSimulationArguments().setSimulationType(SimulationType.CONTACT_MAP);
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		ContactMap contactMap = kappaSystem.getContactMap();
		contactMap.fillContactMap(kappaSystem.getRules(), kappaSystem.getSubViews(), kappaSystem);
		simulationData.getKappaSystem().getState().refreshSimulationType(SimulationType.CONTACT_MAP);
		return contactMap;
	}

	@Override
	protected boolean noNeedToPerform() {
		return simulationData.getKappaSystem().getContactMap().isInitialized();
	}

	@Override
	protected ContactMap retrievePreparedResult() {
		return simulationData.getKappaSystem().getContactMap();
	}

}
