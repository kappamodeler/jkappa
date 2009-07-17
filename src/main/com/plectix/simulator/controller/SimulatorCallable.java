package com.plectix.simulator.controller;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

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
        try {
            simulator.getSimulatorResultsData().getSimulatorExitReport().setStartTimestamp(System.currentTimeMillis());
            simulator.run(simulatorInputData);
        } catch (Exception e) {
        	e.printStackTrace();
        	simulator.getSimulatorResultsData().getSimulatorExitReport().setException(e);
        	simulator.cleanUpAfterException(e);
        } finally {
        	simulator.getSimulatorResultsData().getSimulatorExitReport().setEndTimestamp(System.currentTimeMillis());
        	simulator.getSimulatorResultsData().getSimulatorExitReport().setSimulatorCallableId(id);
                   
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
