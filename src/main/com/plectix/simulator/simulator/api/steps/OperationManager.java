package com.plectix.simulator.simulator.api.steps;

import java.util.LinkedHashMap;
import java.util.Map;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.util.Info.InfoType;

public class OperationManager {
	private static Map<OperationType, AbstractOperation<?>> defaultOperations = new LinkedHashMap<OperationType, AbstractOperation<?>>();
	private final SimulationData simulationData;
	private boolean retryGettingKappaInput = true;
	
	public OperationManager(KappaSystem kappaSystem) {
		this.simulationData = kappaSystem.getSimulationData();
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
		
		if (args.getInputFilename() != null) {
			defaultOperations.put(OperationType.KAPPA_FILE_LOADING, new KappaFileLoadingOperation(simulationData, args.getInputFilename()));
		} else if (args.getInputCharArray() != null) {
			defaultOperations.put(OperationType.KAPPA_FILE_LOADING, new KappaFileLoadingOperation(simulationData, args.getInputCharArray()));
		} else {
			// if kappa input cannot be found (i.e. simulator is not properly fed with arguments) we report error
			if (defaultOperations.get(OperationType.KAPPA_FILE_LOADING) != null) {
				defaultOperations.put(OperationType.KAPPA_FILE_LOADING, new ReportErrorOperation("Kappa input cannot be found"));	
			}
			
			// we'll keep refreshing until success. 
			retryGettingKappaInput = true;
		}
	}
	
	private void performDefaultOperation(OperationType type) throws Exception {
		OperationType previousNecessaryOperation = type.getNecessaryOperation();
		if (type == OperationType.KAPPA_FILE_COMPILATION) {
			// previousNecessaryOperation is KAPPA_FILE_LOADING
			retryKappaInputLoading();
			this.perform(defaultOperations.get(previousNecessaryOperation));
			/*
			 *  if some kappaInput source was found, previous line would work fine
			 *  and simulationData would countain a pointer to corresponding KappaFile object,
			 *  so we may use simple constructor for KappaFileCompilationOperation
			 *  
			 */
			this.perform(new KappaFileCompilationOperation(simulationData, InfoType.DO_NOT_OUTPUT));
		} else {
			AbstractOperation<?> operation = defaultOperations.get(type);
			this.perform(operation);
		}
	}
	
	public <E> E perform(AbstractOperation<E> operation) throws Exception {
		retryKappaInputLoading();

		if (operation.noNeedToPerform()) {
			return operation.retrievePreparedResult();
		}
	
		OperationType previousNecessaryOperation = operation.getType().getNecessaryOperation();
		
		if (previousNecessaryOperation != null) {
			this.performDefaultOperation(previousNecessaryOperation);
		}
	
		return operation.perform();
	}
	
	/**
	 * This method doesn't try performing necessary previous operations
	 * @param <E>
	 * @param operation
	 * @return
	 * @throws Exception
	 */
	public <E> E performSequentially(AbstractOperation<E> operation) throws Exception {
		retryKappaInputLoading();

		if (operation.noNeedToPerform()) {
			return operation.retrievePreparedResult();
		}
	
		return operation.perform();
	}
}
