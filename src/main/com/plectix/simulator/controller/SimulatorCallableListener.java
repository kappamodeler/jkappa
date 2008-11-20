package com.plectix.simulator.controller;

/**
 * 
 * @author ecemis
 */
public interface SimulatorCallableListener {
	
    /**
     * Called when the Simulator is done
     */
    public void finished(SimulatorCallable simulatorCallable);
}
