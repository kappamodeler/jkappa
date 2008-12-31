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
     * 
     * @return
     */
    public double getCurrentTime();
    
    /**
     * 
     * @return
     */
    public SimulatorResultsData getSimulatorResultsData();

}
