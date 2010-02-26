/**
 * 
 */
package com.plectix.simulator.simulator;

public interface ExperimentListener {

	/**
	 * Called before a new simulation run is started. 
	 * The class implementing this interface can get the simulation input
	 * data from the simulator and make changes.
	 * 
	 * @param runNo the run number the simulator is going to start 
	 * @param simulator
	 */
	public void startingRun(int runNo, Simulator simulator);
	
	/**
	 * Called after a simulation run has ended.
	 * The class implementing this interface can get the simulation output
	 * data from the simulator and process it.
	 * 
	 * @param runNo the run number the simulator has just finished
	 * @param simulator
	 */
	public void finishedRun(int runNo, Simulator simulator);

	/**
	 * Called when all simulation runs are ended.
	 * <br> <br>
	 * Note that the number of runs performed may be less than originally requested
	 * if an Exception or Error has occurred. 
	 * 
	 * @param runNo the number of runs the simulator has successfully completed
	 * @param simulator
	 */
	public void finishedAll(int runNo, Simulator simulator);
	
}