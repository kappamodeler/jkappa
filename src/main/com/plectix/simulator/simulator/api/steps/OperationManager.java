package com.plectix.simulator.simulator.api.steps;

import java.util.LinkedHashMap;
import java.util.Map;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;

public class OperationManager {
	private Map<OperationType, AbstractOperation<?>> defaultOperations = new LinkedHashMap<OperationType, AbstractOperation<?>>();
	private final SimulationData simulationData;
	private boolean retryGettingKappaInput = true;
	
	public OperationManager(KappaSystem kappaSystem) {
		this.simulationData = kappaSystem.getSimulationData();
		defaultOperations.put(OperationType.KAPPA_FILE_COMPILATION, new KappaFileCompilationOperation(this.simulationData));
		defaultOperations.put(OperationType.KAPPA_MODEL_BUILDING, new KappaModelBuildingOperation(simulationData));
		defaultOperations.put(OperationType.INITIALIZATION, new SolutionInitializationOperation(this.simulationData));
		defaultOperations.put(OperationType.SUBVIEWS, new SubviewsComputationOperation(kappaSystem));
		defaultOperations.put(OperationType.LOCAL_VIEWS, new LocalViewsComputationOperation(this.simulationData));
	}
	
	private void retryKappaInputLoading() {
		if (!retryGettingKappaInput) {
			return;
		} else {
			retryGettingKappaInput = false;
		}
		
		SimulationArguments args = simulationData.getSimulationArguments();
		
		if (args.getInputFileName() != null) {
			defaultOperations.put(OperationType.KAPPA_FILE_LOADING, new KappaFileLoadingOperation(simulationData, args.getInputFileName()));
		} else if (args.getInputCharArray() != null) {
			defaultOperations.put(OperationType.KAPPA_FILE_LOADING, new KappaFileLoadingOperation(simulationData, args.getInputCharArray()));
		} else {
			// if kappa input cannot be found (i.e. simulator is not properly fed with arguments) we report error
			if (defaultOperations.get(OperationType.KAPPA_FILE_LOADING) == null) {
				defaultOperations.put(OperationType.KAPPA_FILE_LOADING, new ReportErrorOperation("Kappa input was not set"));	
			}
			
			// we'll keep refreshing until success. 
			retryGettingKappaInput = true;
		}
	}
	
	public <E> E perform(AbstractOperation<E> operation) throws Exception {
		if (operation.getType() == OperationType.KAPPA_MODEL_BUILDING) {
			retryKappaInputLoading();
		}
		
		if (operation.noNeedToPerform()) {
			return operation.retrievePreparedResult();
		}
	
		OperationType previousNecessaryOperation = operation.getType().getNecessaryOperation();
	
		if (previousNecessaryOperation != null) {
			this.perform(defaultOperations.get(previousNecessaryOperation));
		}
	
		return operation.perform();
	}
}
