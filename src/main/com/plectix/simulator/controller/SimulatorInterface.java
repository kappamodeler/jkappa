package com.plectix.simulator.controller;

public interface SimulatorInterface {
	
    /**
     * Returns the name of this Simulator
     * @return the name of the Simulator
     */
    public String getName();

    /**
     * @throws Exception 
     */
    public void run(SimulatorInputData simulatorInputData) throws Exception;

    /**
     * Returns the current simulator time
     * 
     * @return
     */
    public double getCurrentTime();
    
    /**
     * Returns the status of the simulation, including the latest data.
     * 
     * @return
     */
    public SimulatorStatusInterface getStatus();
    
    /**
     * Returns the results data of the simulation.
     * 
     * @return
     */
    public SimulatorResultsData getSimulatorResultsData();

}
