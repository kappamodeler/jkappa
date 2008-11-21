package com.plectix.simulator.controller;

public interface SimulatorInterface extends Cloneable {
	
    /**
     * Returns the name of this Simulator
     * @return the name of the Simulator
     */
    public String getName();
    
    /**
     * @return
     */
    public SimulatorInterface clone();

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
