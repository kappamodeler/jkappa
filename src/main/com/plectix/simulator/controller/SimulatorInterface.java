package com.plectix.simulator.controller;

import com.plectix.simulator.streaming.LiveData;

public interface SimulatorInterface {
	
    /**
     * Returns the name of this Simulator.
     * 
     * @return the name of the Simulator
     */
    public String getName();

    /**
     * @throws Exception 
     */
    public void run(SimulatorInputData simulatorInputData) throws Exception;
    
    /**
     * Returns the status of the simulation, including the latest data.
     * 
     * @return the status of the simulation
     */
    public SimulatorStatusInterface getStatus();
    
    /**
     * Returns the streaming live data
     * 
     * @param liveData
     * @return the live data
     */
    public LiveData getLiveData();
    
    /**
     * Returns the results data of the simulation.
     * 
     * @return the output of the simulation
     */
    public SimulatorResultsData getSimulatorResultsData();

    /**
     * Call-back function used when an Exception is thrown from the simulation thread
     * 
     * @param e the Exception thrown
     */
	public void cleanUpAfterException(Exception e);

}
