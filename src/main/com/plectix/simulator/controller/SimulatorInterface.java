package com.plectix.simulator.controller;

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
     * Returns the current simulation time.
     * 
     * @return the current simulation time
     */
    public double getCurrentTime();
    
    /**
     * Returns the status of the simulation, including the latest data.
     * 
     * @return the status of the simulation
     */
    public SimulatorStatusInterface getStatus();
    
    /**
     * Returns the results data of the simulation.
     * 
     * @return the output of the simulation
     */
    public SimulatorResultsData getSimulatorResultsData();

}
