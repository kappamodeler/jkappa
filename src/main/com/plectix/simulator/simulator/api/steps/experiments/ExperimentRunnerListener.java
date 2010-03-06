package com.plectix.simulator.simulator.api.steps.experiments;

import com.plectix.simulator.simulator.Simulator;

public interface ExperimentRunnerListener {

	/**
	 * Called before a new experiment is started. 
	 * The class implementing this interface can get the simulation input
	 * data from the simulator and make changes.
	 * 
	 * @param experimentNo the experiment number the simulator is going to start 
	 * @param simulator
	 * @throws Exception 
	 */
	public void startingExperiment(int experimentNo, Simulator simulator) throws Exception;
	
	/**
	 * Called after a simulation run has ended.
	 * The class implementing this interface can get the simulation output
	 * data from the simulator and process it.
	 * 
	 * @param experimentNo the experiment number the simulator has just finished
	 * @param simulator
	 */
	public void finishedExperiment(int experimentNo, Simulator simulator);

	/**
	 * Called when all simulation experiments are ended.
	 * <br> <br>
	 * Note that the number of experiments performed may be less than originally requested
	 * if an Exception or Error has occurred. 
	 * 
	 * @param experimentNo the experiment of runs the simulator has successfully completed
	 * @param simulator
	 */
	public void finishedAllExperiments(int experimentNo, Simulator simulator);

}
