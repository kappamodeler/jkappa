package com.plectix.simulator.controller;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.steps.DumpHelpOperation;
import com.plectix.simulator.simulator.api.steps.NoKappaInputException;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.util.Info.InfoType;

//**********************************************************************************************************
/**
 * 
 * @author ecemis
 */
public class SimulatorCallable implements Callable<SimulatorResultsData> {
    private static AtomicLong SIMULATOR_CALLABLE_COUNT = new AtomicLong(0);
    
    private long id = SIMULATOR_CALLABLE_COUNT.getAndIncrement();
    
    private SimulatorInterface simulator = null;
    private SimulatorCallableListener listener = null;
    private SimulatorInputData simulatorInputData = null;    
    
    //**********************************************************************************************************
    /**
     * Creates a SimulatorCallable
     * @param simulatorInputData 
     */
    public SimulatorCallable(SimulatorInterface simulatorInterface, 
    		SimulatorInputData simulatorInputData,
    		SimulatorCallableListener simulatorCallableListener) {

        if (simulatorInterface == null || simulatorInputData == null) {
        	throw new RuntimeException("Unexpected null objects");
        }

    	this.simulator = simulatorInterface;
    	this.listener = simulatorCallableListener;
    	this.simulatorInputData = simulatorInputData;
    }


    //**********************************************************************************************************
    /**
     * 
     */
	public SimulatorResultsData call() throws Exception {
		SimulatorCallableExitReport simulatorExitReport = simulator.getSimulatorResultsData().getSimulatorExitReport();
        try {
        	simulatorExitReport.setStartTimestamp(System.currentTimeMillis());
            simulator.run(simulatorInputData);
        } catch (NoKappaInputException e) {
        	SimulationData simulationData = simulator.getSimulationData();
        	simulationData.addInfo(InfoType.WARNING, e.getMessage());
			OperationManager manager = simulationData.getKappaSystem().getOperationManager();
			manager.performSequentially(new DumpHelpOperation(simulationData));
		} catch (Exception e) {
        	e.printStackTrace();
        	simulatorExitReport.setException(e);
        	simulator.cleanUpAfterException(e);
        } catch (OutOfMemoryError outOfMemoryError) {
        	outOfMemoryError.printStackTrace();
        	simulatorExitReport.setException(new Exception(outOfMemoryError));
        	System.err.println("Caught an OutOfMemoryError!");
        } finally {
        	simulatorExitReport.setEndTimestamp(System.currentTimeMillis());
        	simulatorExitReport.setSimulatorCallableId(id);
                   
        	if (listener != null) {
            	listener.finished(this);
        	}
        }
        
        return simulator.getSimulatorResultsData();
	}
    
    //**********************************************************************************************************
    //
    //                        GETTERS AND SETTERS
    //
    // 


	public final long getId() {
        return id;
    }

    public final SimulatorInterface getSimulator() {
        return simulator;
    }

    public final SimulatorResultsData getSimulatorResultsData() {
        return simulator.getSimulatorResultsData();
    }
    
    public final SimulatorCallableExitReport getSimulatorExitReport() {
    	return simulator.getSimulatorResultsData().getSimulatorExitReport();
    }


}
